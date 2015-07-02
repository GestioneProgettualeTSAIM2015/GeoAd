package it.itskennedy.tsaim.geoad.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import it.itskennedy.tsaim.geoad.core.Engine;
import it.itskennedy.tsaim.geoad.services.GeoAdService;

public class BootBroadcastReceiver extends BroadcastReceiver 
{
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		Log.d(Engine.APP_NAME, "Boot Completed");
		
		Intent vService = new Intent(context, GeoAdService.class);
		context.startService(vService);
	}
}
