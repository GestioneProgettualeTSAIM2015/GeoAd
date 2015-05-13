package it.itskennedy.tsaim.geoad.services;

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

public class LocationManager implements LocationListener, ConnectionCallbacks, OnConnectionFailedListener
{
	public interface LocationListener
	{
		void onLocationUpdated(Location aLocation);
	}

	private static final long BACKGROUND_POLLING = 30 * 1000;
	private static final long ACTIVE_POLLING = 15 * 1000;

	LocationListener mListener;
	private LocationRequest mLocationRequest;
	private Context mContext;

	private static LocationManager mInstance;
	
	private GoogleApiClient mGoogleApiClient;

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
			mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			
			mLocationRequest.setInterval(BACKGROUND_POLLING);

			mGoogleApiClient = new GoogleApiClient.Builder(aContext).addApi(LocationServices.API).addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).build();
			mGoogleApiClient.connect();

		}
	}
	
	@Override
	public void onLocationChanged(Location location)
	{
		if (mListener != null)
		{	
			mListener.onLocationUpdated(location);
		}
	}

	@Override
	public void onConnected(Bundle dataBundle)
	{
		if (servicesAvailable())
		{
			LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
		}
	}
	
	public void updatePolling(boolean aIsInBackground)
	{
		if(mGoogleApiClient.isConnected())
		{
			LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
			
			if(aIsInBackground)
			{
				mLocationRequest.setInterval(BACKGROUND_POLLING);
			}
			else
			{
				mLocationRequest.setInterval(ACTIVE_POLLING);
			}
			
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

	public void setListener(LocationListener aListener)
	{
		mListener = aListener;
	}
}