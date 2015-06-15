package it.itskennedy.tsaim.geoad.widgets;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class RemoteViewWidgetService extends RemoteViewsService 
{
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) 
	{
		return (new OffersViewsFactory(this.getApplicationContext(), intent));
	}
}
