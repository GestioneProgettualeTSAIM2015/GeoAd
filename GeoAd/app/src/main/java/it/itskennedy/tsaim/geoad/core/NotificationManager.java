package it.itskennedy.tsaim.geoad.core;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.activity.NewOfferActivity;
import it.itskennedy.tsaim.geoad.entity.Offer;

/**
 * Created by ITS on 15/05/2015.
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
        PendingIntent vPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        String vTitle = aContext.getString(R.string.new_offer) + " " + aOffer.getLocationName();

        NotificationCompat.Builder vBuilder =
                new NotificationCompat.Builder(aContext)
                        //.setSmallIcon()
                        //.setLargeIcon()
                        .setContentTitle(vTitle)
                        .setContentText(aOffer.getDesc())
                        .setTicker(vTitle)
                        .setContentIntent(vPendingIntent);

        Notification vNotification = vBuilder.build();

        vNotification.flags = Notification.DEFAULT_ALL;
        android.app.NotificationManager vNotificationManager = (android.app.NotificationManager) aContext.getSystemService(Context.NOTIFICATION_SERVICE);
        vNotificationManager.notify(aOffer.getId(), vNotification);
    }
}
