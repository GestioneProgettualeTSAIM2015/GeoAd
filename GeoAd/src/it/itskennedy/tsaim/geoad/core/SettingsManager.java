package it.itskennedy.tsaim.geoad.core;

import it.itskennedy.tsaim.geoad.widgets.WidgetProvider;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * Created by Marco Zeni on 13/05/2015.
 */
public class SettingsManager
{
    private static final String PREF_PUSH_ID = "pref_push_id";
    private static final String PREF_APP_VERSION = "pref_app_version";
    private static final String PREF_TOKEN = "pref_token";
    private static final String PREF_HELP = "pref_help";

    private SharedPreferences mPref;
    private Editor mEditor;
    private Context mContext;

    public static SettingsManager get(Context aContext)
    {
        return new SettingsManager(aContext);
    }

    private SettingsManager(Context aContext)
    {
    	mContext = aContext;
        mPref = PreferenceManager.getDefaultSharedPreferences(aContext);
        mEditor = mPref.edit();
    }

    public void savePushId(String aPushId)
    {
        mEditor.putString(PREF_PUSH_ID, aPushId);
        mEditor.commit();
    }

    public String getPushId()
    {
        return mPref.getString(PREF_PUSH_ID, "");
    }

    public int getAppVersion()
    {
        return mPref.getInt(PREF_APP_VERSION, 0);
    }

    public void saveAppVersion(int aVersion)
    {
        mEditor.putInt(PREF_APP_VERSION, aVersion);
        mEditor.commit();
    }

	public void saveToken(String aToken)
	{
		mEditor.putString(PREF_TOKEN, aToken);
		mEditor.commit();
		
		Intent vIntent = new Intent(mContext, WidgetProvider.class);
		vIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		mContext.sendBroadcast(vIntent);
	}

	public String getToken()
	{
		return mPref.getString(PREF_TOKEN, null);
	}

	public boolean isUserLogged() 
	{
		return mPref.getString(PREF_TOKEN, null) != null;
	}

	public boolean getHelpPref() 
	{
		return mPref.getBoolean(PREF_HELP, true);
	}

	public void saveHelpPref(boolean isChecked) 
	{
		mEditor.putBoolean(PREF_HELP, !isChecked);
		mEditor.commit();
	}
}
