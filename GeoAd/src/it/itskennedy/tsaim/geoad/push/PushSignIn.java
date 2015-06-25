package it.itskennedy.tsaim.geoad.push;

import java.io.IOException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import it.itskennedy.tsaim.geoad.core.Engine;
import it.itskennedy.tsaim.geoad.core.SettingsManager;

public class PushSignIn
{   
	private static boolean mPending = false;
	
    private String mPushId;
    private Context mContext;
	private GoogleCloudMessaging mGoogleCloudMessaging;
	private PushKeyReceiver mReceiver;
    
	public interface PushKeyReceiver
	{
		void onKey(String aKey);
	}
	
    public PushSignIn(Context aContext, PushKeyReceiver aReceiver)
    {
    	mContext = aContext;
    	mReceiver = aReceiver;
    	
    	if (checkPlayServices())
    	{ 
    		mPushId = getPushId();

            if (mPushId.isEmpty())
            {
            	if(!mPending)
            	{
            		mPending = true;
                    registerInBackground();
            	}
            }
            else
            {
            	if(mReceiver != null)
            	{
            		mReceiver.onKey(mPushId);
            	}
            	
            	Log.w(Engine.APP_NAME, mPushId);
            	
            	Log.d(Engine.APP_NAME, "Push Key Already Stored");
            }    
        }
    	else 
    	{
            Log.d(Engine.APP_NAME, "No valid Google Play Services APK found");
        }	
    }
    
    private boolean checkPlayServices() 
	{
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
	    
	    if (resultCode != ConnectionResult.SUCCESS) 
		{
	        return false;
	    }
	    
	    return true;
	}
    
    private String getPushId() 
	{
    	String vPushId = SettingsManager.get(mContext).getPushId();
    	int vLastVersion = SettingsManager.get(mContext).getAppVersion();
	    
	    if (vPushId.isEmpty()) 
	    {
	        return "";
	    }
	   
	    int vCurrentVersion = getAppVersion(mContext);
	    if (vLastVersion != vCurrentVersion) 
	    {
	        return "";
	    }
	    
	    return vPushId;
	}
	
	private int getAppVersion(Context context) 
	{
	    try 
	    {
	        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } 
	    catch (NameNotFoundException e) 
	    {
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	private void registerInBackground() 
	{
		mGoogleCloudMessaging = GoogleCloudMessaging.getInstance(mContext);
		
	    new AsyncTask<Void, Void, Void>() 
	    {
	        @Override
	        protected Void doInBackground(Void... params) 
	        {
	            try 
	            {
	                if (mGoogleCloudMessaging == null) 
	                {
	                	mGoogleCloudMessaging = GoogleCloudMessaging.getInstance(mContext);
	                }
	                
	                mPushId = mGoogleCloudMessaging.register(Engine.PROJECT_NUMBER);
	                Log.d(Engine.APP_NAME, "App Registered for Push");
	            } 
	            catch (IOException ex) 
	            {
	                
	            }
	            
	            return null;
	        }

			@Override
			protected void onPostExecute(Void result)
			{
				mPending = false;
				
				if(mReceiver != null)
		    	{
		    		mReceiver.onKey(mPushId);
		    	}
				savePushKey();
			}   
	    }.execute(null, null, null);
	}
	
	private void savePushKey()
	{
		SettingsManager vSettings = SettingsManager.get(mContext);

		vSettings.savePushId(mPushId);
		vSettings.saveAppVersion(getAppVersion(mContext));
	}
}
