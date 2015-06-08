package it.itskennedy.tsaim.geoad.services;

import it.itskennedy.tsaim.geoad.core.ConnectionManager;
import it.itskennedy.tsaim.geoad.core.ConnectionManager.JsonResponse;
import it.itskennedy.tsaim.geoad.core.LocationManager;
import it.itskennedy.tsaim.geoad.core.LocationManager.LocationListener;
import it.itskennedy.tsaim.geoad.core.NotificationManager;
import it.itskennedy.tsaim.geoad.entity.LocationModel;
import it.itskennedy.tsaim.geoad.entity.Offer;
import it.itskennedy.tsaim.geoad.localdb.DataOffersContentProvider;
import it.itskennedy.tsaim.geoad.widgets.OffersWidgetProvider;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.BaseColumns;

import com.loopj.android.http.RequestParams;

public class GeoAdService extends Service implements LocationListener
{
	public static final String NEW_OFFER = "new_offer_action";
	public static final String DELETE_OFFER = "delete_offer";
	
	public static final String NEW_LOCATION = "new_location_action";
	public static final String DELETE_LOCATION = "delete_location";
	
	public static final String DATA = "new_data";
	public static final String DELETE_ID = "delete_id";
	
	public static final String NAME = "GeoAd Service";

	private static final int EARTH_RADIUS = 6371;
	private static final double LAT_SPLIT = 0.007;
	private static final float SPEED_THRESHOLD = 5; //m/s
	private static final int DISTANCE_THRESHOLD = 0; //0 ONLY FOR TEST
	private double LNG_SPLIT;
	
	private Location mPosition;
	private List<LocationModel> mNearLocations;

	public interface GetLocationListener
	{
		public void onLoad(LocationModel aLocation);
	}
	
	@Override
	public void onCreate() 
	{
	  	LocationManager.get(this).addListener(this);
	  	
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
				if(vAction.equals(NEW_OFFER))
				{
					Bundle vExtra = intent.getExtras();
					if(!vExtra.isEmpty())
					{
						String vJson = intent.getExtras().getString(DATA);
						manageOffer(Offer.fromJSON(vJson));
						sendWidgetBroadcast();
					}
				}
				else if(vAction.equals(DELETE_OFFER))
				{
					int vToRemove = intent.getIntExtra(DELETE_ID, 0);
					getContentResolver().delete(DataOffersContentProvider.OFFERS_URI, BaseColumns._ID + " = " + vToRemove, null);
					sendWidgetBroadcast();
				}
				else if(vAction.equals(NEW_LOCATION))
				{
					Bundle vExtra = intent.getExtras();
					if(!vExtra.isEmpty())
					{
						String vJson = intent.getExtras().getString(DATA);
						addIfNotExist(LocationModel.fromJSON(vJson));
					}
				}
				else if(vAction.equals(DELETE_LOCATION))
				{
					removeIfExist(intent.getIntExtra(DELETE_ID, 0));
				}
			}
		}
		
	    return START_STICKY;
	}
	
	private void addIfNotExist(LocationModel aLocation)
	{
		for(int i = 0; i < mNearLocations.size(); ++i)
		{
			if(mNearLocations.get(i).getId() == aLocation.getId())
			{
				return;
			}
		}
		
		mNearLocations.add(aLocation);
	}
	
	private void removeIfExist(int aId)
	{
		for(int i = 0; i < mNearLocations.size(); ++i)
		{
			if(mNearLocations.get(i).getId() == aId)
			{
				mNearLocations.remove(i);
				return;
			}
		}
	}

	private void manageOffer(final Offer vOffer)
	{
		getLocationById(vOffer.getLocationId(), new GetLocationListener()
		{	
			@Override
			public void onLoad(LocationModel aLocation)
			{
				if(aLocation != null)
				{
					NotificationManager.showOffer(GeoAdService.this, vOffer);
					getContentResolver().insert(DataOffersContentProvider.OFFERS_URI, vOffer.getContentValues());
				}
			}
		});
	}
	
	@Override
	public IBinder onBind(Intent intent) 
	{
	    return new GeoAdBinder();
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
		Offer vTest = new Offer(1, "Offerta Prova", 559, "NOME", "Venite qua blabla", 123456, 789101);
		ContentValues vV = vTest.getContentValues();
		getContentResolver().insert(DataOffersContentProvider.OFFERS_URI, vV);
		
		sendWidgetBroadcast();
		
		if(mPosition != null)
		{
			float[] vResult = new float[1];
			
			Location.distanceBetween(aLocation.getLatitude(), aLocation.getLongitude(),
					mPosition.getLatitude(), mPosition.getLongitude(), vResult);
			
			if(vResult[0] > DISTANCE_THRESHOLD)
			{
				mPosition = aLocation;
				updateServer();	
			}
		}
		else
		{
			updateLngSplit(aLocation);
			mPosition = aLocation;
			updateServer();
		}
	}

	private void updateLngSplit(Location aLocation)
	{
		double vParallelRadius = Math.cos(Math.toRadians(aLocation.getLatitude())) * EARTH_RADIUS;
		double vParallelLength = vParallelRadius * 2 * Math.PI;

		double vMeter = EARTH_RADIUS * 2 * Math.PI / 4 * LAT_SPLIT / 90;
		LNG_SPLIT = 360 * vMeter / vParallelLength;
	}

	private void updateServer()
	{
		float vSpeed = mPosition.getSpeed();

		RequestParams vParams = new RequestParams();

		double vLat = LAT_SPLIT, vLng = LNG_SPLIT;

		if(vSpeed > SPEED_THRESHOLD)
		{
			vLat = LAT_SPLIT * 2;
			vLng = LNG_SPLIT * 2;
		}

		vParams.add("nwcoord.lat", (mPosition.getLatitude() + vLat) + "");
		vParams.add("nwcoord.lng", (mPosition.getLongitude() - vLng) + "");
		vParams.add("secoord.lat", (mPosition.getLatitude() - vLat) + "");
		vParams.add("secoord.lng", (mPosition.getLongitude() + vLng) + "");

		ConnectionManager.obtain().get("api/locations", vParams, new ConnectionManager.JsonResponse()
		{
			@Override
			public void onResponse(boolean aResult, Object aResponse)
			{
				if(aResult && aResponse != null && aResponse instanceof JSONArray)
				{
					mNearLocations = LocationModel.getListFromJsonArray((JSONArray)aResponse);
				}
			}
		});
	}

	public List<LocationModel> getNears() 
	{
		return mNearLocations;
	}
	
	public void getLocationById(long aId, final GetLocationListener aListener)
	{
		for(int i = 0; i < mNearLocations.size(); ++i)
		{
			if(mNearLocations.get(i).getId() == aId)
			{
				if(aListener != null)
				{
					aListener.onLoad(mNearLocations.get(i));
					return;
				}
			}
		}
		
		RequestParams vParams = new RequestParams();
		vParams.put("id", aId);
		
		ConnectionManager.obtain().get("api/locations", vParams, new JsonResponse()
		{	
			@Override
			public void onResponse(boolean aResult, Object aResponse)
			{
				if(aListener != null && aResult && aResponse != null && aResponse instanceof JSONObject)
				{
					aListener.onLoad(LocationModel.fromJSON((JSONObject)aResponse));	
					return;
				}
			}
		});
	}
	
	public class GeoAdBinder extends Binder
	{
		public GeoAdService getService()
		{
			return GeoAdService.this;
		}
	}
	
	public void sendWidgetBroadcast()
	{
		Intent vIntent = new Intent(this, OffersWidgetProvider.class);
		vIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		sendBroadcast(vIntent);
	}
}
