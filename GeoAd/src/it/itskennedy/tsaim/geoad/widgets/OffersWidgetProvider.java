package it.itskennedy.tsaim.geoad.widgets;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.activities.NewOfferActivity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class OffersWidgetProvider extends AppWidgetProvider
{
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
		                           new ComponentName(context, OffersWidgetProvider.class));
		appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.offers);
		
		RemoteViews widget = getRemoteView(context); 
		appWidgetManager.updateAppWidget(appWidgetIds, widget);
	}

	@Override
    public void onUpdate(final Context aContext, final AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
		for (int i = 0; i < appWidgetIds.length; i++) 
		{
			RemoteViews widget = getRemoteView(aContext);
			appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
		}
		
		super.onUpdate(aContext, appWidgetManager, appWidgetIds);
    }
	
	private RemoteViews getRemoteView(Context context) 
	{
		RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget_offers_layout);
		Intent vIntent = new Intent(context, OffersWidgetService.class);
		widget.setRemoteAdapter(R.id.offers, vIntent);
		
	    Intent vClick = new Intent(context, NewOfferActivity.class);
	    PendingIntent vClickPending = PendingIntent.getActivity(context, 0, vClick, PendingIntent.FLAG_UPDATE_CURRENT);  
	    widget.setPendingIntentTemplate(R.id.offers, vClickPending);
		return widget;
	}
}