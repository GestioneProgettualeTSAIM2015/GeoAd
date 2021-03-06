package it.itskennedy.tsaim.geoad.localdb;

import android.provider.BaseColumns;

/**
 * Created by Cado on 13/05/2015.
 */
public class OffersHelper implements BaseColumns {

    public static final String TABLE_NAME = "offers";

    public static final String NAME = "name";
    public static final String OFF_ID = "offid";
    public static final String LOCATION_ID = "locationid";
    public static final String LOCATION_NAME = "locationname";
    public static final String DESC = "description";
    public static final String EXP_DATE = "expiredate";
    public static final String INS_DATE = "insdate";

    public final static String CREATE_QUERY = "CREATE TABLE " + TABLE_NAME +"("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + OFF_ID + " INTEGER, "
            + NAME + " TEXT, "
            + LOCATION_ID + " INTEGER NOT NULL, "
            + LOCATION_NAME + " TEXT, "
            + DESC + " TEXT, "
            + EXP_DATE + " INTEGER, "
            + INS_DATE + " INTEGER "
            + "); ";
}
