package it.itskennedy.tsaim.geoad.entity;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import it.itskennedy.tsaim.geoad.core.Engine;

/**
 * Created by Marco Zeni on 15/05/2015.
 */
public class Offer
{
    public static final String BUNDLE_KEY = "offer_bundle";

    public static final String ID = "id";
    public static final String LOC_ID = "loc_id";
    public static final String DESC = "desc";
    public static final String INS_DATE = "ins_date";
    public static final String EXP_DATE = "exp_date";

    private int mId;
    private int mLocationId;
    private String mDesc;
    private long mInsDate;
    private long mExpDate;

    public Offer(int aId, int aLocId, String aDesc, long aInsDate, long aExpDate)
    {
        mId = aId;
        mDesc = aDesc;
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

    public Bundle getBundle()
    {
        Bundle vBundle = new Bundle();

        vBundle.putInt(ID, mId);
        vBundle.putInt(LOC_ID, mLocationId);
        vBundle.putString(DESC, mDesc);
        vBundle.putLong(INS_DATE, mInsDate);
        vBundle.putLong(EXP_DATE, mExpDate);

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

        return new Offer(vId, vLocationId, vDesc, vInsDate, vExpDate);
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

            return new Offer(vId, vLocationId, vDesc, vInsDate, vExpDate);
        }
        catch (JSONException e)
        {
            Log.d(Engine.APP_NAME, "Json Parse Error");
        }

        return null;
    }
}
