package it.itskennedy.tsaim.geoad.core.argumentedreality;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;
import it.itskennedy.tsaim.geoad.core.Engine;
import it.itskennedy.tsaim.geoad.core.LocationManager;
import it.itskennedy.tsaim.geoad.core.LocationManager.LocationListener;
import it.itskennedy.tsaim.geoad.entity.LocationModel;
import it.itskennedy.tsaim.geoad.services.GeoAdService;

public class ArgumentedRealityManager implements LocationListener, SensorEventListener 
{
	private static final int MAX_CHANGE = 6;
	private static final int PITCH_THRESHOLD = 20;
	private static final int ROLL_THRESHOLD = 12;
	private static final int ANGLE_THRESHOLD = 50;
	private static final int ACCURACY_THRESHOLD = 25;
	
	private Location mActualLocation;
	private List<LocationModel> mNears;
	private Context mContext;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Sensor mMagnetometer;
	private float[] accelerometerValues;
	private float[] geomagneticMatrix;

	private int mLastDirection = -1;
	private int mLastPitch;
	private int mLastRoll;
	boolean firstReading = true;

	private ArgumentedRealityListener mListener;
	
	private BroadcastReceiver mNearReceiver = new BroadcastReceiver() 
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String vNearString = intent.getExtras().getString(GeoAdService.NEAR_DATA);
			try
			{
				if(vNearString != null)
				{
					mNears = LocationModel.getListFromJsonArray(new JSONArray(vNearString));
				}
				
				onSomethingChange();
			} 
			catch (JSONException e)
			{
				Log.e(Engine.APP_NAME, "Json Decode Error");
			}
		}
	};
	private boolean mTooLowAccuracy;
	
	public interface ArgumentedRealityListener
	{
		void onNewData(List<ArgumentedRealityLocation> aToDraw, int aPitch, int aRoll);
		void tooLowAccuracy();
		void onUnreliableSensor();
		void deviceNotPointed();
	}
	
	public ArgumentedRealityManager(Context aContext)
	{
		mContext = aContext;
		mNears = new ArrayList<LocationModel>();
		
		////////////////////////////////////////////////////////////////////////
		//TEST
		LocationManager.get(mContext).addListener(this);
		mNears.add(new LocationModel(1, "", "", "PROVA", 46, 13, "", ""));
		////////////////////////////////////////////////////////////////////////
		
		Intent vAskNear = new Intent(mContext, GeoAdService.class);
		vAskNear.setAction(GeoAdService.ASK_NEAR);
		mContext.startService(vAskNear);
		
		mSensorManager = (SensorManager) Engine.get().getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		onResume();
	}

    public void onResume() 
	{
    	mContext.registerReceiver(mNearReceiver, new IntentFilter(GeoAdService.NEAR_ACTION));
    	mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
    	mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }
    
    public void onPause() 
	{
        mSensorManager.unregisterListener(this);
        mContext.unregisterReceiver(mNearReceiver);
    }

	@Override
	public void onLocationUpdated(Location aLocation)
	{
		mActualLocation = aLocation;
		
		if(aLocation.getAccuracy() > ACCURACY_THRESHOLD)
		{
			mTooLowAccuracy = true;
			if(mListener != null)
			{
				mListener.tooLowAccuracy();
			}
		}
		else
		{
			mTooLowAccuracy = false;
			onSomethingChange();	
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{	
		if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
		{
			if(mListener != null)
			{
				mListener.onUnreliableSensor();	
			}
			return;
		}

		if(mTooLowAccuracy)
		{
			if(mListener != null)
			{
				mListener.tooLowAccuracy();
			}
		}
		else
		{
			switch (event.sensor.getType()) 
	        {
	        	case Sensor.TYPE_ACCELEROMETER:
	        	{
	        		accelerometerValues = event.values.clone();
	                break;
	        	}   
	        	case Sensor.TYPE_MAGNETIC_FIELD:
	        	{
	        		geomagneticMatrix = event.values.clone();
	                break;	
	        	}
	        }   

	        if (geomagneticMatrix != null && accelerometerValues != null && event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) 
	        {
	            float[] R = new float[16];
	            float[] I = new float[16];
	            float[] outR = new float[16];

	            SensorManager.getRotationMatrix(R, I, accelerometerValues, geomagneticMatrix);
	            SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X, SensorManager.AXIS_Z, outR);
	            float values[] = new float[4];
	            SensorManager.getOrientation(outR,values);

	            int vDirection = normalizeDegrees(filterChange((int)Math.toDegrees(values[0])));
	            mLastPitch = normalizeDegrees(Math.toDegrees(values[1]));
	            mLastRoll = normalizeDegrees(Math.toDegrees(values[2]));
	            
	            boolean vPitchControl = mLastPitch < PITCH_THRESHOLD || mLastPitch > 360 - PITCH_THRESHOLD;
	            boolean vRollControl = Math.abs(mLastRoll - 90) < ROLL_THRESHOLD || Math.abs(mLastRoll - 270) < ROLL_THRESHOLD;
	            
	            if(vPitchControl && vRollControl)
	            {
	            	if(vDirection != mLastDirection)
	                {
	                    mLastDirection = vDirection;
	                    onSomethingChange();
	                }
	            }
	            else
	            {
	            	if(mListener != null)
	            	{
	            		mListener.deviceNotPointed();
	            	}
	            }
	        }
		}
	}
	
	private int normalizeDegrees(double aRads)
	{
	    return (int)((aRads + 360) % 360);
	}

	private int filterChange(int newDir)
	{
	    newDir = normalizeDegrees(newDir);
	    
	    if(firstReading)
	    {
	        firstReading = false;
	        return newDir;
	    }       

	    int delta = newDir - mLastDirection;
	    int normalizedDelta = normalizeDegrees(delta);
	    int change = Math.min(Math.abs(delta), MAX_CHANGE);

	    if(normalizedDelta > 180)
	    {
	        change = -change;
	    }

	    return mLastDirection + change;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		//do nothing
	}
	
	private void onSomethingChange()
	{
		List<ArgumentedRealityLocation> vResult = new ArrayList<ArgumentedRealityLocation>();
		
		if(mActualLocation != null)
		{
			for(int i = 0; i < mNears.size(); ++i)
			{
				Location vActual = mNears.get(i).getLocation();
				
				int vBearingTo = normalizeDegrees(mActualLocation.bearingTo(vActual));
				int vAngleDifference = vBearingTo - mLastDirection;
				
				if(Math.abs(vAngleDifference) < ANGLE_THRESHOLD)
				{
					int vId = mNears.get(i).getId();
					String vName = mNears.get(i).getName();
					
					float vDistance = vActual.distanceTo(mActualLocation);
					
					ArgumentedRealityLocation vToAdd = new ArgumentedRealityLocation(vId, vName, vDistance, vAngleDifference);
					vResult.add(vToAdd);
				}
			}
		}
		
		if(mListener != null)
		{
			mListener.onNewData(vResult, mLastPitch, mLastRoll);
		}
	}
	
	public void setListener(ArgumentedRealityListener aListener)
	{
		mListener = aListener;
	}
}
