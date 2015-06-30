package it.itskennedy.tsaim.geoad;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Utils
{
	public static final int TYPE_SEARCH_LIST = 0;
	public static final int TYPE_SEARCH_MAP = 4;
	public static final int TYPE_AUGMENTED_REALITY = 1;
	public static final int TYPE_PREFERENCE = 2;
	public static final int TYPE_LOCATIONS = 3;
	public static final int TYPE_FILTER = 5;
	public static final int TYPE_DETAIL = 6;
	
	public static Bitmap base64ToBitmap(String aBase64)
	{
		byte[] imageAsBytes = Base64.decode(aBase64.getBytes(), Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
	}
	
	public static final String LOC_TYPE_CA = "ca";
	public static final String LOC_TYPE_POI = "poi";
	
	public static final String PRIMARY_CATEGORY = "PRIMARY_CATEGORY";
	public static final String SECONDARY_CATEGORY = "SECONDARY_CATEGORY";
	public static final String RADIUS = "RADIUS";
	public static final String NAME = "NAME";
	public static final String FILTER_CATEGORY = "FILTER_CATEGORY";
	public static final String LOCATION_URL_TEMPLATE = "http://geoad.somee.com/api/locationsfilter/around?P.Lat=%s&P.Lng=%s&R=%s%s";
	public static final int DEFAULT_RADIUS = 8;
	
	public static final String LOCATIONS_LIST = "LOCATIONS_LIST";
}
