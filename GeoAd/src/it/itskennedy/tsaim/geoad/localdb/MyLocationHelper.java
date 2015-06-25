package it.itskennedy.tsaim.geoad.localdb;

import android.provider.BaseColumns;

/**
 * Created by Cado on 13/05/2015.
 */
public class MyLocationHelper implements BaseColumns {

    public static final String TABLE_NAME = "mylocation";

    public static final String NAME = FavoritesHelper.NAME;
    public static final String LAT = FavoritesHelper.LAT;
    public static final String LNG = FavoritesHelper.LNG;
    public static final String DESC = FavoritesHelper.DESC;
    public static final String TYPE = FavoritesHelper.TYPE;
    public static final String PCAT = FavoritesHelper.PCAT;
    public static final String SCAT = FavoritesHelper.SCAT;

    public final static String CREATE_QUERY = "CREATE TABLE " + TABLE_NAME +"("
            + _ID + " INTEGER PRIMARY KEY, "
            + NAME + " TEXT NOT NULL, "
            + LAT + " DOUBLE, "
            + LNG + " DOUBLE, "
            + DESC + " TEXT, "
            + TYPE + " TEXT, "
            + PCAT + " TEXT, "
            + SCAT + " TEXT"
            + "); ";
}
