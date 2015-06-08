package it.itskennedy.tsaim.geoad.widgets;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.core.Engine;
import it.itskennedy.tsaim.geoad.entity.Offer;
import it.itskennedy.tsaim.geoad.localdb.DataOffersContentProvider;
import it.itskennedy.tsaim.geoad.localdb.OffersHelper;

import java.util.ArrayList;
import java.util.List;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class OffersViewsFactory implements RemoteViewsService.RemoteViewsFactory
{
	private Context mContext;
	private List<Offer> mOffersList;

	private int mCount = 0;
	private int mAppWidgetId;
	
	public OffersViewsFactory(Context aContext, Intent aIntent)
	{
		mAppWidgetId = aIntent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) - 1;
		mContext = aContext;
	}
  
	@Override
	public void onCreate()
	{
		mCount = 0;
		
		mOffersList = new ArrayList<Offer>();
		Cursor vCursorOffers = mContext.getContentResolver().query(DataOffersContentProvider.OFFERS_URI, null, null, null, null);
		
		int vDescIndex = vCursorOffers.getColumnIndex(OffersHelper.DESC);
		int vNameIndex = vCursorOffers.getColumnIndex(OffersHelper.NAME);
		int vExpIndex = vCursorOffers.getColumnIndex(OffersHelper.EXP_DATE);
		int vInsIndex = vCursorOffers.getColumnIndex(OffersHelper.INS_DATE);
		int vLocNameIndex = vCursorOffers.getColumnIndex(OffersHelper.LOCATION_NAME);
		int vIdIndex = vCursorOffers.getColumnIndex(OffersHelper.OFF_ID);
		int vLocIdIndex = vCursorOffers.getColumnIndex(OffersHelper.LOCATION_ID);
		
		while(vCursorOffers.moveToNext())
		{
			int vId = vCursorOffers.getInt(vIdIndex);
			int vLocId = vCursorOffers.getInt(vLocIdIndex);
			String vDesc = vCursorOffers.getString(vDescIndex);
			long vExpTime = vCursorOffers.getLong(vExpIndex);
			long vInsTime = vCursorOffers.getLong(vInsIndex);
			String vLocationName = vCursorOffers.getString(vLocNameIndex);
			String vName = vCursorOffers.getString(vNameIndex);
			
			Offer vTemp = new Offer(vId, vName, vLocId, vLocationName, vDesc, vInsTime, vExpTime);
			
			mOffersList.add(vTemp);
			Log.d(Engine.APP_NAME, mCount++ + "");
		}
		
		vCursorOffers.close();
	}
  
	@Override
	public void onDestroy()
	{
		// do nothing
	}

	@Override
	public int getCount()
	{
		return(mOffersList.size());
	}

	@Override
	public RemoteViews getViewAt(int position)
	{
		RemoteViews vRow = new RemoteViews(mContext.getPackageName(), R.layout.offer_row);
    
		vRow.setTextViewText(R.id.textViewOffDesc, mOffersList.get(position).getDesc());
		vRow.setTextViewText(R.id.textViewOffExp, mOffersList.get(position).getExpTime());
		vRow.setTextViewText(R.id.textViewOffLocName, mOffersList.get(position).getLocationName());

		Intent vClick = new Intent();
		vClick.putExtra(Offer.BUNDLE_KEY, mOffersList.get(position).getBundle());
		vRow.setOnClickFillInIntent(R.id.widget_complete_row, vClick);
		
		return(vRow);
	}

	@Override
	public RemoteViews getLoadingView()
	{
		return(null);
	}
  
	@Override
	public int getViewTypeCount()
	{
		return(1);
	}

	@Override
	public long getItemId(int position)
	{
		return(position);
	}

	@Override
	public boolean hasStableIds()
	{
		return(true);
	}

	@Override
	public void onDataSetChanged()
	{
		onCreate();
	}
}