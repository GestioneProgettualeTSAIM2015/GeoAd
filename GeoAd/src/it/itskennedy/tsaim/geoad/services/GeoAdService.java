package it.itskennedy.tsaim.geoad.services;

import it.itskennedy.tsaim.geoad.core.ConnectionManager;
import it.itskennedy.tsaim.geoad.core.LocationManager;
import it.itskennedy.tsaim.geoad.core.LocationManager.LocationListener;
import it.itskennedy.tsaim.geoad.core.NotificationManager;
import it.itskennedy.tsaim.geoad.entity.LocationModel;
import it.itskennedy.tsaim.geoad.entity.Offer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.json.JSONArray;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;

import com.loopj.android.http.RequestParams;

public class GeoAdService extends Service implements LocationListener
{
	public static final String OFFER_ACTION = "offer_action";
	public static final String OFFER_DATA = "offer_data";
	public static final String NEAR_ACTION = "near_action";
	public static final String NEAR_DATA = "near_data";
	public static final String ASK_NEAR = "ask_near";
	
	public static final String NAME = "GeoAd Service";

	private static final int EARTH_RADIUS = 6371;
	private static final double LAT_SPLIT = 0.006;
	private static final float SPEED_THRESHOLD = 5; //m/s
	private static final int DISTANCE_THRESHOLD = 0; //ONLY FOR TEST
	private double LNG_SPLIT;

	private Location mPosition;
	private List<LocationModel> mNearLocations;
	private String mNearString;
	private Queue<Offer> mPendingOffer;

	@Override
	public void onCreate() 
	{
		HandlerThread thread = new HandlerThread(NAME, Process.THREAD_PRIORITY_BACKGROUND);
	  	thread.start();
	  
	  	LocationManager.get(this).addListener(this);

	  	mPendingOffer = new LinkedList<Offer>();
		mNearLocations = new ArrayList<LocationModel>();

	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		if(intent != null)
		{
			String vAction = intent.getAction();
			
			if(vAction != null)
			{
				if(vAction.equals(OFFER_ACTION))
				{
					Bundle vExtra = intent.getExtras();

					if(!vExtra.isEmpty())
					{
						String vJson = intent.getExtras().getString(OFFER_DATA);
						Offer vOffer = Offer.fromJSON(vJson);

						manageOffer(vOffer, true);
					}
				}
				else if(vAction.equals(ASK_NEAR))
				{
					broadcastNearLocation();
				}
			}
		}
		
	    return START_STICKY;
	}

	private void manageOffer(Offer vOffer, boolean aCanEnqueue)
	{
		LocationModel vSearch = findLocation(vOffer.getLocationId());
		
		if(vSearch != null)
		{
			NotificationManager.showOffer(GeoAdService.this, vOffer, vSearch);
			//TODO cadorin
			//getContentResolver().insert(URI, vOffer.getContentValues());
		}
		else
		{
			if(aCanEnqueue)
			{
				enqueueOffer(vOffer);
			}
		}
	}
	
	private void enqueueOffer(Offer aOffer)
	{
		mPendingOffer.add(aOffer);
	}
	
	private void manageOfferIfExist()
	{
		if(mPendingOffer.size() > 0)
		{
			while(!mPendingOffer.isEmpty())
			{
				manageOffer(mPendingOffer.remove(), false);
			}	
		}
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
			float[] vResult = new float[1];
			
			Location.distanceBetween(aLocation.getLatitude(), aLocation.getLongitude(),
					mPosition.getLatitude(), mPosition.getLongitude(), vResult);
			
			if(vResult[0] > DISTANCE_THRESHOLD)
			{
				mPosition = aLocation;
				updateServer(aLocation);	
			}
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

		ConnectionManager.obtain().post("locations/UpdatePosition", vParams, new ConnectionManager.JsonResponse()
		{
			@Override
			public void onResponse(boolean aResult, Object aResponse)
			{
				if(aResult && aResponse != null && aResponse instanceof JSONArray)
				{
					mNearLocations = LocationModel.getListFromJsonArray((JSONArray)aResponse);
					mNearString = aResponse.toString();
					
					broadcastNearLocation();
					
					manageOfferIfExist();	
				}
			}
		});
	}
	
	private void broadcastNearLocation() 
	{
		Intent vDispatchNear = new Intent();
		vDispatchNear.setAction(GeoAdService.NEAR_ACTION);
		vDispatchNear.putExtra(GeoAdService.NEAR_DATA, mNearString);
		sendBroadcast(vDispatchNear);
	}
}
