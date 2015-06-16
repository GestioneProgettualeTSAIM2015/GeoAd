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
	public static final String NEW_LOCATION = "LocationCreated";
	public static final String UPDATE_LOCATION = "LocationUpdated";
	public static final String NEW_OFFER = "OfferingCreated";
	public static final String UPDATE_OFFER = "OfferingUpdated";
	public static final String DELETE_LOCATION = "LocationDeleted";
	public static final String DELETE_OFFER = "OfferingDeleted";
	
    @Override
    public void onReceive(Context context, Intent intent) 
    {
        Bundle vExtras = intent.getExtras();

        if(!vExtras.isEmpty())
        {
        	String vPushAction = vExtras.getString("action");
        	
        	if(vPushAction != null)
        	{
        		Intent vToService = new Intent(context, GeoAdService.class);
            	
            	switch(vPushAction)
            	{
            		case UPDATE_OFFER:
    	        	case NEW_OFFER:
    	        	{
    	        		Log.i(Engine.APP_NAME, "Push received - NEW OFFER");
    	        		String vJsonOffer = vExtras.getString("message");
    	                vToService.setAction(GeoAdService.NEW_OFFER);
    	                vToService.putExtra(GeoAdService.DATA, vJsonOffer);
    	                context.startService(vToService);
    	                break;
    	        	}
    	        	case UPDATE_LOCATION:
    	        	case NEW_LOCATION:
    	        	{
    	        		Log.i(Engine.APP_NAME, "Push received - NEW LOCATION");
    	        		String vJsonLocation = vExtras.getString("message");
    	                vToService.setAction(GeoAdService.NEW_LOCATION);
    	                vToService.putExtra(GeoAdService.DATA, vJsonLocation);
    	                context.startService(vToService);
    	                break;
    	        	}
    	        	case DELETE_OFFER:
    	        	{
    	        		Log.i(Engine.APP_NAME, "Push received - DELETE OFFER");
    	        		vToService.setAction(GeoAdService.DELETE_OFFER);
    	        		vToService.putExtra(GeoAdService.DELETE_ID, Integer.valueOf(vExtras.getString("message")));
    	                context.startService(vToService);
    	                break;
    	        	}
    	        	case DELETE_LOCATION:
    	        	{
    	        		Log.i(Engine.APP_NAME, "Push received - DELETE LOCATION");
    	        		vToService.setAction(GeoAdService.DELETE_LOCATION);
    	        		vToService.putExtra(GeoAdService.DELETE_ID, Integer.valueOf(vExtras.getString("message")));
    	                context.startService(vToService);
    	                break;
    	        	}
            	}
        	}
        }
    }
}