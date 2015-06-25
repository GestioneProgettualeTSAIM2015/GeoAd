package it.itskennedy.tsaim.geoad.localdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Cado on 13/05/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "data.db";
    private final static int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FavoritesHelper.CREATE_QUERY);
        db.execSQL(IgnoredHelper.CREATE_QUERY);
        db.execSQL(OffersHelper.CREATE_QUERY);
        db.execSQL(MyLocationHelper.CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoritesHelper.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + IgnoredHelper.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + OffersHelper.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MyLocationHelper.TABLE_NAME);
        onCreate(db);
    }

}
