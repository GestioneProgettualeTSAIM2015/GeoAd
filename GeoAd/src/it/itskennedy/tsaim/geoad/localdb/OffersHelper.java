package it.itskennedy.tsaim.geoad.localdb;

import android.provider.BaseColumns;

/**
 * Created by Cado on 13/05/2015.
 */
public class OffersHelper implements BaseColumns {

    public static final String TABLE_NAME = "offers";

    public static final String LOCATION_ID = "locationid";
    public static final String DESC = "description";
    public static final String EXP_DATE = "expiredate";

    public final static String CREATE_QUERY = "CREATE TABLE " + TABLE_NAME +"("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + LOCATION_ID + " INTEGER NOT NULL, "
            + DESC + " TEXT, "
            + EXP_DATE + " DATETIME "
            + "); ";
}
