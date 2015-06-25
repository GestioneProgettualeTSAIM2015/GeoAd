package it.itskennedy.tsaim.geoad.localdb;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Cado on 13/05/2015.
 */
public class DataFavContentProvider extends ContentProvider {
    public static final String AUTHORITY = "it.itskennedy.tsaim.geoad.localdb.datafavcontentprovider";

    public static final String FAVORITES_PATH = "favorites";
    public static final String IGNORED_PATH = "ignored";
    public static final String MYLOC_PATH = "mylocations";

    public static final Uri FAVORITES_URI =
            Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTHORITY + "/" + FAVORITES_PATH);
    public static final Uri IGNORED_URI =
            Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTHORITY + "/" + IGNORED_PATH);
    public static final Uri MYLOC_URI =
            Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTHORITY + "/" + MYLOC_PATH);

    private static final int FULL_FAVORITES_TABLE = 0;
    private static final int SINGLE_FAVORITE = 1;

    private static final int FULL_IGNORED_TABLE = 2;
    private static final int SINGLE_IGNORED = 3;
    
    private static final int FULL_MYLOC_TABLE = 4;
    private static final int SINGLE_MYLOC = 5;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, FAVORITES_PATH, FULL_FAVORITES_TABLE);
        uriMatcher.addURI(AUTHORITY, FAVORITES_PATH + "/#", SINGLE_FAVORITE);

        uriMatcher.addURI(AUTHORITY, IGNORED_PATH, FULL_IGNORED_TABLE);
        uriMatcher.addURI(AUTHORITY, IGNORED_PATH + "/#", SINGLE_IGNORED);
        
        uriMatcher.addURI(AUTHORITY, MYLOC_PATH, FULL_MYLOC_TABLE);
        uriMatcher.addURI(AUTHORITY, MYLOC_PATH + "/#", SINGLE_MYLOC);
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
            case FULL_FAVORITES_TABLE:
                queryBuilder.setTables(FavoritesHelper.TABLE_NAME);
                break;

            case SINGLE_FAVORITE:
                queryBuilder.setTables(FavoritesHelper.TABLE_NAME);
                queryBuilder.appendWhere(FavoritesHelper._ID + "=" + uri.getLastPathSegment());
                break;

            case FULL_IGNORED_TABLE:
                queryBuilder.setTables(IgnoredHelper.TABLE_NAME);
                break;

            case SINGLE_IGNORED:
                queryBuilder.setTables(IgnoredHelper.TABLE_NAME);
                queryBuilder.appendWhere(IgnoredHelper._ID + "=" + uri.getLastPathSegment());
                break;
                
            case FULL_MYLOC_TABLE:
                queryBuilder.setTables(MyLocationHelper.TABLE_NAME);
                break;

            case SINGLE_MYLOC:
                queryBuilder.setTables(MyLocationHelper.TABLE_NAME);
                queryBuilder.appendWhere(MyLocationHelper._ID + "=" + uri.getLastPathSegment());
                break;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    public static final String MIME_TYPE_ALL_FAVORITES = ContentResolver.CURSOR_DIR_BASE_TYPE + "/allfavorites";
    public static final String MIME_TYPE_SINGLE_FAVORITE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/singlefavorite";

    public static final String MIME_TYPE_ALL_IGNORED = ContentResolver.CURSOR_DIR_BASE_TYPE + "/allignored";
    public static final String MIME_TYPE_SINGLE_IGNORED = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/singleignored";

    public static final String MIME_TYPE_ALL_MYLOC = ContentResolver.CURSOR_DIR_BASE_TYPE + "/allmy";
    public static final String MIME_TYPE_SINGLE_MYLOC = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/singlemy";

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case FULL_FAVORITES_TABLE:
                return MIME_TYPE_ALL_FAVORITES;
            case SINGLE_FAVORITE:
                return MIME_TYPE_SINGLE_FAVORITE;
            case FULL_IGNORED_TABLE:
                return MIME_TYPE_ALL_IGNORED;
            case SINGLE_IGNORED:
                return MIME_TYPE_SINGLE_IGNORED;
            case FULL_MYLOC_TABLE:
                return MIME_TYPE_ALL_MYLOC;
            case SINGLE_MYLOC:
                return MIME_TYPE_SINGLE_MYLOC;
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long result;
        switch (uriMatcher.match(uri)) {
            case FULL_FAVORITES_TABLE:
                result = db.insert(FavoritesHelper.TABLE_NAME, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Uri.parse(FAVORITES_URI.toString() + "/" + result);
            case FULL_IGNORED_TABLE:
                result = db.insert(IgnoredHelper.TABLE_NAME, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Uri.parse(IGNORED_URI.toString() + "/" + result);
            case FULL_MYLOC_TABLE:
                result = db.insert(MyLocationHelper.TABLE_NAME, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Uri.parse(MYLOC_URI.toString() + "/" + result);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deletedLines = 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case FULL_FAVORITES_TABLE:
                deletedLines = db.delete(FavoritesHelper.TABLE_NAME, selection, selectionArgs);
                break;
            case SINGLE_FAVORITE:
                String favUri = FavoritesHelper._ID + " = " + uri.getLastPathSegment();
                deletedLines = db.delete(FavoritesHelper.TABLE_NAME, favUri, selectionArgs);
                break;
            case FULL_IGNORED_TABLE:
                deletedLines = db.delete(IgnoredHelper.TABLE_NAME, selection, selectionArgs);
                break;
            case SINGLE_IGNORED:
                String ignoreUri = IgnoredHelper._ID + " = " + uri.getLastPathSegment();
                deletedLines = db.delete(IgnoredHelper.TABLE_NAME, ignoreUri, selectionArgs);
                break;
            case FULL_MYLOC_TABLE:
                deletedLines = db.delete(MyLocationHelper.TABLE_NAME, selection, selectionArgs);
                break;
            case SINGLE_MYLOC:
                String myUri = MyLocationHelper._ID + " = " + uri.getLastPathSegment();
                deletedLines = db.delete(MyLocationHelper.TABLE_NAME, myUri, selectionArgs);
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

            case FULL_FAVORITES_TABLE:
                updatedLines = db.update(FavoritesHelper.TABLE_NAME, values, selection, selectionArgs);
                break;
            case SINGLE_FAVORITE:
                String favUri = FavoritesHelper._ID + " = " + uri.getLastPathSegment();
                updatedLines = db.update(FavoritesHelper.TABLE_NAME, values, favUri, selectionArgs);
                break;
            case FULL_IGNORED_TABLE:
                updatedLines = db.update(IgnoredHelper.TABLE_NAME, values, selection, selectionArgs);
                break;
            case SINGLE_IGNORED:
                String ignoreUri = IgnoredHelper._ID + " = " + uri.getLastPathSegment();
                updatedLines = db.update(IgnoredHelper.TABLE_NAME, values, ignoreUri, selectionArgs);
                break;
            case FULL_MYLOC_TABLE:
                updatedLines = db.update(MyLocationHelper.TABLE_NAME, values, selection, selectionArgs);
                break;
            case SINGLE_MYLOC:
                String myUri = MyLocationHelper._ID + " = " + uri.getLastPathSegment();
                updatedLines = db.update(MyLocationHelper.TABLE_NAME, values, myUri, selectionArgs);
                break;
        }
        if (updatedLines > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updatedLines;
    }
}
