package it.itskennedy.tsaim.geoad.fragment.dialogs;

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

public class EditLocationDescriptionDialogFragment extends DialogFragment  {

	public interface IEditLocationDescriptionDialogFragment {
		public void changeDesc(String newDesc);
	}
	
	private IEditLocationDescriptionDialogFragment iHandler;
	
	private final static int MAX_DESCRIPTION_LENGTH = 120;
	
	public static final String LOCATION_DESCRIPTION_TAG = "locationdescriptiontag";
	
	private EditText mEtLocationDescription;
	
	public static EditLocationDescriptionDialogFragment getNewInstance(String currentDesc) {
		EditLocationDescriptionDialogFragment instance = new EditLocationDescriptionDialogFragment();
		Bundle b = new Bundle();
		b.putString(LOCATION_DESCRIPTION_TAG, currentDesc);
		instance.setArguments(b);
		instance.iHandler = new IEditLocationDescriptionDialogFragment() { public void changeDesc(String newDesc) {} };
		return instance;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		View vView = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_edit_location_desc, null);	
		
		mEtLocationDescription = (EditText) vView.findViewById(R.id.etLocationDesc);
		
		String currentDesc = null;
		if (savedInstanceState == null && (currentDesc = getCurrentDesc()) != null)
			mEtLocationDescription.setText(currentDesc);
		
		builder
			.setTitle(getResources().getString(R.string.edit_description))
			.setView(vView)
			.setPositiveButton(getResources().getString(R.string.edit), new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (iHandler != null) {
						String newDesc = mEtLocationDescription.getText().toString();
						if (newDesc.length() > MAX_DESCRIPTION_LENGTH)
							newDesc = newDesc.substring(0, MAX_DESCRIPTION_LENGTH);
						String currentDesc = getCurrentDesc();
						if (currentDesc != null && !newDesc.equals(currentDesc))
							iHandler.changeDesc(newDesc);
					}
				}
			})
			.setNegativeButton(getResources().getString(R.string.cancel), null);
		
		return builder.create();
	}
	
	private String getCurrentDesc() {
		Bundle args = getArguments();
		if (args != null) return args.getString(LOCATION_DESCRIPTION_TAG);
		return null;
	}

	@Override
	public void setTargetFragment(Fragment fragment, int requestCode) {
		
		if (fragment instanceof IEditLocationDescriptionDialogFragment)
			iHandler = (IEditLocationDescriptionDialogFragment) fragment;
		
		super.setTargetFragment(fragment, requestCode);
	}
}
