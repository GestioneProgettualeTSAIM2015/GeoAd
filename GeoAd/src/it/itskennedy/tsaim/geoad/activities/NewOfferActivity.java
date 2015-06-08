package it.itskennedy.tsaim.geoad.activities;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.entity.LocationModel;
import it.itskennedy.tsaim.geoad.entity.Offer;
import it.itskennedy.tsaim.geoad.services.GeoAdService;
import it.itskennedy.tsaim.geoad.services.GeoAdService.GeoAdBinder;
import it.itskennedy.tsaim.geoad.services.GeoAdService.GetLocationListener;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;
public class NewOfferActivity extends Activity
{
	private ServiceConnection mServiceConnection;
	private GeoAdService mService;
	private Offer mOffer;
	private LocationModel mLocation;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newoffer);
        
        Bundle vData = getIntent().getExtras();
       
        if(!vData.isEmpty())
        {
        	mOffer = Offer.fromBundle(vData.getBundle(Offer.BUNDLE_KEY));
        	
        	mServiceConnection = new ServiceConnection()
        	{	
				@Override
				public void onServiceDisconnected(ComponentName name) 
				{
					mService = null;
				}
				
				@Override
				public void onServiceConnected(ComponentName name, IBinder service) 
				{
					mService = ((GeoAdBinder) service).getService();
					mService.getLocationById(mOffer.getLocationId(), new GetLocationListener()
					{	
						@Override
						public void onLoad(LocationModel aLocation)
						{
							mLocation = aLocation;
							if(mLocation == null)
							{
								mLocation = new LocationModel(2, "", "", "LOC TEST", 15, 14, "TEST DESC", "ca");
							}
							
							onServiceBinded();
						}
					});
				}
			}; 	
			
			bindService(new Intent(this, GeoAdService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
        } 
    }
    
    private void onServiceBinded()
    {
    	TextView vText = (TextView) findViewById(R.id.textViewOffDesc);
    	vText.setText("offer - id " + mOffer.getId() + " desc" + mOffer.getDesc() + "\nlocation - id " + mLocation.getId() + " name " + mLocation.getName());
    }

	@Override
	protected void onStop()
	{
		unbindService(mServiceConnection);
		mService = null;
		super.onStop();
	}
}
