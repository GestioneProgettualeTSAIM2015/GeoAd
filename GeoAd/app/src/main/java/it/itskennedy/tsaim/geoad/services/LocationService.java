package it.itskennedy.tsaim.geoad.services;

import it.itskennedy.tsaim.geoad.LocationManager.LocationListener;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.widget.Toast;

public class LocationService extends Service implements LocationListener
{
	public static final String LOCATION_ACTION = "location_action";

	@Override
	public void onCreate() 
	{
	  HandlerThread thread = new HandlerThread("Location Service", Process.THREAD_PRIORITY_BACKGROUND);
	  thread.start();
	  Toast.makeText(this, "Service Created", Toast.LENGTH_SHORT).show();
	  
	  LocationManager.get(this).setListener(this);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
	    return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) 
	{
	    return null;
	}
	
	@Override
	public void onDestroy() 
  	{
		Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLocationUpdated(Location aLocation) 
	{
		Toast.makeText(this, "New Position", Toast.LENGTH_SHORT).show();
		
		Intent vBroadcastIntent = new Intent();
		vBroadcastIntent.setAction(LOCATION_ACTION);
		Bundle vBundle = new Bundle();
		vBundle.putDouble("lat", aLocation.getLatitude());
		vBundle.putDouble("lng", aLocation.getLongitude());
		vBroadcastIntent.putExtras(vBundle);
		sendBroadcast(vBroadcastIntent);
	}
}
