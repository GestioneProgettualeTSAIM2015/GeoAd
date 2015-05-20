package it.itskennedy.tsaim.geoad.localdb;

import android.provider.BaseColumns;

/**
 * Created by Cado on 13/05/2015.
 */
public class FavoritesHelper implements BaseColumns {

    public static final String TABLE_NAME = "favorites";

    public static final String NAME = "name";
    public static final String LAT = "lat";
    public static final String LNG = "lng";

    public final static String CREATE_QUERY = "CREATE TABLE " + TABLE_NAME +"("
            + _ID + " INTEGER PRIMARY KEY, "
            + NAME + " TEXT NOT NULL, "
            + LAT + " DOUBLE "
            + LNG + " DOUBLE "
            + "); ";
}
