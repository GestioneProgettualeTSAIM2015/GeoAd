package it.itskennedy.tsaim.geoad.augmentedreality;

import it.itskennedy.tsaim.geoad.core.Engine;
import it.itskennedy.tsaim.geoad.core.LocationManager;
import it.itskennedy.tsaim.geoad.core.LocationManager.LocationListener;
import it.itskennedy.tsaim.geoad.entity.LocationModel;
import it.itskennedy.tsaim.geoad.services.GeoAdService;
import it.itskennedy.tsaim.geoad.services.GeoAdService.GeoAdBinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;

public class AugmentedRealityManager implements LocationListener 
{	
	private Location mActualLocation;
	private List<LocationModel> mNears;
	private Context mContext;

	private AugmentedRealityListener mListener;
	
	private GeoAdService mService;
	private ServiceConnection mServiceConnection = new ServiceConnection()
	{
		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			mService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			mService = ((GeoAdBinder) service).getService();
		}
	};
	
	public interface AugmentedRealityListener
	{
		void onNewNearLocations(List<LocationModel> aToDraw);
		void onNewPosition(Location aCurrentLocation);
		void tooLowAccuracy();
		void onUnreliableSensor();
	}
	
	public AugmentedRealityManager(Context aContext)
	{
		mContext = aContext;
		mNears = new ArrayList<LocationModel>();
		
		LocationManager.get(mContext).addListener(this);

		onResume();
	}

    public void onResume() 
	{    	
    	Intent vBinding = new Intent(mContext, GeoAdService.class);
		mContext.bindService(vBinding, mServiceConnection, Context.BIND_AUTO_CREATE);
    }
    
    public void onPause() 
	{
        if(mService != null)
        {
        	mContext.unbindService(mServiceConnection);
        	mService = null;
        }
    }

	@Override
	public void onLocationUpdated(Location aLocation)
	{
		mActualLocation = aLocation;
		
		if(mService != null)
		{
			mNears = mService.getNears();
		}
		
		if(mListener != null)
		{
			mListener.onNewPosition(aLocation);
			mListener.onNewNearLocations(mNears);
		}
	}
	
	public void setListener(AugmentedRealityListener aListener)
	{
		mListener = aListener;
	}
}
