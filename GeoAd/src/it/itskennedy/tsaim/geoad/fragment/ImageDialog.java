package it.itskennedy.tsaim.geoad.fragment;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.Utils;
import it.itskennedy.tsaim.geoad.core.ConnectionManager;
import it.itskennedy.tsaim.geoad.core.Engine;
import it.itskennedy.tsaim.geoad.core.ConnectionManager.JsonResponse;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImageDialog extends DialogFragment 
{
	public static final String TAG = "image_dialog";
	public static final String ID = "id";
	
	public static ImageDialog get(int aId) 
	{
		ImageDialog instance = new ImageDialog();
		Bundle b = new Bundle();
		b.putInt(ID, aId);
		instance.setArguments(b);
		return instance;
	}

	private ProgressBar mProgress;
	private ImageView mImage;
	private int mId;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		View vView = getActivity().getLayoutInflater().inflate(R.layout.image_dialog, null);	
		builder.setView(vView);
		
		mId = getArguments().getInt(ID);
		mProgress = (ProgressBar) vView.findViewById(R.id.progressBarDialogImage);
		mImage = (ImageView) vView.findViewById(R.id.imageViewDialog);
		
		String vCacheImage = Engine.get().getCache().getImage(mId); 
		if(vCacheImage == null)
		{
			ConnectionManager.obtain().get("api/photos/data/" + mId, null, new JsonResponse()
			{	
				@Override
				public void onResponse(boolean aResult, Object aResponse)
				{
					if(aResult)
					{
						Engine.get().getCache().cacheImage((String) aResponse, mId);
						showImage((String) aResponse);	
					}
				}
			});
		}
		else
		{
			showImage(vCacheImage);
		}
		
		builder.setNegativeButton("Chiudi", null);

		Dialog vDialog = builder.create();
		return vDialog;
	}
	
	private void showImage(String aBase64)
	{
		mProgress.setVisibility(ProgressBar.INVISIBLE);
		mImage.setImageBitmap(Utils.base64ToBitmap(aBase64));
	}
}