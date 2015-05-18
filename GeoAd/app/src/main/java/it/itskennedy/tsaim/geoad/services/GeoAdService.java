package it.itskennedy.tsaim.geoad.services;

import it.itskennedy.tsaim.geoad.core.ConnectionManager;
import it.itskennedy.tsaim.geoad.core.Engine;
import it.itskennedy.tsaim.geoad.core.LocationManager.LocationListener;
import it.itskennedy.tsaim.geoad.core.LocationManager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

public class GeoAdService extends Service implements LocationListener
{
	public static final String LOCATION_ACTION = "location_action";

	public static final String NAME = "GeoAd Service";

	private static final int EARTH_RADIUS = 6371;
	private static final double LAT_SPLIT = 0.003;
	private static final float SPEED_THRESHOLD = 5; //m/s
	private double LNG_SPLIT;

	private long mLatSq;
	private long mLngSq;

	private Location mPosition;

	@Override
	public void onCreate() 
	{
		HandlerThread thread = new HandlerThread(NAME, Process.THREAD_PRIORITY_BACKGROUND);
	  	thread.start();
	  
	  	LocationManager.get(this).addListener(this);
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
			public void onResponse(boolean aResult, JSONObject aResponse)
			{
				if(aResult && aResponse != null)
				{
					//TODO
					//aggiorna content provider cadorin!
				}
			}
		});
	}
}
