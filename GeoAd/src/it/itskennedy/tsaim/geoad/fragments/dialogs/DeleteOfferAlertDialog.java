package it.itskennedy.tsaim.geoad.fragments.dialogs;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.fragments.EditLocationFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class DeleteOfferAlertDialog extends DialogFragment {

	public static final String TAG = "offer_delete_dialog";
	private static final String ID = "id";
	private static final String POSITION = "position";
	
	public static DeleteOfferAlertDialog getInstance(int position, long id) {
		DeleteOfferAlertDialog vDialog = new DeleteOfferAlertDialog();
		Bundle bundle = new Bundle();
		bundle.putLong(ID, id);
		bundle.putInt(POSITION, position);
		vDialog.setArguments(bundle);
		
		return vDialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setTitle(getResources().getString(R.string.delete_offer_dialog_title));
		
		builder.setMessage(getResources().getString(R.string.delete_offer_dialog_text));
		
		builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				Fragment f = getTargetFragment();
				Bundle args = getArguments();
				if (f instanceof EditLocationFragment && args != null) {					
					((EditLocationFragment)f).deleteOffer(
							args.getInt(POSITION),
							args.getLong(ID));
				}
			}
		});
		
		builder.setNegativeButton(getResources().getString(R.string.cancel), null);
		
		Dialog dialog = builder.create();
		return dialog;
	}
}
