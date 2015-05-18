package it.itskennedy.tsaim.geoad.receiver;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

import it.itskennedy.tsaim.geoad.services.PushService;

public abstract class InAppPushReceiver extends BroadcastReceiver
{
	public static IntentFilter getIntentFilter()
	{
		return new IntentFilter(PushService.PUSH_ACTION);
	}
}
