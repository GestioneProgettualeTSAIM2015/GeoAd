package it.itskennedy.tsaim.geoad.core;

import android.content.Context;
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

    private SharedPreferences mPref;
    private Editor mEditor;

    public static SettingsManager get(Context aContext)
    {
        return new SettingsManager(aContext);
    }

    private SettingsManager(Context aContext)
    {
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
}
