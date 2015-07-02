package it.itskennedy.tsaim.geoad.widgets;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.activities.MainActivity;
import it.itskennedy.tsaim.geoad.activities.NewOfferActivity;
import it.itskennedy.tsaim.geoad.core.SettingsManager;
import it.itskennedy.tsaim.geoad.localdb.DataOffersContentProvider;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider
{
	@Override
	public void onReceive(Context aContext, Intent intent) 
	{
		aContext.getContentResolver().call(DataOffersContentProvider.OFFERS_URI, DataOffersContentProvider.DELETE_EXPIRED, null, null);
		
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(aContext);
		int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
		                           new ComponentName(aContext, WidgetProvider.class));
		
		for(int i = 0; i < appWidgetIds.length; ++i)
		{
			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i], R.id.offers);
			
			RemoteViews widget = getRemoteView(aContext); 
			appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
		}
	}

	@Override
    public void onUpdate(final Context aContext, final AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
		aContext.getContentResolver().call(DataOffersContentProvider.OFFERS_URI, DataOffersContentProvider.DELETE_EXPIRED, null, null);
		
		for (int i = 0; i < appWidgetIds.length; i++) 
		{
			RemoteViews widget = getRemoteView(aContext);
			appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
		}
		
		super.onUpdate(aContext, appWidgetManager, appWidgetIds);
    }
	
	private RemoteViews getRemoteView(Context context) 
	{
		RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		Intent vIntent = new Intent(context, RemoteViewWidgetService.class);
		widget.setRemoteAdapter(R.id.offers, vIntent);
		widget.setEmptyView(R.id.offers, R.id.textViewWNoOffer);
		
		boolean vLogged = SettingsManager.get(context).isUserLogged();
		widget.setViewVisibility(R.id.ButtonWAdmin, vLogged ? View.VISIBLE : View.GONE);
		
		if(vLogged)
		{
			Intent vAdminClick = new Intent(context, MainActivity.class);
			vAdminClick.setAction(MainActivity.MY_LOCATION_ACTION);
			PendingIntent vAdminClickPending = PendingIntent.getActivity(context, 0, vAdminClick, PendingIntent.FLAG_UPDATE_CURRENT);  
		    widget.setOnClickPendingIntent(R.id.ButtonWAdmin, vAdminClickPending);
		}
		
	    Intent vClick = new Intent(context, NewOfferActivity.class);
	    PendingIntent vClickPending = PendingIntent.getActivity(context, 0, vClick, PendingIntent.FLAG_UPDATE_CURRENT);  
	    widget.setPendingIntentTemplate(R.id.offers, vClickPending);
		return widget;
	}
}