package it.itskennedy.tsaim.geoad;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

public class ThumbWithDim extends ImageView 
{
	public final int mWidth;
	public final int mHeight;
	
	public ThumbWithDim(Context aContext, int aW, int aH, String aBase64) 
	{
		super(aContext, null);
		
		mWidth = aW;
		mHeight = aH;
		
		byte[] imageAsBytes = Base64.decode(aBase64.getBytes(), Base64.DEFAULT);
	    setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
	    setPadding(15, 0, 15, 0);
	}

}
