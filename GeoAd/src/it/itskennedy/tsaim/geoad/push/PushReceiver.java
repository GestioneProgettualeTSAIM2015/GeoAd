package it.itskennedy.tsaim.geoad.push;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import it.itskennedy.tsaim.geoad.core.Engine;
import it.itskennedy.tsaim.geoad.services.GeoAdService;

public class PushReceiver extends WakefulBroadcastReceiver 
{
	public static final int NEW_LOCATION = 0;
	public static final int NEW_OFFER = 1;
	public static final int DELETE_LOCATION = 2;
	public static final int DELETE_OFFER = 3;
	
    @Override
    public void onReceive(Context context, Intent intent) 
    {
        Log.d(Engine.APP_NAME, "Push received");

        Bundle vExtras = intent.getExtras();

        if(!vExtras.isEmpty())
        {
        	int vPushCode = vExtras.getInt("push_code");
        	
        	Intent vToService = new Intent(context, GeoAdService.class);
        	
        	switch(vPushCode)
        	{
	        	case NEW_OFFER:
	        	{
	        		String vJsonOffer = vExtras.getString("json_obj");
	                vToService.setAction(GeoAdService.NEW_OFFER);
	                vToService.putExtra(GeoAdService.DATA, vJsonOffer);
	                context.startService(vToService);
	                break;
	        	}
	        	case NEW_LOCATION:
	        	{
	        		String vJsonLocation = vExtras.getString("json_obj");
	                vToService.setAction(GeoAdService.NEW_LOCATION);
	                vToService.putExtra(GeoAdService.DATA, vJsonLocation);
	                context.startService(vToService);
	                break;
	        	}
	        	case DELETE_OFFER:
	        	{
	        		vToService.setAction(GeoAdService.DELETE_OFFER);
	        		vToService.putExtra(GeoAdService.DELETE_ID, vExtras.getInt("delete_id"));
	                context.startService(vToService);
	                break;
	        	}
	        	case DELETE_LOCATION:
	        	{
	        		vToService.setAction(GeoAdService.DELETE_LOCATION);
	        		vToService.putExtra(GeoAdService.DELETE_ID, vExtras.getInt("delete_id"));
	                context.startService(vToService);
	                break;
	        	}
        	}
        }
    }
}