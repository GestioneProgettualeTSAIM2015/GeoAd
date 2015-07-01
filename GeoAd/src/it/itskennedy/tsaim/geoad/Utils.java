package it.itskennedy.tsaim.geoad;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.gms.appdatasearch.GetRecentContextCall;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

public class Utils
{
	public static final int TYPE_SEARCH_LIST = 0;
	public static final int TYPE_SEARCH_MAP = 4;
	public static final int TYPE_AUGMENTED_REALITY = 1;
	public static final int TYPE_PREFERENCE = 2;
	public static final int TYPE_LOCATIONS = 3;
	public static final int TYPE_FILTER = 5;
	public static final int TYPE_DETAIL = 6;
	
	private static final int MAX_IMAGE_WIDTH = 960;
	private static final int MAX_IMAGE_HEIGHT = 720;
	
	public static Bitmap base64ToBitmap(String aBase64)
	{
		byte[] imageAsBytes = Base64.decode(aBase64.getBytes(), Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
	}

	public static String BitmapToBase64 (Bitmap bitmap) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
		byte[] byteArray = byteArrayOutputStream .toByteArray();
		return Base64.encodeToString(byteArray, Base64.DEFAULT);
	}
	
	public static int getOrientation(Context context, Uri photoUri) {
	    Cursor cursor = context.getContentResolver().query(photoUri,
	            new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

	  
	    if (cursor == null || cursor.getCount() != 1) {
	        return -1;
	    }

	    cursor.moveToFirst();
	    return cursor.getInt(0);
	}
	

	
	public static Bitmap getCorrectlyOrientedImage(Context context, Uri photoUri) throws IOException {
	    InputStream is = context.getContentResolver().openInputStream(photoUri);
	    BitmapFactory.Options dbo = new BitmapFactory.Options();
	    dbo.inJustDecodeBounds = true;
	    BitmapFactory.decodeStream(is, null, dbo);
	    is.close();

	    int rotatedWidth, rotatedHeight;
	    int orientation = getOrientation(context, photoUri);

	    if (orientation == 90 || orientation == 270) {
	        rotatedWidth = dbo.outHeight;
	        rotatedHeight = dbo.outWidth;
	    } else {
	        rotatedWidth = dbo.outWidth;
	        rotatedHeight = dbo.outHeight;
	    }

	    Bitmap srcBitmap;
	    is = context.getContentResolver().openInputStream(photoUri);
	    if (rotatedWidth > MAX_IMAGE_WIDTH || rotatedHeight > MAX_IMAGE_HEIGHT) {
	        float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_WIDTH);
	        float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_HEIGHT);
	        float maxRatio = Math.max(widthRatio, heightRatio);
	        
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inSampleSize = (int) maxRatio;
	        srcBitmap = BitmapFactory.decodeStream(is, null, options);
	    } else {
	        srcBitmap = BitmapFactory.decodeStream(is);
	    }
	    is.close();
	    if (orientation > 0) {
	        Matrix matrix = new Matrix();
	        matrix.postRotate(orientation);

	        srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
	                srcBitmap.getHeight(), matrix, true);
	    }

	    return srcBitmap;
	}
	
	public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
	    int width = bm.getWidth();
	    int height = bm.getHeight();
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    Matrix matrix = new Matrix();
	    matrix.postScale(scaleWidth, scaleHeight);
	    Bitmap resizedBitmap = Bitmap.createBitmap(
	        bm, 0, 0, width, height, matrix, false);
	    return resizedBitmap;
	}
	
	public static File createPhotoFile(){
	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "GeoAd");
	    if (!mediaStorageDir.exists()){
	        if (!mediaStorageDir.mkdirs()){
	            return null;
	        }
	    }
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
	   
	    return mediaFile;
	}
	
	public static void setListViewHeight(ExpandableListView listView,
			int group) {
		ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
		int totalHeight = 0;
		int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
				View.MeasureSpec.EXACTLY);
		for (int i = 0; i < listAdapter.getGroupCount(); i++) {
			View groupItem = listAdapter.getGroupView(i, false, null, listView);
			groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

			totalHeight += groupItem.getMeasuredHeight();

			if (((listView.isGroupExpanded(i)) && (i != group))
					|| ((!listView.isGroupExpanded(i)) && (i == group))) {
				for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
					View listItem = listAdapter.getChildView(i, j, false, null,
							listView);
					listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

					totalHeight += listItem.getMeasuredHeight();

				}
			}
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		int height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
		if (height < 10)
			height = 200;
		params.height = height;
		listView.setLayoutParams(params);
		listView.requestLayout();

	}
	
	public static final String LOC_TYPE_CA = "ca";
	public static final String LOC_TYPE_POI = "poi";
	
	public static final String PRIMARY_CATEGORY = "PRIMARY_CATEGORY";
	public static final String SECONDARY_CATEGORY = "SECONDARY_CATEGORY";
	public static final String RADIUS = "RADIUS";
	public static final String NAME = "NAME";
	public static final String FILTER_CATEGORY = "FILTER_CATEGORY";
	public static final String LOCATION_URL_TEMPLATE = "api/locationsfilter/around?P.Lat=%s&P.Lng=%s&R=%s%s";
	public static final int DEFAULT_RADIUS = 8;
	
	public static final String LOCATIONS_LIST = "LOCATIONS_LIST";
}
