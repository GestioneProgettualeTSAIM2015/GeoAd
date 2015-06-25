package it.itskennedy.tsaim.geoad.core;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.activities.NewOfferActivity;
import it.itskennedy.tsaim.geoad.entity.Offer;

/**
 * Created by Marco Zeni on 15/05/2015.
 */
public class NotificationManager
{
    public static void showOffer(Context aContext, Offer aOffer)
    {
        Intent resultIntent = new Intent(aContext, NewOfferActivity.class);
        resultIntent.putExtra(Offer.BUNDLE_KEY, aOffer.getBundle());
       
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(aContext);
        stackBuilder.addParentStack(NewOfferActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent vPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);

        String vName = aOffer.getLocationName();
        String vTitle = aContext.getString(R.string.new_offer) + " " + vName;

        NotificationCompat.Builder vBuilder =
                new NotificationCompat.Builder(aContext)
                        .setSmallIcon(R.drawable.offer_small)
                        .setContentTitle(vTitle)
                        .setContentText(aOffer.getDesc())
                        .setTicker(vTitle)
                        .setContentIntent(vPendingIntent)
                        .setAutoCancel(true);

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT)
		{
			vBuilder.setLargeIcon(BitmapFactory.decodeResource(aContext.getResources(), R.drawable.offer));
		}
        
        Notification vNotification = vBuilder.build();

        android.app.NotificationManager vNotificationManager = (android.app.NotificationManager) aContext.getSystemService(Context.NOTIFICATION_SERVICE);
        vNotificationManager.notify(aOffer.getId(), vNotification);
    }
}
