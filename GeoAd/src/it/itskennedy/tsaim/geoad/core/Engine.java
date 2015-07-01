package it.itskennedy.tsaim.geoad.core;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.core.ConnectionManager.JsonResponse;
import it.itskennedy.tsaim.geoad.entity.LocationModel;
import it.itskennedy.tsaim.geoad.localdb.DataFavContentProvider;
import it.itskennedy.tsaim.geoad.localdb.DataOffersContentProvider;
import it.itskennedy.tsaim.geoad.localdb.FavoritesHelper;
import it.itskennedy.tsaim.geoad.localdb.IgnoredHelper;
import it.itskennedy.tsaim.geoad.localdb.MyLocationHelper;
import it.itskennedy.tsaim.geoad.localdb.OffersHelper;
import it.itskennedy.tsaim.geoad.push.PushSignIn;
import it.itskennedy.tsaim.geoad.push.PushSignIn.PushKeyReceiver;
import it.itskennedy.tsaim.geoad.services.GeoAdService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.loopj.android.http.RequestParams;

/**
 * Created by Marco Zeni on 13/05/2015.
 */
public class Engine extends Application implements PushKeyReceiver
{
    public static String SERVER_URL = "";
    public static String APP_NAME = "GeoAd";
    public static String PROJECT_NUMBER = "";

    private static Engine mInstance;
	private String mKey;
	private String mToken;
	private Base64Cache mCache;
	
	private ArrayList<Integer>  mMyLocIdList;

	public enum LocationState
	{
		NEUTRAL, FAVORITE, IGNORED
	}
	
    @Override
    public void onCreate()
    {
        super.onCreate();
        mInstance = this; 
        
        Intent vService = new Intent(this, GeoAdService.class);
        startService(vService);

        setVariables();
        fillMyIdList();
        
        mToken = SettingsManager.get(this).getToken();
        mCache = new Base64Cache();
        
        new PushSignIn(this, this);
    }

    public static Engine get()
    {
    	return mInstance;
    }
    
    private void fillMyIdList() 
    {
    	mMyLocIdList = new ArrayList<Integer>();
    	Cursor vCur = getContentResolver().query(DataFavContentProvider.MYLOC_URI, null, null, null, null);
		while (vCur.moveToNext()) 
		{
			int vIdIndex = vCur.getColumnIndex(MyLocationHelper._ID);
			
			mMyLocIdList.add(vCur.getInt(vIdIndex));
		}
		vCur.close();
    }

