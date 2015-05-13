package it.itskennedy.tsaim.geoad.services;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

public abstract class InAppLocationReceiver extends BroadcastReceiver
{
	public static IntentFilter getIntentFilter()
	{
		return new IntentFilter(LocationService.LOCATION_ACTION);
	}
}
