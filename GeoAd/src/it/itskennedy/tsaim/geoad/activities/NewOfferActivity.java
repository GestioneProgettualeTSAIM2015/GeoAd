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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
public class NewOfferActivity extends Activity
{
	private ServiceConnection mServiceConnection;
	private GeoAdService mService;
	private Offer mOffer;
	private LocationModel mLocation;
	
	private TextView mLocationName, mOfferDescription, mOfferValidUntil, mLocationCat;
	private ProgressBar mProgressBar;
 	private Button mBtnGoToDetails;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newoffer);
        
    	mLocationName = (TextView) findViewById(R.id.textViewNameLocation);
    	mOfferDescription = (TextView) findViewById(R.id.textViewOfferDescription);
    	mOfferValidUntil = (TextView) findViewById(R.id.textViewValidUntil);
    	mLocationCat = (TextView) findViewById(R.id.textViewLocationCat);
    	mBtnGoToDetails = (Button) findViewById(R.id.btnGoToLocationDetail);
    	mProgressBar = (ProgressBar) findViewById(R.id.progressBarDetailOffer);
        
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
    	setTitle(getResources().getString(R.string.actvity_offer_detail_title) + " " + mOffer.getName());
    	mLocationName.setText(mLocation.getName());
    	mOfferDescription.setText(getResources().getString(R.string.offer_title) + mOffer.getDesc());
    	mOfferValidUntil.setText(getResources().getString(R.string.offer_exp_date) + mOffer.getExpTime());
    	mLocationCat.setText(getResources().getString(R.string.location_cat) + mLocation.getPCat());
    	mBtnGoToDetails.setText(getResources().getString(R.string.location_button_goto));
    	mBtnGoToDetails.setOnClickListener(new OnClickListener()
    	{	
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(NewOfferActivity.this, MainActivity.class);
				i.setAction(MainActivity.DETAIL_ACTION);
				i.putExtra(MainActivity.DETAIL_DATA, mLocation.getBundle());
				startActivity(i);	
				finish();
			}
		});
    	hideLoader();
    }
		
	private void hideLoader()
	{
		mProgressBar.setVisibility(View.GONE);
		mLocationName.setVisibility(View.VISIBLE);
		mOfferDescription.setVisibility(View.VISIBLE);
		mOfferValidUntil.setVisibility(View.VISIBLE);
		mLocationCat.setVisibility(View.VISIBLE);
		mBtnGoToDetails.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onStop()
	{
		unbindService(mServiceConnection);
		mService = null;
		super.onStop();
	}
}