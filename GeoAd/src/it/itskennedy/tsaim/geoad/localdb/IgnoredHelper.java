package it.itskennedy.tsaim.geoad.localdb;

import android.provider.BaseColumns;

/**
 * Created by Cado on 13/05/2015.
 */
public class IgnoredHelper implements BaseColumns {

    public static final String TABLE_NAME = "ignored";
    
    public static final String COLUMN_NAME = "name_ignored";

    public final static String CREATE_QUERY = "CREATE TABLE " + TABLE_NAME +"("
            + _ID + " INTEGER PRIMARY KEY, "
            + COLUMN_NAME + " TEXT "
            + "); ";
}
