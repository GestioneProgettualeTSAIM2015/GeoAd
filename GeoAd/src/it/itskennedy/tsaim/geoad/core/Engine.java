package it.itskennedy.tsaim.geoad.core;

import android.app.Application;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.push.PushSignIn;
import it.itskennedy.tsaim.geoad.push.PushSignIn.PushKeyReceiver;
import it.itskennedy.tsaim.geoad.services.GeoAdService;

/**
 * Created by Marco Zeni on 13/05/2015.
 */
public class Engine extends Application implements PushKeyReceiver
{
    public static String SERVER_URL = "";
    public static String APP_NAME = "GeoAd";
    public static String PROJECT_NUMBER = "";

    private static Engine mInstance;
	private String mKey;
	private String mToken;

    @Override
    public void onCreate()
    {
        super.onCreate();
        
        Intent vService = new Intent(this, GeoAdService.class);
        startService(vService);

        setVariables();
        
        mToken = SettingsManager.get(this).getToken();
        
        new PushSignIn(this, this);

        mInstance = this;
    }

    public static Engine get()
    {
    	return mInstance;
    }

    public void setVariables()
    {
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(
                    new InputStreamReader(getResources().openRawResource(R.raw.config)));

            JSONObject vObj = new JSONObject(reader.readLine());

            SERVER_URL = vObj.getString("server_url");
            PROJECT_NUMBER = vObj.getString("project_num");

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        } 
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                } 
                catch (IOException e)
                {
                }
            }
        }
    }

	public String getKey() 
	{
		if(mKey != null)
		{
			return mKey;
		}
		
		new PushSignIn(this, this);
		
		return null;
	}
	
	public String getToken()
	{
		return mToken;
	}

	@Override
	public void onKey(String aKey)
	{
		mKey = aKey;
	}

	public void setToken(String aToken) 
	{
		mToken = aToken;
	}
}
