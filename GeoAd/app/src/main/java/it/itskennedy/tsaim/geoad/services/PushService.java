package it.itskennedy.tsaim.geoad.services;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import it.itskennedy.tsaim.geoad.core.NotificationManager;
import it.itskennedy.tsaim.geoad.entity.Offer;
import it.itskennedy.tsaim.geoad.push.PushReceiver;

public class PushService extends IntentService 
{
	public static final String PUSH_ACTION = "push_action";

	public PushService() 
	{
		super("PushService");
	}

	@Override
	protected void onHandleIntent(Intent intent) 
	{
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty())
		{
			if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
			{
				String vJson = extras.getString("json_obj");
				Offer vOffer = Offer.fromJSON(vJson);

				NotificationManager.showOffer(this, vOffer);

				//TODO cadorin
				//getContentResolver().insert(URI, vOffer.getContentValues());

			}
		}

		PushReceiver.completeWakefulIntent(intent);
	}
}
