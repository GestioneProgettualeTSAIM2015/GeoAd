package it.itskennedy.tsaim.geoad.fragment;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.interfaces.IFilterDialogFragment;
import it.itskennedy.tsaim.geoad.interfaces.ILoginDialogFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

public class FilterDialogFragment extends DialogFragment
{
	private IFilterDialogFragment mListener;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
         AlertDialog.Builder createProjectAlert = new AlertDialog.Builder(getActivity());
         LayoutInflater inflater = getActivity().getLayoutInflater();
         
         createProjectAlert.setView(inflater.inflate(R.layout.fragment_dialog_filter, null))
            .setPositiveButton("Salva", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int id) {
                	mListener.onFilterSave(new Bundle());
                }
            })
            .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int id) {
                }
            });
         
         return createProjectAlert.create();
    }
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof IFilterDialogFragment)
        {
        	mListener = (IFilterDialogFragment) activity;
        }
    }
}
