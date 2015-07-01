package it.itskennedy.tsaim.geoad.entity;

import it.itskennedy.tsaim.geoad.Utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class Thumb extends ImageView
{
	public final int mId;
	
	public Thumb(Context aContext, int aId, String aBase64) 
	{
		super(aContext, null);
		
		mId = aId;
		setImageBitmap(Utils.base64ToBitmap(aBase64));
		setPadding(13, 0, 13, 0);
	}
}
