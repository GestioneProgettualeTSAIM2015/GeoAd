package it.itskennedy.tsaim.geoad.localdb;

import it.itskennedy.tsaim.geoad.core.Engine;

import java.util.Date;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Cado on 13/05/2015.
 */
public class DataOffersContentProvider extends ContentProvider {
    public static final String AUTHORITY = "it.itskennedy.tsaim.geoad.localdb.dataofferscontentprovider";

    public static final String OFFERS_PATH = "offers";
    public static final String DELETE_EXPIRED = "delete_expired";

    public static final Uri OFFERS_URI =
            Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTHORITY + "/" + OFFERS_PATH);

    private static final int FULL_OFFERS_TABLE = 4;
    private static final int SINGLE_OFFER = 5;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, OFFERS_PATH, FULL_OFFERS_TABLE);
        uriMatcher.addURI(AUTHORITY, OFFERS_PATH + "/#", SINGLE_OFFER);
    }

    private DBHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)) {
            case FULL_OFFERS_TABLE:
                queryBuilder.setTables(OffersHelper.TABLE_NAME);
                break;
            case SINGLE_OFFER:
                queryBuilder.setTables(OffersHelper.TABLE_NAME);
                queryBuilder.appendWhere(OffersHelper._ID + "=" + uri.getLastPathSegment());
                break;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    public static final String MIME_TYPE_ALL_OFFERS = ContentResolver.CURSOR_DIR_BASE_TYPE + "/alloffers";
    public static final String MIME_TYPE_SINGLE_OFFER = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/singleoffer";

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case FULL_OFFERS_TABLE:
                return MIME_TYPE_ALL_OFFERS;
            case SINGLE_OFFER:
                return MIME_TYPE_SINGLE_OFFER;
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long result = -1;
        switch (uriMatcher.match(uri)) {
            case FULL_OFFERS_TABLE:
                result = db.insert(OffersHelper.TABLE_NAME, null, values);
                Log.d(Engine.APP_NAME, "ROW INSERTED ID: " + result);
                getContext().getContentResolver().notifyChange(OFFERS_URI, null);
                return Uri.parse(OFFERS_URI.toString() + "/" + result);
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deletedLines = 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case FULL_OFFERS_TABLE:
                deletedLines = db.delete(OffersHelper.TABLE_NAME, selection, selectionArgs);
                break;
            case SINGLE_OFFER:
                String offerUri = OffersHelper._ID + " = " + uri.getLastPathSegment();
                deletedLines = db.delete(OffersHelper.TABLE_NAME, selection + " AND " + offerUri, selectionArgs);
                break;
        }
        if (deletedLines > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedLines;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int updatedLines = 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case FULL_OFFERS_TABLE:
                updatedLines = db.update(OffersHelper.TABLE_NAME, values, selection, selectionArgs);
                break;
            case SINGLE_OFFER:
                String offerUri = OffersHelper._ID + " = " + uri.getLastPathSegment();
                updatedLines = db.update(OffersHelper.TABLE_NAME, values, selection + " AND " + offerUri, selectionArgs);
                break;
        }
        if (updatedLines > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updatedLines;
    }

	@Override
	public Bundle call(String method, String arg, Bundle extras)
	{
		if(method.equals(DELETE_EXPIRED))
		{
			int vDeleteLine = 0;
			
			long vExpTime = new Date().getTime() - 24 * 3600000;
			
			SQLiteDatabase vDb = dbHelper.getWritableDatabase();
			vDeleteLine = vDb.delete(OffersHelper.TABLE_NAME, OffersHelper.EXP_DATE + " < " + String.valueOf(vExpTime), null);
			
			if(vDeleteLine > 0)
			{
				getContext().getContentResolver().notifyChange(OFFERS_URI, null);
			}
		}
		
		return null;
	}
}
