package it.itskennedy.tsaim.geoad.activities;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.RequestParams;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.Utils;
import it.itskennedy.tsaim.geoad.core.ConnectionManager;
import it.itskennedy.tsaim.geoad.core.Engine;
import it.itskennedy.tsaim.geoad.core.SettingsManager;
import it.itskennedy.tsaim.geoad.core.ConnectionManager.JsonResponse;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity
{
	private EditText mEmail;
	private EditText mPassword;
	private EditText mConfirmPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		mEmail = (EditText)findViewById(R.id.editTextEmail);
		mPassword = (EditText)findViewById(R.id.editTextPassword);
		mConfirmPassword = (EditText)findViewById(R.id.editTextConfirmPassword);
		
		Button vRegister = (Button)findViewById(R.id.buttonRegister);
		vRegister.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String email = mEmail.getText().toString();
				String password = mPassword.getText().toString();
				String confirmPassword = mConfirmPassword.getText().toString();
				if (email.length() == 0)
				{
					mEmail.setError("Riempire il campo email");
				}
				else if (!password.equals(confirmPassword))
				{
					mPassword.setText("");
					mConfirmPassword.setText("");
					mPassword.setError("Le password non corrispondono");
					mConfirmPassword.setError("Le password non corrispondono");
				}
				else
				{
					registerAccount();
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
		JSONObject vObjReg = new JSONObject();
		
		try 
		{
			vObjReg.put("ConfirmPassword", mConfirmPassword.getText().toString());
			vObjReg.put("Email", mEmail.getText().toString());
			vObjReg.put("Password", mPassword.getText().toString());
		} 
		catch (JSONException e)
		{
			Log.e(Engine.APP_NAME, "Json Compose Error!");
		}
		
		final ConnectionManager vCM = ConnectionManager.obtain();
		
		vCM.post("api/Account/Register", vObjReg, new JsonResponse()
		{	
			@Override
			public void onResponse(boolean aResult, Object aResponse)
			{
				Toast.makeText(RegisterActivity.this, "REGISTRATO", Toast.LENGTH_SHORT).show();
				RequestParams vParams = new RequestParams();
				
				vParams.put("UserName", mEmail.getText().toString());
				vParams.put("password", mPassword.getText().toString());
				vParams.put("grant_type", "password");
				
				vCM.post("Token", vParams, new JsonResponse()
				{	
					@Override
					public void onResponse(boolean aResult, Object aResponse)
					{
						if(aResult && aResponse != null)
						{
							String aToken = "";
							try
							{
								aToken = ((JSONObject)aResponse).getString("access_token");
								SettingsManager.get(RegisterActivity.this).saveToken(aToken);
								Engine.get().setToken(aToken);
								
								SettingsManager.get(RegisterActivity.this).saveUserLogged(true);
							
								Intent vMainAct = new Intent(RegisterActivity.this, MainActivity.class);
								startActivity(vMainAct);
								finish();
							} 
							catch (JSONException e)
							{
								Log.e(Engine.APP_NAME, "JSON Decode Errore");
							}
						}
					}
				});	
			}
		});
	}
}
