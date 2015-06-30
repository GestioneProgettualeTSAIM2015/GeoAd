package it.itskennedy.tsaim.geoad.core;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class LocationManager implements LocationListener, ConnectionCallbacks, OnConnectionFailedListener
{
	public interface LocationListener
	{
		void onLocationUpdated(Location aLocation);
	}

	private static final long METER_DISPLACEMENT = 50;

	ArrayList<LocationListener> mListeners;
	private LocationRequest mLocationRequest;
	private Context mContext;

	private static LocationManager mInstance;
	
	private GoogleApiClient mGoogleApiClient;

	private Location mPosition;

	public static LocationManager get(Context aContext)
	{
		if(mInstance == null)
		{
			mInstance = new LocationManager(aContext);
		}
		
		return mInstance;
	}
	
	private LocationManager(Context aContext)
	{
		mContext = aContext;

		if (servicesAvailable())
		{
			mLocationRequest = LocationRequest.create();
			mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
			
			mLocationRequest.setInterval(10000);
			//mLocationRequest.setSmallestDisplacement(METER_DISPLACEMENT);

			mGoogleApiClient = new GoogleApiClient.Builder(aContext).addApi(LocationServices.API).addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).build();
			mGoogleApiClient.connect();

		}
	}
	
	@Override
	public void onLocationChanged(Location location)
	{
		mPosition = location;
		
		if (mListeners != null)
		{
			for(int i = 0; i < mListeners.size(); ++i)
			{
				mListeners.get(i).onLocationUpdated(location);
			}
		}
	}
	
	public Location getActualLocation()
	{
		return mPosition;
	}

	@Override
	public void onConnected(Bundle dataBundle)
	{
		if (servicesAvailable())
		{
			LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
		}
	}

	@Override
	public void onConnectionSuspended(int i)
	{

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult)
	{

	}

	public void cancelGeolocation()
	{
		if (mGoogleApiClient.isConnected())
		{
			LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
		}
	}

	private boolean servicesAvailable()
	{
		return ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
	}

	public void addListener(LocationListener aListener)
	{
		if(mListeners == null)
		{
			mListeners = new ArrayList<LocationListener>();
		}

		mListeners.add(aListener);
	}

	public void removeListener(LocationListener aListner)
	{
		mListeners.remove(aListner);
	}
}