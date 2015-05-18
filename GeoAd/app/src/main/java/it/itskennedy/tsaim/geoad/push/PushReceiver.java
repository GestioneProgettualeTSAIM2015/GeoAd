package it.itskennedy.tsaim.geoad.push;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import it.itskennedy.tsaim.geoad.core.Engine;
import it.itskennedy.tsaim.geoad.services.PushService;

public class PushReceiver extends WakefulBroadcastReceiver 
{
    @Override
    public void onReceive(Context context, Intent intent) 
    {
        Log.d(Engine.APP_NAME, "Push received");

        ComponentName comp = new ComponentName(context.getPackageName(), PushService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK); 
    }
}