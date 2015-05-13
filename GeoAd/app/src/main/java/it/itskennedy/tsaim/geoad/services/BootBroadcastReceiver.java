package it.itskennedy.tsaim.geoad.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver 
{
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		Log.d("GeoAd", "Boot Completed");
		
		Intent vService = new Intent(context, LocationService.class);
		context.startService(vService);
	}
}
