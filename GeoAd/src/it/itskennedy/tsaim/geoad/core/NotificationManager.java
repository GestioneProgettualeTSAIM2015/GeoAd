package it.itskennedy.tsaim.geoad.core;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.activity.NewOfferActivity;
import it.itskennedy.tsaim.geoad.entity.LocationModel;
import it.itskennedy.tsaim.geoad.entity.Offer;

/**
 * Created by Marco Zeni on 15/05/2015.
 */
public class NotificationManager
{
    public static void showOffer(Context aContext, Offer aOffer, LocationModel aLocation)
    {
        Intent resultIntent = new Intent(aContext, NewOfferActivity.class);
        resultIntent.putExtra(Offer.BUNDLE_KEY, aOffer.getBundle());
        resultIntent.putExtra(LocationModel.BUNDLE_KEY, aLocation.getBundle());
       
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(aContext);
        stackBuilder.addParentStack(NewOfferActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent vPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        String vName = aLocation != null ? aLocation.getName() : "Unknown";
        String vTitle = aContext.getString(R.string.new_offer) + " " + vName;

        NotificationCompat.Builder vBuilder =
                new NotificationCompat.Builder(aContext)
                        .setSmallIcon(R.drawable.ic_launcher)
                        //.setLargeIcon()
                        .setContentTitle(vTitle)
                        .setContentText(aOffer.getDesc())
                        .setTicker(vTitle)
                        .setContentIntent(vPendingIntent);

        Notification vNotification = vBuilder.build();

        android.app.NotificationManager vNotificationManager = (android.app.NotificationManager) aContext.getSystemService(Context.NOTIFICATION_SERVICE);
        vNotificationManager.notify(aOffer.getId(), vNotification);
    }
}
