package it.itskennedy.tsaim.geoad.fragment.dialogs;

import java.util.Calendar;

import it.itskennedy.tsaim.geoad.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

public class NewOfferDialogFragment extends DialogFragment {
	
	public interface INewOfferDialogFragment {
		public void newOffer(String name, String desc, long expDate);
	}
	
	private INewOfferDialogFragment iHandler;
	
	private final static int MAX_NAME_LENGTH = 30;
	private final static int MAX_DESCRIPTION_LENGTH = 120;
	
	private EditText mEtOfferingName,
					 mEtOfferingDesc;
	
	private DatePicker mDpOfferingExpDate;
	
	public static NewOfferDialogFragment getNewInstance() {
		NewOfferDialogFragment instance = new NewOfferDialogFragment();
		instance.iHandler = null;
		return instance;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		View vView = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_new_offer, null);	
		
		mEtOfferingName = (EditText) vView.findViewById(R.id.etOfferingName);
		mEtOfferingDesc = (EditText) vView.findViewById(R.id.etOfferingDesc);
		mDpOfferingExpDate = (DatePicker) vView.findViewById(R.id.dpOfferingExpDate);
		mDpOfferingExpDate.setMinDate(System.currentTimeMillis() - 1000);
		
		builder
			.setTitle(getResources().getString(R.string.add_offering))
			.setView(vView)
			.setPositiveButton(getResources().getString(R.string.confirm), new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (iHandler != null) {
						
						String name = mEtOfferingName.getText().toString();
						if (name.length() < 1) {
							mEtOfferingName.setError("Name must be at least 1 character!");
							return;
						} else if (name.length() > MAX_NAME_LENGTH)
							name = name.substring(0, MAX_NAME_LENGTH);
						
						String desc = mEtOfferingDesc.getText().toString();
						if (desc.length() > MAX_DESCRIPTION_LENGTH)
							desc = desc.substring(0, MAX_DESCRIPTION_LENGTH);
						
						Calendar calendar = Calendar.getInstance();
						calendar.set(
							mDpOfferingExpDate.getYear(),
							mDpOfferingExpDate.getMonth(),
							mDpOfferingExpDate.getDayOfMonth(), 
							0, 0, 0);
						
						long expDate = calendar.getTimeInMillis();
						
						iHandler.newOffer(name, desc, expDate);
					}
				}
			})
			.setNegativeButton(getResources().getString(R.string.cancel), null);
		
		return builder.create();
	}

	@Override
	public void setTargetFragment(Fragment fragment, int requestCode) {
		
		if (fragment instanceof INewOfferDialogFragment)
			iHandler = (INewOfferDialogFragment) fragment;
		
		super.setTargetFragment(fragment, requestCode);
	}
}
