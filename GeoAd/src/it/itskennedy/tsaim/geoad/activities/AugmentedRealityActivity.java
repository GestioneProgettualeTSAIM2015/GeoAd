package it.itskennedy.tsaim.geoad.activities;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.augmentedreality.AugmentedRealityManager;
import it.itskennedy.tsaim.geoad.augmentedreality.AugmentedRealityManager.AugmentedRealityListener;
import it.itskennedy.tsaim.geoad.augmentedreality.AugmentedRealityManager.IAugmentedReality;
import it.itskennedy.tsaim.geoad.entity.LocationModel;
import it.itskennedy.tsaim.geoad.localdb.DataOffersContentProvider;
import it.itskennedy.tsaim.geoad.localdb.OffersHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.opengl.util.LowPassFilter;
import com.beyondar.android.plugin.radar.RadarView;
import com.beyondar.android.plugin.radar.RadarWorldPlugin;
import com.beyondar.android.util.math.geom.Point3;
import com.beyondar.android.view.BeyondarViewAdapter;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.BeyondarObjectList;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;

public class AugmentedRealityActivity extends FragmentActivity implements
		OnClickBeyondarObjectListener, IAugmentedReality {

	private static final int TYPE_POI = 0;
	private static final int TYPE_CA = 1;
	
	private BeyondarFragmentSupport mBeyondarFragment;
	private RadarView mRadarView;
	private RadarWorldPlugin mRadarPlugin;
	private World mWorld;

	private List<BeyondarObject> showViewOn;
	private ArrayList<Integer> activeOffersLocationIDs;
	private List<LocationModel> activeLocations;

	private BeyondarObject activeObj;		
	
	private ImageButton btnImgClose, btnImgGoToDetails;

	private AugmentedRealityManager mArgReality;

	private AugmentedRealityListener mArgReaListener = new AugmentedRealityListener() {

		@Override
		public void onNewPosition(Location aCurrentLocation, List<LocationModel> aToDraw) {
			handleNewPosition(aCurrentLocation, aToDraw);
		}
	};
	
	private void handleNewPosition(Location aCurrentLocation, List<LocationModel> aToDraw) {
		
		if (aCurrentLocation == null || aToDraw == null) return;
		
		mWorld.setGeoPosition(aCurrentLocation.getLatitude(),aCurrentLocation.getLongitude());
		List<LocationModel> toAdd = new ArrayList<>();

		//for every world object I check if they are still to be drawn or not
		for (BeyondarObjectList objList : mWorld.getBeyondarObjectLists()) {
			for (BeyondarObject obj : objList) {
				if (findLocationFromId(obj.getId(), aToDraw) == null) {
					mWorld.remove(obj);
					LocationModel lmToDelete = findLocationFromId(obj.getId(), activeLocations);
					activeLocations.remove(lmToDelete);
				}
			}
		}
		// for every location to draw I check if its new and I need to add it
		for (LocationModel lm : aToDraw) {
			if (findWorldObjectById(lm.getId()) == null) {
				toAdd.add(lm);
			}
		}

		for (int i = 0; i < toAdd.size(); ++i) {
			LocationModel lm = toAdd.get(i);
			activeLocations.add(lm);
			GeoObject go = new GeoObject(lm.getId());
			go.setGeoPosition(lm.getLocation().getLatitude(), lm.getLocation().getLongitude());
			go.setName(lm.getName());
			switch (lm.getType().toLowerCase()) {
			case "poi":
				if (activeOffersLocationIDs.contains(lm.getId()))
					go.setImageResource(R.drawable.poi_o);
				else
					go.setImageResource(R.drawable.poi);
				mWorld.addBeyondarObject(go, TYPE_POI);
				break;

			default:
				if (activeOffersLocationIDs.contains(lm.getId()))
					go.setImageResource(R.drawable.ca_o);
				else
					go.setImageResource(R.drawable.ca);
				mWorld.addBeyondarObject(go, TYPE_CA);
				break;
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_augmented_reality);
		
		mArgReality = new AugmentedRealityManager(this);
		mArgReality.setIAugmentedRealityListener(this);
		showViewOn = Collections.synchronizedList(new ArrayList<BeyondarObject>());
		activeLocations = new ArrayList<LocationModel>();
		
		btnImgClose = (ImageButton) findViewById(R.id.btn_img_close);
		btnImgGoToDetails = (ImageButton) findViewById(R.id.btn_img_goto_detail);
		
		btnImgClose.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showViewOn.remove(activeObj);
				activeObj = null;
				btnImgClose.setVisibility(View.GONE);
				btnImgGoToDetails.setVisibility(View.GONE);
			}
		});
		
		btnImgGoToDetails.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LocationModel current = findLocationFromId(activeObj.getId(), activeLocations);
				if (current != null) {
					Intent i = new Intent(AugmentedRealityActivity.this, MainActivity.class);
					i.setAction(MainActivity.DETAIL_ACTION);
					i.putExtra(MainActivity.DETAIL_DATA, current.getBundle());
					startActivity(i);
					finish();
				}
			}
		});

		mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(R.id.beyondarFragment);

		mRadarView = (RadarView) findViewById(R.id.radarView);

		populateOffersList();

		// Create the Radar plugin
		mRadarPlugin = new RadarWorldPlugin(this);
		// set the radar view in to our radar plugin
		mRadarPlugin.setRadarView(mRadarView);
		// Set how far (in meters) we want to display in the view
		mRadarPlugin.setMaxDistance(150);

		// We can customize the color of the items
		mRadarPlugin.setListColor(TYPE_POI, Color.RED);
		mRadarPlugin.setListColor(TYPE_CA, Color.GREEN);
		// and also the size
		mRadarPlugin.setListDotRadius(TYPE_POI, 1);
		mRadarPlugin.setListDotRadius(TYPE_CA, 1);

		mBeyondarFragment.setOnClickBeyondarObjectListener(this);

		CustomBeyondarViewAdapter customBeyondarViewAdapter = new CustomBeyondarViewAdapter(this);
		mBeyondarFragment.setBeyondarViewAdapter(customBeyondarViewAdapter);

		// We create the world and fill it ...
		mWorld = new World(this);
		// .. and send it to the fragment
		mBeyondarFragment.setWorld(mWorld);

		mBeyondarFragment.setMaxDistanceToRender(10000);
		mBeyondarFragment.setPullCloserDistance(50);
		mBeyondarFragment.setPushAwayDistance(10);
		
		mBeyondarFragment.setRetainInstance(true);

		// add the plugin
		mWorld.addPlugin(mRadarPlugin);
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		// We also can see the Frames per seconds
//		mBeyondarFragment.showFPS(true);

		LowPassFilter.ALPHA = 0.008f;
	}

	@Override
	protected void onResume() {
		mArgReality.onResume();
		mArgReality.setListener(mArgReaListener);
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mArgReality.onPause();
		mArgReality.setListener(null);
	}

	@Override
	public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects) {

		if (beyondarObjects.size() == 0) {
			return;
		}
		BeyondarObject beyondarObject = beyondarObjects.get(0);
		if (showViewOn.contains(beyondarObject)) {
			showViewOn.remove(beyondarObject);
			activeObj = null;
			btnImgClose.setVisibility(View.GONE);
			btnImgGoToDetails.setVisibility(View.GONE);
		} else {
			showViewOn.clear();
			showViewOn.add(beyondarObject);
			activeObj = beyondarObject;
		}
	}

	private class CustomBeyondarViewAdapter extends BeyondarViewAdapter {

		LayoutInflater inflater;

		public CustomBeyondarViewAdapter(Context context) {
			super(context);
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(BeyondarObject beyondarObject, View recycledView, ViewGroup parent) {
						
			if (!showViewOn.contains(beyondarObject)) {
				return null;
			}
			if (recycledView == null) {
					recycledView = inflater.inflate(R.layout.near_ar_object_view, parent, false);
			}
						
			TextView txtName = (TextView) recycledView
					.findViewById(R.id.titleTextView);
			TextView txtDistance = (TextView) recycledView
					.findViewById(R.id.distanceTextView);
			TextView txtDescription = (TextView) recycledView
					.findViewById(R.id.descriptionTextView);
			
			LocationModel current = findLocationFromId(beyondarObject.getId(), activeLocations);
			
			if (current != null) {
				txtName.setText(current.getName());
				txtDistance.setText((int) beyondarObject.getDistanceFromUser() + " m");
				txtDescription.setText(current.getDescription());
			}
						
			// Once the view is ready we specify the position
			setPosition(beyondarObject.getScreenPositionTopRight());

			Rect panelRect = new Rect();
			recycledView.getDrawingRect(panelRect);
			updateCloseAndGoToButtons(beyondarObject.getScreenPositionTopRight(), panelRect);
			
			return recycledView;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}
		
	public void populateOffersList() {

		activeOffersLocationIDs = new ArrayList<Integer>();
		String[] projection = { OffersHelper.LOCATION_ID };

		Cursor cursor = getContentResolver().query(
				DataOffersContentProvider.OFFERS_URI, projection, null, null,
				null);

		int idColumnIndex = cursor.getColumnIndex(OffersHelper.LOCATION_ID);

		if (cursor.moveToFirst()) {
			do {
				activeOffersLocationIDs.add(cursor.getInt(idColumnIndex));
			} while (cursor.moveToNext());
		}
	}
	
	public void updateCloseAndGoToButtons(Point3 p, Rect panelRect) {
		
		if (activeObj != null && panelRect != null) {
			float panelWidth = panelRect.width();
			float panelHeight = panelRect.height();
			if (panelHeight > 0 && panelWidth > 0) {
				
				btnImgGoToDetails.setX(p.x + panelWidth - btnImgClose.getWidth() / 2);
				btnImgGoToDetails.setY(p.y + panelHeight - btnImgClose.getHeight() / 2);

				btnImgClose.setX(p.x + panelWidth - btnImgClose.getWidth() / 2);
				btnImgClose.setY(p.y - btnImgClose.getHeight() / 2);
				btnImgGoToDetails.setVisibility(View.VISIBLE);
				btnImgClose.setVisibility(View.VISIBLE);
			}
		}
	}
	
	public BeyondarObject findWorldObjectById (long id) {
		for (BeyondarObjectList objList : mWorld.getBeyondarObjectLists()) {
			for (BeyondarObject obj : objList) {
				if (obj.getId() == id) 
					return obj;
			}
		}
		return null;
	}
	
	public LocationModel findLocationFromId(long id, List<LocationModel> list) {
		for (LocationModel lm : list) {
			if (lm.getId() == id ) 
				return lm;
		}
		return null;
	}

	@Override
	public void onServiceReady() {
		handleNewPosition(mArgReality.getCurrentPosition(), mArgReality.getNears());
	}
}
