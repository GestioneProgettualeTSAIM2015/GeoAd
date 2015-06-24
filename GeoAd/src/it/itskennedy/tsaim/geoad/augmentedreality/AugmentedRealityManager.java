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
		
		
		mNears.add(new LocationModel(1, "", "", "roba 1", 46.0005, 13.0005, "Descrizione lunga lunga lunga ", "POI"));
		mNears.add(new LocationModel(2, "", "", "roba 2", 46, 13.0005, "Descrizione lunga lunga lungaDescrizione lunga lunga lungaDescrizione lunga lunga lunga", "POI"));
		mNears.add(new LocationModel(3, "", "", "roba 3", 45.9995, 13, "Descrizione lunga lunga lunga", "POI"));
		mNears.add(new LocationModel(4, "", "", "roba 4", 45.9995, 13.0005, "Descrizione lunga lunga lungaDescrizione lunga lunga lunga", "CA"));
		mNears.add(new LocationModel(5, "", "", "roba 5", 46, 12.9995, "Descrizione lunga lunga lunga", "POI"));
		mNears.add(new LocationModel(6, "", "", "roba 6", 46.0005, 12.9995, "Descrizione lunga lunga lunga", "CA"));
		mNears.add(new LocationModel(7, "", "", "roba 7", 46.0005, 13, "Descrizione lunga lunga lungaDescrizione lunga lunga lunga", "CA"));
		mNears.add(new LocationModel(8, "", "", "roba 8", 45.9995, 12.9995, "Descrizione lunga lunga lungaDescrizione lunga lunga lunga", "CA"));
		mNears.add(new LocationModel(9, "", "", "DUH", 46.01, 13.02, "Descrizione lunga lunga lunga", "CA"));
		
		
		LocationManager.get(mContext).addListener(this);

		onResume();
	}

    public void onResume() 
	{    	
//    	Intent vBinding = new Intent(mContext, GeoAdService.class);
//		mContext.bindService(vBinding, mServiceConnection, Context.BIND_AUTO_CREATE);
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
//			List<LocationModel> mOldNears = mNears;
////		TEST!!!!!!!
////		mNears = mService.getNears();
//		
//		if (!mNears.equals(mOldNears)) {
//			
//		}
		}
		
		////////////TEST
		if(mListener != null)
		{
			aLocation.setLatitude(46);
			aLocation.setLongitude(13);
			mListener.onNewPosition(aLocation);
			mListener.onNewNearLocations(mNears);

		}
	}
	
	public void setListener(AugmentedRealityListener aListener)
	{
		mListener = aListener;
	}
}