    public void setVariables()
    {
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(
                    new InputStreamReader(getResources().openRawResource(R.raw.config)));

            JSONObject vObj = new JSONObject(reader.readLine());

            SERVER_URL = vObj.getString("server_url");
            PROJECT_NUMBER = vObj.getString("project_num");

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        } 
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                } 
                catch (IOException e)
                {
                }
            }
        }
    }

	public String getKey() 
	{
		if(mKey != null)
		{
			return mKey;
		}
		
		new PushSignIn(this, this);
		
		return null;
	}
	
	public String getToken()
	{
		return mToken;
	}

	@Override
	public void onKey(String aKey)
	{
		mKey = aKey;
		if(!SettingsManager.get(this).isMarkedSync())
        {
        	resetPreferenceOnServer(aKey);
        }
	}

	public void onLogin(String aToken) 
	{
		mToken = aToken;
		ConnectionManager.obtain().getNoKey(Routes.MY_LOCATIONS, null, new JsonResponse()
		{	
			@Override
			public void onResponse(boolean aResult, Object aResponse) 
			{
				if(aResult && aResponse != null && aResponse instanceof JSONArray)
				{
					JSONArray vMyLoc = ((JSONArray)aResponse);
					
					for(int i = 0; i < vMyLoc.length(); ++i)
					{
						LocationModel vLoc;
						try
						{
							vLoc = LocationModel.fromJSON(vMyLoc.getJSONObject(i));
							getContentResolver().insert(DataFavContentProvider.MYLOC_URI, vLoc.getContentValues());
						}
						catch (JSONException e)
						{
							Log.e(APP_NAME, "JSON Error");
						}
					}
				}
			}
		});
	}
	
	public Base64Cache getCache()
	{
		return mCache;
	}

	public void updateLocationState(final LocationModel aLocation, LocationState aState)
	{
		RequestParams vParams = new RequestParams();
		vParams.put("Id", aLocation.getId());
		
		if(aState != getLocationState(aLocation.getId()))
		{
			if(aState == LocationState.FAVORITE)
			{	
				ConnectionManager.obtain().post(Routes.MY_FAVORITES, vParams, new JsonResponse()
				{	
					@Override
					public void onResponse(boolean aResult, Object aResponse)
					{
						if(aResult)
						{
							deleteIgnored(aLocation.getId());
							getContentResolver().insert(DataFavContentProvider.FAVORITES_URI, aLocation.getContentValues());
						}
					}
				});
			}
			else if(aState == LocationState.IGNORED)
			{
				ConnectionManager.obtain().post(Routes.MY_IGNORED, vParams, new JsonResponse()
				{	
					@Override
					public void onResponse(boolean aResult, Object aResponse)
					{
						if(aResult)
						{
							deleteFavorite(aLocation.getId());
							getContentResolver().insert(DataFavContentProvider.IGNORED_URI, aLocation.getIgnoredContentValues());	
						}
					}
				});
			}
			else if(aState == LocationState.NEUTRAL)
			{
				setNeutralLocationState(aLocation.getId());
			}
		}
	}
	
	public void setNeutralLocationState(final int aId)
	{
		LocationState vActual = getLocationState(aId);
		if(vActual == LocationState.IGNORED)
		{	
			ConnectionManager.obtain().delete(Routes.MY_IGNORED + "?Id=" + aId + "&key=" + mKey, new JsonResponse()
			{	
				@Override
				public void onResponse(boolean aResult, Object aResponse)
				{
					if(aResult)
					{
						deleteIgnored(aId);
					}
				}
			});
		}
		else if(vActual == LocationState.FAVORITE)
		{		
			ConnectionManager.obtain().delete(Routes.MY_IGNORED + "?Id=" + aId + "&key=" + mKey, new JsonResponse()
			{	
				@Override
				public void onResponse(boolean aResult, Object aResponse)
				{
					if(aResult)
					{
						deleteFavorite(aId);
					}
				}
			});
		}
	}

	private void deleteFavorite(int aId)
	{
		getContentResolver().delete(DataFavContentProvider.FAVORITES_URI, IgnoredHelper._ID + " = " + aId, null);
		getContentResolver().delete(DataOffersContentProvider.OFFERS_URI, OffersHelper.LOCATION_ID + " = " + aId, null);
	}

	private void deleteIgnored(final int aId)
	{
		getContentResolver().delete(DataFavContentProvider.IGNORED_URI, IgnoredHelper._ID + " = " + aId, null);
	}

	public LocationState getLocationState(int id)
	{
		Cursor vCur = getContentResolver().query(DataFavContentProvider.FAVORITES_URI, null, FavoritesHelper._ID + " = " + id, null, null);
		if(vCur.getCount() == 1)
		{
			return LocationState.FAVORITE;
		}
		else
		{
			vCur = getContentResolver().query(DataFavContentProvider.IGNORED_URI, null, IgnoredHelper._ID + " = " + id, null, null);
			if(vCur.getCount() == 1)
			{
				return LocationState.IGNORED;
			}	
		}
		
		vCur.close();
		return LocationState.NEUTRAL;
	}
	
	public void resetPreferenceOnServer(String aKey)
	{
		ConnectionManager.obtain().delete(Routes.MY_PREFERENCES + aKey, null);
		SettingsManager.get(mInstance).setMarkedSyncTrue();
	}
	
	public boolean imLocationOwner(int aLocId)
	{
		return mMyLocIdList.contains(aLocId);
	}

	public void removeMyLocation(int vId) 
	{
		getContentResolver().delete(DataFavContentProvider.MYLOC_URI, MyLocationHelper._ID + " = " + vId, null);
	}
}
