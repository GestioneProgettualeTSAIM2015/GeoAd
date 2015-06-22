package it.itskennedy.tsaim.geoad.core;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.entity.LocationModel;
import it.itskennedy.tsaim.geoad.localdb.DataFavContentProvider;
import it.itskennedy.tsaim.geoad.localdb.DataOffersContentProvider;
import it.itskennedy.tsaim.geoad.localdb.FavoritesHelper;
import it.itskennedy.tsaim.geoad.localdb.IgnoredHelper;
import it.itskennedy.tsaim.geoad.localdb.OffersHelper;
import it.itskennedy.tsaim.geoad.push.PushSignIn;
import it.itskennedy.tsaim.geoad.push.PushSignIn.PushKeyReceiver;
import it.itskennedy.tsaim.geoad.services.GeoAdService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

	public enum LocationState
	{
		NEUTRAL, FAVORITE, IGNORED
	}
	
    @Override
    public void onCreate()
    {
        super.onCreate();
        
        Intent vService = new Intent(this, GeoAdService.class);
        startService(vService);

        setVariables();
        
        mToken = SettingsManager.get(this).getToken();
        mCache = new Base64Cache();
        
        new PushSignIn(this, this);

        mInstance = this; 
    }

    public static Engine get()
    {
    	return mInstance;
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
	}

	public void setToken(String aToken) 
	{
		mToken = aToken;
	}
	
	public Base64Cache getCache()
	{
		return mCache;
	}

	public void updateLocationState(LocationModel aLocation, LocationState aState)
	{
		if(aState == LocationState.FAVORITE)
		{
			deleteIgnored(aLocation.getId());
			getContentResolver().insert(DataFavContentProvider.FAVORITES_URI, aLocation.getContentValues());
			notifyServer();
		}
		else if(aState == LocationState.IGNORED)
		{
			deleteFavorite(aLocation.getId());
			getContentResolver().insert(DataFavContentProvider.IGNORED_URI, aLocation.getIgnoredContentValues());	
			notifyServer();
		}
		else if(aState == LocationState.NEUTRAL)
		{
			setNeutralLocationState(aLocation.getId());
		}
	}
	
	public void setNeutralLocationState(int aId)
	{
		deleteIgnored(aId);
		deleteFavorite(aId);
		notifyServer();
	}
	
	private void notifyServer()
	{
		JSONArray vJsonList = new JSONArray();
		
		Cursor vCur = getContentResolver().query(DataFavContentProvider.FAVORITES_URI, null, null, null, null);
		while(vCur.moveToNext())
		{
			int vIdIndex = vCur.getColumnIndex(FavoritesHelper._ID);
			vJsonList.put(vCur.getInt(vIdIndex));
		}
		
		vCur = getContentResolver().query(DataFavContentProvider.IGNORED_URI, null, null, null, null);
		while(vCur.moveToNext())
		{
			int vIdIndex = vCur.getColumnIndex(IgnoredHelper._ID);
			vJsonList.put(-vCur.getInt(vIdIndex));
		}
		
		RequestParams vParams = new RequestParams();
		vParams.put("ids", vJsonList.toString());
		
		ConnectionManager.obtain().post("api/favorites", vParams, null);
	}

	private void deleteFavorite(int aId)
	{
		getContentResolver().delete(DataFavContentProvider.FAVORITES_URI, IgnoredHelper._ID + " = " + aId, null);
		getContentResolver().delete(DataOffersContentProvider.OFFERS_URI, OffersHelper.LOCATION_ID + " = " + aId, null);
	}

	private void deleteIgnored(int aId)
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
		
		return LocationState.NEUTRAL;
	}
}
