package it.itskennedy.tsaim.geoad.fragment;

import it.itskennedy.tsaim.geoad.MainActivity;
import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.activities.RegisterActivity;
import it.itskennedy.tsaim.geoad.interfaces.ILoginDialogFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginDialogFragment extends DialogFragment
{
	private ILoginDialogFragment mListener;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
         AlertDialog.Builder createProjectAlert = new AlertDialog.Builder(getActivity());
         LayoutInflater inflater = getActivity().getLayoutInflater();
         
         createProjectAlert.setView(inflater.inflate(R.layout.fragment_dialog_login, null))
            .setPositiveButton("Login", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int id) {
                	EditText textMail = (EditText)getDialog().findViewById(R.id.editTextMail);
                	EditText textPassword = (EditText)getDialog().findViewById(R.id.editTextPassword);
                	
                	mListener.onLoginButtonPressed(textMail.getText().toString(), textPassword.getText().toString());
                }
            })
            .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int id) {
                	mListener.onCancelButtonPressed();
                }
            })
            .setNeutralButton("Registrati", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int id) {
                	mListener.onRegisterButtonPressed();
                }
            })
            .setCancelable(false);
         
         return createProjectAlert.create();
    }
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ILoginDialogFragment)
        {
        	mListener = (ILoginDialogFragment) activity;
        }
    }
}
