package it.itskennedy.tsaim.geoad.fragments.dialogs;

import it.itskennedy.tsaim.geoad.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EditLocationNameDialogFragment extends DialogFragment {
	
	public interface IEditLocationNameDialogFragment {
		public void changeName(String newName);
	}
	
	private IEditLocationNameDialogFragment iHandler;
	
	private final static int MAX_NAME_LENGTH = 30;
	
	public static final String LOCATION_NAME_TAG = "locationnametag";
	
	private EditText mEtLocationName;
	
	public static EditLocationNameDialogFragment getNewInstance(String currentName) {
		EditLocationNameDialogFragment instance = new EditLocationNameDialogFragment();
		Bundle b = new Bundle();
		b.putString(LOCATION_NAME_TAG, currentName);
		instance.setArguments(b);
		instance.iHandler = new IEditLocationNameDialogFragment() { public void changeName(String newName) {} };
		return instance;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		View vView = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_edit_location_name, null);	
		
		mEtLocationName = (EditText) vView.findViewById(R.id.etLocationName);
		
		String currentName = null;
		if (savedInstanceState == null && (currentName = getCurrentName()) != null)
			mEtLocationName.setText(currentName);
		
		builder
			.setTitle(getResources().getString(R.string.edit_name))
			.setView(vView)
			.setPositiveButton(getResources().getString(R.string.edit), new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (iHandler != null) {
						String newName = mEtLocationName.getText().toString();
						if (newName.length() < 1) {
							mEtLocationName.setError("Name must be at least 1 character!");
							return;
						} else if (newName.length() > MAX_NAME_LENGTH)
							newName = newName.substring(0, MAX_NAME_LENGTH);
						String currentName = getCurrentName();
						if (currentName != null && !newName.equals(currentName))
							iHandler.changeName(newName);
					}
				}
			})
			.setNegativeButton(getResources().getString(R.string.cancel), null);
		
		return builder.create();
	}
	
	private String getCurrentName() {
		Bundle args = getArguments();
		if (args != null) return args.getString(LOCATION_NAME_TAG);
		return null;
	}

	@Override
	public void setTargetFragment(Fragment fragment, int requestCode) {
		
		if (fragment instanceof IEditLocationNameDialogFragment)
			iHandler = (IEditLocationNameDialogFragment) fragment;
		
		super.setTargetFragment(fragment, requestCode);
	}
}
