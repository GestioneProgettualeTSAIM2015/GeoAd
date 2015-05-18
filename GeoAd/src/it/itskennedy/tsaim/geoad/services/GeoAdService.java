package it.itskennedy.tsaim.geoad.services;

import it.itskennedy.tsaim.geoad.core.ConnectionManager;
import it.itskennedy.tsaim.geoad.core.LocationManager.LocationListener;
import it.itskennedy.tsaim.geoad.core.LocationManager;
import it.itskennedy.tsaim.geoad.core.NotificationManager;
import it.itskennedy.tsaim.geoad.entity.LocationModel;
import it.itskennedy.tsaim.geoad.entity.Offer;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;

import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class GeoAdService extends Service implements LocationListener
{
	public static final String OFFER_ACTION = "offer_action";
	public static final String OFFER_DATA = "offer_data";

	public static final String NAME = "GeoAd Service";

	private static final int EARTH_RADIUS = 6371;
	private static final double LAT_SPLIT = 0.003;
	private static final float SPEED_THRESHOLD = 5; //m/s
	private double LNG_SPLIT;

	private Location mPosition;
	private List<LocationModel> mNearLocations;

	private BroadcastReceiver mOfferReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Bundle vExtra = intent.getExtras();

			if(!vExtra.isEmpty())
			{
				String vJson = intent.getExtras().getString(OFFER_DATA);
				Offer vOffer = Offer.fromJSON(vJson);

				NotificationManager.showOffer(GeoAdService.this, vOffer, findLocation(vOffer.getLocationId()));

				//TODO cadorin
				//getContentResolver().insert(URI, vOffer.getContentValues());
			}
		}
	};

	@Override
	public void onCreate() 
	{
		HandlerThread thread = new HandlerThread(NAME, Process.THREAD_PRIORITY_BACKGROUND);
	  	thread.start();
	  
	  	LocationManager.get(this).addListener(this);

		mNearLocations = new ArrayList<LocationModel>();

		registerReceiver(mOfferReceiver, new IntentFilter(OFFER_ACTION));
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
	    return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) 
	{
	    return null;
	}

	@Override
	public void onDestroy()
	{
		unregisterReceiver(mOfferReceiver);
		LocationManager.get(this).removeListener(this);
		super.onDestroy();
	}

	@Override
	public void onLocationUpdated(Location aLocation) 
	{
		if(mPosition != null)
		{
			updateServer(aLocation);
		}
		else
		{
			updateLngSplit(aLocation);
			mPosition = aLocation;
		}
	}

	private LocationModel findLocation(int aLocationId)
	{
		for(int i = 0; i < mNearLocations.size(); ++i)
		{
			if(mNearLocations.get(i).getId() == aLocationId)
			{
				return mNearLocations.get(i);
			}
		}

		return null;
	}

	private void  updateLngSplit(Location aLocation)
	{
		double vParallelRadius = Math.cos(Math.toRadians(aLocation.getLatitude())) * EARTH_RADIUS;
		double vParallelLength = vParallelRadius * 2 * Math.PI;

		double vMeter = EARTH_RADIUS * 2 * Math.PI / 4 * LAT_SPLIT / 90;
		LNG_SPLIT = 360 * vMeter / vParallelLength;
	}

	private void updateServer(Location aLocation)
	{
		float vSpeed = aLocation.getSpeed();

		RequestParams vParams = new RequestParams();

		double vLat = LAT_SPLIT, vLng = LNG_SPLIT;

		if(vSpeed > SPEED_THRESHOLD)
		{
			vLat = LAT_SPLIT * 2;
			vLng = LNG_SPLIT * 2;
		}

		vParams.add("north_lat", (aLocation.getLatitude() + vLat) + "");
		vParams.add("west_lng", (aLocation.getLongitude() + vLng) + "");
		vParams.add("south_lat", (aLocation.getLatitude() - vLat) + "");
		vParams.add("east_lng", (aLocation.getLongitude() - vLng) + "");

		ConnectionManager.get(this).send("updatePosition", vParams, new ConnectionManager.JsonResponse()
		{
			@Override
			public void onResponse(boolean aResult, JSONArray aResponse)
			{
				if(aResult && aResponse != null)
				{
					mNearLocations = LocationModel.getListFromJson(aResponse);
				}
			}
		});
	}
}
