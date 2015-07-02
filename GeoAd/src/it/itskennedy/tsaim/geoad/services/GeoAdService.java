package it.itskennedy.tsaim.geoad.services;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.core.ConnectionManager;
import it.itskennedy.tsaim.geoad.core.ConnectionManager.JsonResponse;
import it.itskennedy.tsaim.geoad.core.Engine;
import it.itskennedy.tsaim.geoad.core.Engine.LocationState;
import it.itskennedy.tsaim.geoad.core.LocationManager;
import it.itskennedy.tsaim.geoad.core.LocationManager.LocationListener;
import it.itskennedy.tsaim.geoad.core.NotificationManager;
import it.itskennedy.tsaim.geoad.core.Routes;
import it.itskennedy.tsaim.geoad.entity.LocationModel;
import it.itskennedy.tsaim.geoad.entity.Offer;
import it.itskennedy.tsaim.geoad.localdb.DataFavContentProvider;
import it.itskennedy.tsaim.geoad.localdb.DataOffersContentProvider;
import it.itskennedy.tsaim.geoad.localdb.FavoritesHelper;
import it.itskennedy.tsaim.geoad.localdb.MyLocationHelper;
import it.itskennedy.tsaim.geoad.localdb.OffersHelper;
import it.itskennedy.tsaim.geoad.widgets.WidgetProvider;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Notification;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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
	private static final int DISTANCE_THRESHOLD = 400; //0 ONLY FOR TEST
	private double LNG_SPLIT;
	
	private static final int NOTIFICATION = 12345;
	
	private Location mPosition;
	private List<LocationModel> mNearLocations;
	private List<Offer> mSuspendedOffers;

	public interface GetLocationListener
	{
		public void onLoad(LocationModel aLocation);
	}
	
	public Location getCurrentPosition() {
		return mPosition;
	}
	
	@Override
	public void onCreate() 
	{
	  	LocationManager.get(this).addListener(this);
	  	
	  	Log.w(Engine.APP_NAME, "Service Created!");
	  	
	  	startForeground(NOTIFICATION, getNotify());
	  	
	  	mSuspendedOffers = new ArrayList<Offer>();
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
						
						Offer vOff = Offer.fromJSON(vJson); 
						if(Engine.get().getLocationState(vOff.getLocationId()) != LocationState.IGNORED)
						{
							manageOffer(vOff);
						}
					}
				}
				else if(vAction.equals(DELETE_OFFER)) 
				{
					int vToRemove = intent.getIntExtra(DELETE_ID, 0);
					getContentResolver().delete(DataOffersContentProvider.OFFERS_URI, OffersHelper.OFF_ID + " = " + vToRemove, null);
					sendWidgetBroadcast();
				}
				else if(vAction.equals(NEW_LOCATION))
				{
					Bundle vExtra = intent.getExtras();
					if(!vExtra.isEmpty())
					{
						String vJson = intent.getExtras().getString(DATA);
						addOrReplace(LocationModel.fromJSON(vJson));
					}
				}
				else if(vAction.equals(DELETE_LOCATION))
				{
					int vToRemove = intent.getIntExtra(DELETE_ID, 0);
					getContentResolver().delete(DataOffersContentProvider.OFFERS_URI, OffersHelper.LOCATION_ID + " = " + vToRemove, null);
					getContentResolver().delete(DataFavContentProvider.MYLOC_URI, MyLocationHelper._ID + " = " + vToRemove, null);
					removeIfExist(vToRemove);
					sendWidgetBroadcast();
				}
			}
		}
		
	    return START_STICKY;
		
	}
	
	private Notification getNotify()
	{
		NotificationCompat.Builder vBuilder =
				new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("GeoAd Service")
				.setContentText("Running...")
				.setOngoing(true);
		
		if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT)
		{
			vBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		}
		
		return vBuilder.build();
	}
	
	private void addOrReplace(LocationModel aLocation)
	{
		for(int i = 0; i < mNearLocations.size(); ++i)
		{
			if(mNearLocations.get(i).getId() == aLocation.getId())
			{
				mNearLocations.add(i, aLocation);
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

	private void manageOffer(Offer aOffer)
	{
		Cursor vCur = getContentResolver().query(DataOffersContentProvider.OFFERS_URI, null, OffersHelper.OFF_ID + " = " + aOffer.getId(), null, null);
		
		if(vCur.getCount() == 0)
		{
			if(mPosition != null)
			{
				if(isLocationNearOrFavorite(aOffer.getLocationId()) || true)
				{
					getContentResolver().insert(DataOffersContentProvider.OFFERS_URI, aOffer.getContentValues());
					NotificationManager.showOffer(GeoAdService.this, aOffer);
				}
			}
			else
			{
				suspendOfferForWaitLocations(aOffer);
			}
		}
		else
		{
			getContentResolver().update(DataOffersContentProvider.OFFERS_URI, aOffer.getContentValues(), OffersHelper.OFF_ID + " = " + aOffer.getId(), null);
		}
		
		vCur.close();
		sendWidgetBroadcast();		
	}
	
	private void suspendOfferForWaitLocations(Offer aOffer)
	{
		mSuspendedOffers.add(aOffer);
	}
	
	private boolean isLocationNearOrFavorite(int aId)
	{
		for(int i = 0; i < mNearLocations.size(); ++i)
		{
			if(mNearLocations.get(i).getId() == aId)
			{
				return true;
			}
		}
		
		Cursor vCur = getContentResolver().query(DataFavContentProvider.FAVORITES_URI, null, FavoritesHelper._ID + " = " + aId, null, null);
		if(vCur.getCount() == 1)
		{
			vCur.close();
			return true;
		}
		
		vCur.close();
		
		return false;
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
		if(mPosition != null)
		{
			float[] vResult = new float[1];
			
			Location.distanceBetween(aLocation.getLatitude(), aLocation.getLongitude(),
					mPosition.getLatitude(), mPosition.getLongitude(), vResult);
			
			if(vResult[0] > DISTANCE_THRESHOLD)
			{
				mPosition = aLocation;
				updateFromServer();	
			}
		}
		else
		{
			updateLngSplit(aLocation);
			mPosition = aLocation;
			updateFromServer();
		}
	}

	private void updateLngSplit(Location aLocation)
	{
		double vParallelRadius = Math.cos(Math.toRadians(aLocation.getLatitude())) * EARTH_RADIUS;
		double vParallelLength = vParallelRadius * 2 * Math.PI;

		double vMeter = EARTH_RADIUS * 2 * Math.PI / 4 * LAT_SPLIT / 90;
		LNG_SPLIT = 360 * vMeter / vParallelLength;
	}

	private void updateFromServer()
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

		ConnectionManager.obtain().post(Routes.POSITIONS, vParams, new ConnectionManager.JsonResponse()
		{
			@Override
			public void onResponse(boolean aResult, Object aResponse)
			{
				if(aResult && aResponse != null && aResponse instanceof JSONObject)
				{
					JSONArray vLocations = ((JSONObject)aResponse).optJSONArray("Locations");
					JSONArray vOffers = ((JSONObject)aResponse).optJSONArray("Offers");
					
					mNearLocations = LocationModel.getListFromJsonArray(vLocations);
					
					List<Offer> vOffersList = Offer.getListFromJsonArray(vOffers);
					
					for(int i = 0; i < mSuspendedOffers.size(); ++i)
					{
						vOffersList.add(mSuspendedOffers.remove(i));
					}
					
					for(int i = 0; i < vOffersList.size(); ++i)
					{
						manageOffer(vOffersList.get(i));
					}
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
		
		Cursor vC = getContentResolver().query(DataFavContentProvider.FAVORITES_URI, null, FavoritesHelper._ID + " = " + aId, null, null);
		
		if(vC.moveToFirst())
		{
			LocationModel vLoc = locationFromCursor(aId, vC);
			aListener.onLoad(vLoc);
			return;
		}
		vC.close();
		
		vC = getContentResolver().query(DataFavContentProvider.MYLOC_URI, null, MyLocationHelper._ID + " = " + aId, null, null);
		
		if(vC.moveToFirst())
		{
			LocationModel vLoc = locationFromCursor(aId, vC);
			aListener.onLoad(vLoc);
			return;
		}
		vC.close();
		
		RequestParams vParams = new RequestParams();
		vParams.put("id", aId);
		
		ConnectionManager.obtain().get(Routes.POSITIONS, vParams, new JsonResponse()
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

	private LocationModel locationFromCursor(long aId, Cursor vC)
	{
		int vDescIndex = vC.getColumnIndex(FavoritesHelper.DESC);
		int vLatIndex = vC.getColumnIndex(FavoritesHelper.LAT);
		int vLngIndex = vC.getColumnIndex(FavoritesHelper.LNG);
		int vNameIndex = vC.getColumnIndex(FavoritesHelper.NAME);
		int vPCatIndex = vC.getColumnIndex(FavoritesHelper.PCAT);
		int vSCatIndex = vC.getColumnIndex(FavoritesHelper.SCAT);
		int vTypeIndex = vC.getColumnIndex(FavoritesHelper.TYPE);
		
		String vDesc = vC.getString(vDescIndex);
		double vLat = vC.getDouble(vLatIndex);
		double vLng = vC.getDouble(vLngIndex);
		String vName = vC.getString(vNameIndex);
		String vPCat = vC.getString(vPCatIndex);
		String vSCat = vC.getString(vSCatIndex);
		String vType = vC.getString(vTypeIndex);
		
		LocationModel vLoc = new LocationModel((int)aId, vPCat,vSCat, vName, vLat, vLng, vDesc, vType);
		return vLoc;
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
		Intent vIntent = new Intent(this, WidgetProvider.class);
		vIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		sendBroadcast(vIntent);
	}
}
