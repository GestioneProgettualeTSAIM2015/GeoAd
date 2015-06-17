package it.itskennedy.tsaim.geoad.entity;

import android.location.Location;
import android.os.BaseBundle;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.itskennedy.tsaim.geoad.core.Engine;

/**
 * Created by Marco Zeni on 18/05/2015.
 */
public class LocationModel
{
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
            String vName = vObj.getString(NAME);
            String vDesc = vObj.getString(DESC);
            String vPCat = vObj.getString(PCAT);
            String vSCat = vObj.getString(SCAT);
            double vLat = vObj.getDouble(LAT);
            double vLng = vObj.getDouble(LNG);
            String vType = vObj.getString(TYPE);

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
}
