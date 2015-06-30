package it.itskennedy.tsaim.geoad.entity;

import it.itskennedy.tsaim.geoad.core.Engine;
import it.itskennedy.tsaim.geoad.localdb.FavoritesHelper;
import it.itskennedy.tsaim.geoad.localdb.IgnoredHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Marco Zeni on 18/05/2015.
 */
public class LocationModel implements Serializable
{
	private static final long serialVersionUID = 1596833325021343731L;

	public static final String BUNDLE_KEY = "location_bundle";
	
    public static final String ID = "Id";
    public static final String PCAT = "PCat";
    public static final String SCAT = "SCat";
    public static final String NAME = "Name";
    public static final String LAT = "Lat";
    public static final String LNG = "Lng";
    public static final String DESC = "Desc";
    public static final String TYPE = "Type";

    private int mId;
    private String mPCat;
    private String mSCat;
    private String mName;
    private double mLat;
    private double mLng;
    private String mDesc;
    private String mType;

    
    public LocationModel(int aId, String aPCat, String aSCat, String aName, double aLat, double aLng, String aDesc, String aType)
    {
        mId = aId;
        mPCat = aPCat;
        mSCat = aSCat;
        mName = aName;
        mLat = aLat;
        mLng = aLng;
        mDesc = aDesc;
        mType = aType;
        
    }

    public int getId()
    {
        return mId;
    }

    public String getName()
    {
        return mName;
    }
    
    public String getDescription() {
    	return mDesc;
    }
    
    public String getmPCat()
	{
		return mPCat;
	}

	public String getmSCat()
	{
		return mSCat;
	}

	public Location getLocation()
    {
    	Location vToReturn = new Location("");
    	vToReturn.setLatitude(mLat);
    	vToReturn.setLongitude(mLng);
    	
    	return vToReturn;
    }

    public static List<LocationModel> getListFromJsonArray(JSONArray aServerData)
    {
        List<LocationModel> vResult = new ArrayList<LocationModel>();

        if(aServerData != null)
        {
        	for(int i = 0; i < aServerData.length(); ++i)
            {
            	LocationModel vToAdd = null;
            	
            	try
            	{
            		vToAdd = fromJSON(aServerData.getString(i));
            	}
    	        catch (JSONException e)
    	        {
    	            Log.e(Engine.APP_NAME, "Json Decode Error");
    	        }
            	
                if(vToAdd != null)
                {
                	vResult.add(vToAdd);
                }  
            }
        }

        return vResult;
    }

    public Bundle getBundle()
    {
        Bundle vBundle = new Bundle();

        vBundle.putInt(ID, mId);
        vBundle.putString(NAME, mName);
        vBundle.putString(PCAT, mPCat);
        vBundle.putString(SCAT, mSCat);
        vBundle.putDouble(LAT, mLat);
        vBundle.putDouble(LNG, mLng);
        vBundle.putString(DESC, mDesc);
        vBundle.putString(TYPE, mType);

        return vBundle;
    }
    
    public static LocationModel fromBundle(Bundle aLocationBundle)
    {
        int vId = aLocationBundle.getInt(ID);
        String vName = aLocationBundle.getString(NAME);
        String vDesc = aLocationBundle.getString(DESC);
        String vPCat = aLocationBundle.getString(PCAT);
        String vSCat = aLocationBundle.getString(SCAT);
        double vLat = aLocationBundle.getDouble(LAT);
        double vLng = aLocationBundle.getDouble(LNG);
        String vType = aLocationBundle.getString(TYPE);

        return new LocationModel(vId, vPCat, vSCat, vName, vLat, vLng, vDesc, vType);
    }
    
    public static LocationModel fromJSON(JSONObject aObj)
    {
    	return fromJSON(aObj.toString());
    }

	public static LocationModel fromJSON(String aJson) 
	{
		try
        {
            JSONObject vObj = new JSONObject(aJson);

            int vId = vObj.getInt(ID);
            String vName = vObj.optString(NAME);
            String vDesc = vObj.optString(DESC);
            String vPCat = vObj.optString(PCAT);
            String vSCat = vObj.optString(SCAT).equals("null") ? null : vObj.optString(SCAT);
            double vLat = vObj.optDouble(LAT);
            double vLng = vObj.optDouble(LNG);
            String vType = vObj.optString(TYPE);

            return new LocationModel(vId, vPCat, vSCat, vName, vLat, vLng, vDesc, vType);
        }
        catch (JSONException e)
        {
            Log.d(Engine.APP_NAME, "Json Parse Error");
        }

        return null;
	}

	public String getPCat() 
	{
		return mPCat;
	}

	public String getSCat()
	{
		return mSCat;
	}

	public String getType() 
	{
		return mType;
	}

	public String getDesc() 
	{
		return mDesc;
	}

	public double getLat() 
	{
		return mLat;
	}
	
	public double getLng()
	{
		return mLng;
	}

	public ContentValues getContentValues()
	{
		ContentValues vCont = new ContentValues();

		vCont.put(FavoritesHelper._ID, mId);
		vCont.put(FavoritesHelper.NAME, mName);
        vCont.put(FavoritesHelper.PCAT, mPCat);
        vCont.put(FavoritesHelper.SCAT, mSCat);
        vCont.put(FavoritesHelper.LAT, mLat);
        vCont.put(FavoritesHelper.LNG, mLng);
        vCont.put(FavoritesHelper.DESC, mDesc);
        vCont.put(FavoritesHelper.TYPE, mType);

        return vCont;
	}

	public ContentValues getIgnoredContentValues()
	{
		ContentValues vCont = new ContentValues();

		vCont.put(IgnoredHelper._ID, mId);
		vCont.put(IgnoredHelper.COLUMN_NAME, mName);

        return vCont;
	}
}
