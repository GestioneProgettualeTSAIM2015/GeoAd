package it.itskennedy.tsaim.geoad.entity;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import it.itskennedy.tsaim.geoad.core.Engine;

/**
 * Created by ITS on 15/05/2015.
 */
public class Offer
{
    public static final String BUNDLE_KEY = "bundle_key";

    public static final String ID = "id";
    public static final String LOC_ID = "loc_id";
    public static final String DESC = "desc";
    public static final String INS_DATE = "ins_date";
    public static final String EXP_DATE = "exp_date";
    public static final String LOC_NAME = "loc_name";
    public static final String LOC_LAT = "loc_lat";
    public static final String LOC_LNG = "loc_lng";

    private int mId;
    private int mLocationId;
    private String mDesc;
    private long mInsDate;
    private long mExpDate;
    private String mLocationName;
    private double mLocationLat;
    private double mLocationLng;

    public Offer(int aId, int aLocId, String aDesc, long aInsDate, long aExpDate, String aLocName, double aLocLat, double aLocLng)
    {
        mId = aId;
        mDesc = aDesc;
        mLocationId = aLocId;
        mInsDate = aInsDate;
        mExpDate = aExpDate;
        mLocationName = aLocName;
        mLocationLat = aLocLat;
        mLocationLng = aLocLng;
    }

    public int getId()
    {
        return mId;
    }

    public String getLocationName()
    {
        return mLocationName;
    }

    public String getDesc()
    {
        return mDesc;
    }

    public Bundle getBundle()
    {
        Bundle vBundle = new Bundle();

        vBundle.putInt(ID, mId);
        vBundle.putInt(LOC_ID, mLocationId);
        vBundle.putString(DESC, mDesc);
        vBundle.putLong(INS_DATE, mInsDate);
        vBundle.putLong(EXP_DATE, mExpDate);
        vBundle.putString(LOC_NAME, mLocationName);
        vBundle.putDouble(LOC_LAT, mLocationLat);
        vBundle.putDouble(LOC_LNG, mLocationLng);

        return vBundle;
    }

    public ContentValues getContentValues()
    {
        ContentValues vContentValues = new ContentValues();

        //TODO cadorin
        //impostati l'oggetto secondo i nomi delle tue colonne

        return null;
    }

    public static Offer fromBundle(Bundle aOfferBundle)
    {
        int vId = aOfferBundle.getInt(ID);
        int vLocationId = aOfferBundle.getInt(LOC_ID);
        String vDesc = aOfferBundle.getString(DESC);
        long vInsDate = aOfferBundle.getLong(INS_DATE);
        long vExpDate = aOfferBundle.getLong(EXP_DATE);
        String vLocationName = aOfferBundle.getString(LOC_NAME);
        double vLocationLat = aOfferBundle.getDouble(LOC_LAT);
        double vLocationLng = aOfferBundle.getDouble(LOC_LNG);

        return new Offer(vId, vLocationId, vDesc, vInsDate, vExpDate, vLocationName, vLocationLat, vLocationLng);
    }

    public static Offer fromJSON(String aJSON)
    {
        try
        {
            JSONObject vObj = new JSONObject(aJSON);

            int vId = vObj.getInt(ID);
            int vLocationId = vObj.getInt(LOC_ID);
            String vDesc = vObj.getString(DESC);
            long vInsDate = vObj.getLong(INS_DATE);
            long vExpDate = vObj.getLong(EXP_DATE);
            String vLocationName = vObj.getString(LOC_NAME);
            double vLocationLat = vObj.getDouble(LOC_LAT);
            double vLocationLng = vObj.getDouble(LOC_LNG);

            return new Offer(vId, vLocationId, vDesc, vInsDate, vExpDate, vLocationName, vLocationLat, vLocationLng);
        }
        catch (JSONException e)
        {
            Log.d(Engine.APP_NAME, "Json Parse Error!");
        }

        return null;
    }
}
