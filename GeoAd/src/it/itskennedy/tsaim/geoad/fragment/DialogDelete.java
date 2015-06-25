package it.itskennedy.tsaim.geoad.fragment;

import it.itskennedy.tsaim.geoad.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;

public class DialogDelete extends DialogFragment {

	public static final String LOCATION_ID = "location";
	public static final String GROUP_ID = "group";
	public static final String TAG = "dialog_delete";
	public static final String MESSAGE = "message";
	
	public static final int DELETE_CODE = 1000;
	
	private int mId, mGroup;
	private String mMess;
	
	public static DialogDelete getInstance(int aGroup, int aId, String aMessage)
	{
		DialogDelete instance = new DialogDelete();
		Bundle b = new Bundle();
		b.putInt(LOCATION_ID, aId);
		b.putInt(GROUP_ID, aGroup);
		b.putString(MESSAGE, aMessage);
		instance.setArguments(b);
		return instance;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mId = getArguments().getInt(LOCATION_ID);
		mMess = getArguments().getString(MESSAGE);
		mGroup = getArguments().getInt(GROUP_ID);	
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		if(mMess == null)
		{
			if(mGroup == 0)
			{
				builder.setTitle(getString(R.string.remove_marked_fav));
			}
			else
			{
				builder.setTitle(getString(R.string.remove_marked_ign));
			}
		}
		else
		{
			builder.setTitle(mMess);
		}
		
		
		builder.setPositiveButton("Si", new OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Intent vData = new Intent();
				vData.putExtra(GROUP_ID, mGroup);
				vData.putExtra(LOCATION_ID, mId);
				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, vData);
			}
		});
		builder.setNegativeButton("No", null);
		
		Dialog vDialog = builder.create();
		return vDialog;
	}
}