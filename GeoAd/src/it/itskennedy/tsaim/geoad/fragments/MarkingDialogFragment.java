package it.itskennedy.tsaim.geoad.fragments;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.core.Engine.LocationState;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class MarkingDialogFragment extends DialogFragment 
{
	public static final String TAG = "marking_dialog";
	public static final String ID = "id";
	protected static final String LOCATION_STATE = "locationstate";
	private static final String IGNORABLE = "ignorable";
	
	public static MarkingDialogFragment get(LocationState aState, boolean vIsIgnorable) 
	{
		MarkingDialogFragment instance = new MarkingDialogFragment();
		Bundle vBundle = new Bundle();
		vBundle.putSerializable(LOCATION_STATE, aState.ordinal());
		vBundle.putBoolean(IGNORABLE, vIsIgnorable);
		instance.setArguments(vBundle);
		return instance;
	}

	private boolean mIsIgnorable;
	private Switch mFavSwitch;
	private Switch mIgnSwitch;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		View vView = getActivity().getLayoutInflater().inflate(R.layout.marking_dialog, null);	
		builder.setView(vView);
		
		mFavSwitch = (Switch) vView.findViewById(R.id.switchFavourite);
		mFavSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{	
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				if(isChecked)
				{
					mIgnSwitch.setChecked(false);
				}
			}
		});
		
		mIgnSwitch = (Switch) vView.findViewById(R.id.switchIgnored);
		mIgnSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{	
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				if(isChecked)
				{
					mFavSwitch.setChecked(false);
				}
			}
		});
		
		mIsIgnorable = getArguments().getBoolean(IGNORABLE);
		if(!mIsIgnorable)
		{
			mIgnSwitch.setVisibility(Switch.GONE);
		}
		
		LocationState vState = LocationState.values()[getArguments().getInt(LOCATION_STATE, 0)];
		
		if(vState == LocationState.FAVORITE)
		{
			mFavSwitch.setChecked(true);
		}
		else if(mIsIgnorable && vState == LocationState.IGNORED)
		{
			mIgnSwitch.setChecked(true);
		}
		
		builder.setTitle(getString(R.string.location_state));
		
		builder.setPositiveButton(getString(R.string.save), new OnClickListener()
		{	
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Fragment vTarget = getTargetFragment();
				if(vTarget != null)
				{
					Intent vData = new Intent();
					LocationState vState = LocationState.NEUTRAL;
					
					if(mFavSwitch.isChecked())
					{
						vState = LocationState.FAVORITE;
					}
					else if(mIgnSwitch.isChecked())
					{
						vState = LocationState.IGNORED;
					}
					
					vData.putExtra(LOCATION_STATE, vState.ordinal());
					vTarget.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, vData);
				}
			}
		});
		builder.setNegativeButton(getString(R.string.close), null);

		Dialog vDialog = builder.create();
		return vDialog;
	}
}