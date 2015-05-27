package it.itskennedy.tsaim.geoad.activities;

import it.itskennedy.tsaim.geoad.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		final EditText vEmail = (EditText)findViewById(R.id.editTextEmail);
		final EditText vPassword = (EditText)findViewById(R.id.editTextPassword);
		final EditText vConfirmPassword = (EditText)findViewById(R.id.editTextConfirmPassword);
		
		Button vRegister = (Button)findViewById(R.id.buttonRegister);
		vRegister.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String email = vEmail.getText().toString();
				String password = vPassword.getText().toString();
				String confirmPassword = vConfirmPassword.getText().toString();
				if (email.length() == 0)
				{
					vEmail.setError("Riempire il campo email");
				}
				else if (!password.equals(confirmPassword))
				{
					vPassword.setText("");
					vConfirmPassword.setText("");
					vPassword.setError("Le password non corrispondono");
					vConfirmPassword.setError("Le password non corrispondono");
				}
				else
				{
					registerAccount();
					finish();
				}
			}
		});
		
		Button vCancel = (Button)findViewById(R.id.buttonCancel);
		vCancel.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
	}

	private void registerAccount()
	{
	}

}
