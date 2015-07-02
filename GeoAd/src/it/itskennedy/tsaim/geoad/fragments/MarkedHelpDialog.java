package it.itskennedy.tsaim.geoad.fragments;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.core.SettingsManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MarkedHelpDialog extends DialogFragment 
{
	public static final String TAG = "help_dialog";
	
	public static MarkedHelpDialog get() 
	{
		MarkedHelpDialog instance = new MarkedHelpDialog();
		return instance;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		View vView = getActivity().getLayoutInflater().inflate(R.layout.marked_help_layout, null);	
		
		CheckBox vNotShowAgain = (CheckBox) vView.findViewById(R.id.checkBoxDontShowAgain);
		vNotShowAgain.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{	
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				SettingsManager.get(getActivity()).saveHelpPref(isChecked);
			}
		});
		
		builder.setView(vView);
		builder.setNegativeButton(getString(R.string.close), null);

		Dialog vDialog = builder.create();
		return vDialog;
	}
}