package it.itskennedy.tsaim.geoad.entity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import it.itskennedy.tsaim.geoad.core.Engine;
import it.itskennedy.tsaim.geoad.localdb.OffersHelper;

/**
 * Created by Marco Zeni on 15/05/2015.
 */
public class Offer
{
    public static final String BUNDLE_KEY = "offer_bundle";

    public static final String ID = "Id";
    public static final String NAME = "Name";
    public static final String LOC_NAME = "LocationName";
    public static final String LOC_ID = "LocationId";
    public static final String DESC = "Desc";
    public static final String INS_DATE = "InsDateMillis";
    public static final String EXP_DATE = "ExpDateMillis";

    private int mId;
    private String mName;
    private int mLocationId;
    private String mLocationName;
    private String mDesc;
    private long mInsDate;
    private long mExpDate;

    public Offer(int aId, String aName, int aLocId, String aLocName, String aDesc, long aInsDate, long aExpDate)
    {
        mId = aId;
        mName = aName;
        mDesc = aDesc;
        mLocationName = aLocName;
        mLocationId = aLocId;
        mInsDate = aInsDate;
        mExpDate = aExpDate;
    }

    public int getId()
    {
        return mId;
    }

    public int getLocationId()
    {
        return mLocationId;
    }

    public String getDesc()
    {
        return mDesc;
    }
    
    public String getLocationName()
    {
    	return mLocationName;
    }

    public Bundle getBundle()
    {
        Bundle vBundle = new Bundle();

        vBundle.putInt(ID, mId);
        vBundle.putString(NAME, mName);
        vBundle.putInt(LOC_ID, mLocationId);
        vBundle.putString(LOC_NAME, mLocationName);
        vBundle.putString(DESC, mDesc);
        vBundle.putLong(INS_DATE, mInsDate);
        vBundle.putLong(EXP_DATE, mExpDate);

        return vBundle;
    }

    public ContentValues getContentValues()
    {
        ContentValues vContentValues = new ContentValues();

        vContentValues.put(OffersHelper.OFF_ID, mId);
        vContentValues.put(OffersHelper.NAME, mName);
        vContentValues.put(OffersHelper.DESC, mDesc);
        vContentValues.put(OffersHelper.LOCATION_ID, mLocationId);
        vContentValues.put(OffersHelper.LOCATION_NAME, mLocationName);
        vContentValues.put(OffersHelper.EXP_DATE, mExpDate);
        vContentValues.put(OffersHelper.INS_DATE, mInsDate);

        return vContentValues;
    }

    public static Offer fromBundle(Bundle aOfferBundle)
    {
        int vId = aOfferBundle.getInt(ID);
        String vName = aOfferBundle.getString(NAME);
        String vLocationName = aOfferBundle.getString(LOC_NAME);
        int vLocationId = aOfferBundle.getInt(LOC_ID);
        String vDesc = aOfferBundle.getString(DESC);
        long vInsDate = aOfferBundle.getLong(INS_DATE);
        long vExpDate = aOfferBundle.getLong(EXP_DATE);

        return new Offer(vId, vName, vLocationId, vLocationName, vDesc, vInsDate, vExpDate);
    }

    public static Offer fromJSON(String aJSON)
    {
        try
        {
            JSONObject vObj = new JSONObject(aJSON);

            int vId = vObj.getInt(ID);
            String vName = vObj.getString(NAME);
            String vLocationName = vObj.getString(LOC_NAME);
            int vLocationId = vObj.getInt(LOC_ID);
            String vDesc = vObj.getString(DESC);
            long vInsDate = vObj.getLong(INS_DATE);
            long vExpDate = vObj.getLong(EXP_DATE);

            return new Offer(vId, vName, vLocationId, vLocationName, vDesc, vInsDate, vExpDate);
        }
        catch (JSONException e)
        {
            Log.d(Engine.APP_NAME, "Json Parse Error");
        }

        return null;
    }

	public String getExpTime() 
	{
		DateFormat vFormatter = new SimpleDateFormat("dd/MM/yyyy");

		Calendar vCalendar = Calendar.getInstance();
		vCalendar.setTimeInMillis(mExpDate);

		return vFormatter.format(vCalendar.getTime()); 
	}
}
