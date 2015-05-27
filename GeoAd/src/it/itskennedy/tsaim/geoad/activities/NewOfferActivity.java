package it.itskennedy.tsaim.geoad.activities;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.entity.LocationModel;
import it.itskennedy.tsaim.geoad.entity.Offer;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
public class NewOfferActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newoffer);
        
        Bundle vData = getIntent().getExtras();
        
        if(!vData.isEmpty())
        {
        	Offer vOffer = Offer.fromBundle(vData.getBundle(Offer.BUNDLE_KEY));
        	LocationModel vLocation = LocationModel.fromBundle(vData.getBundle(LocationModel.BUNDLE_KEY));
        	
        	TextView vText = (TextView) findViewById(R.id.textView1);
        	
        	vText.setText("offer - id " + vOffer.getId() + " desc" + vOffer.getDesc() + "\nlocation - id " + vLocation.getId() + " name " + vLocation.getName());
        }
        
    }

}
