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
	public interface IAugmentedReality {
		void onServiceReady();
	}
	
	private IAugmentedReality iListener;
	
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
			if (iListener != null)
				iListener.onServiceReady();
		}
	};
	
	public void setIAugmentedRealityListener(IAugmentedReality iListener) {
		this.iListener = iListener;
	}
	
	public List<LocationModel> getNears() 
	{
		if (mService == null) return null;
		return mService.getNears();
	}
	
	public Location getCurrentPosition() {
		if (mService == null) return null;
		return mService.getCurrentPosition();
	}
	
	public interface AugmentedRealityListener
	{
		void onNewPosition(Location aCurrentLocation, List<LocationModel> aToDraw);
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
			mListener.onNewPosition(aLocation, mNears);
		}
	}
	
	public void setListener(AugmentedRealityListener aListener)
	{
		mListener = aListener;
	}
}
