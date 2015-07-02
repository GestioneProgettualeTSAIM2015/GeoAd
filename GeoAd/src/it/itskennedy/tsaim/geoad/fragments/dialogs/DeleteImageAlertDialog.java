package it.itskennedy.tsaim.geoad.fragments.dialogs;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.core.ConnectionManager;
import it.itskennedy.tsaim.geoad.core.Routes;
import it.itskennedy.tsaim.geoad.core.ConnectionManager.JsonResponse;
import it.itskennedy.tsaim.geoad.fragments.EditLocationFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class DeleteImageAlertDialog extends DialogFragment {

	public static final String TAG = "image_delete_dialog";
	private static final String ID = "id";
	private int id;
	
	public static DeleteImageAlertDialog getInstance(int id) {
		DeleteImageAlertDialog vDialog = new DeleteImageAlertDialog();
		Bundle bundle = new Bundle();
		bundle.putInt(ID, id);
		vDialog.setArguments(bundle);
		
		return vDialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		Bundle bundle = getArguments();
		if (bundle != null) {
			id = bundle.getInt(ID);
		}
		
		builder.setTitle(getResources().getString(R.string.delete_photo_dialog_title));
		
		builder.setMessage(getResources().getString(R.string.delete_photo_dialog_text));
		
		builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				Fragment f = getTargetFragment();
				if (f instanceof EditLocationFragment)
					((EditLocationFragment)f).deletePhoto(id);
			}
		});
		
		builder.setNegativeButton(getResources().getString(R.string.cancel), null);
		
		Dialog dialog = builder.create();
		return dialog;
	}
}
