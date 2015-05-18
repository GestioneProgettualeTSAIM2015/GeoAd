package it.itskennedy.tsaim.geoad.entity;

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
    private Bundle bundle;

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

    public static List<LocationModel> getListFromJson(JSONArray aServerData)
    {
        List<LocationModel> vResult = new ArrayList<LocationModel>();

        try
        {
            for(int i = 0; i < aServerData.length(); ++i)
            {
                JSONObject vActual = aServerData.getJSONObject(i);

                int vId = vActual.getInt(ID);
                String vName = vActual.getString(NAME);
                String vPCat = vActual.getString(PCAT);
                String vSCat = vActual.getString(SCAT);
                double vLat = vActual.getDouble(LAT);
                double vLng = vActual.getDouble(LNG);
                String vDesc = vActual.getString(DESC);
                String vType = vActual.getString(TYPE);

                LocationModel vToAdd = new LocationModel(vId, vPCat, vSCat, vName, vLat, vLng, vDesc, vType);
                vResult.add(vToAdd);
            }
        }
        catch (JSONException e)
        {
            Log.e(Engine.APP_NAME, "Json Decode Error");
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
}
