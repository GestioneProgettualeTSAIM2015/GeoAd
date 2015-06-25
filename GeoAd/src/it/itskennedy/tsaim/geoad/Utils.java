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
}
