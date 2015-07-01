package it.itskennedy.tsaim.geoad.fragments;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.Utils;
import it.itskennedy.tsaim.geoad.activities.MainActivity;
import it.itskennedy.tsaim.geoad.entity.LocationModel;
import it.itskennedy.tsaim.geoad.interfaces.IFragment;
import it.itskennedy.tsaim.geoad.interfaces.ILocationsList;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SearchMapFragment extends Fragment implements ILocationsList
{
	private IFragment mListener;
	private ArrayList<LocationModel> mLocationList;

	public static SearchMapFragment getInstance(Bundle aBundle)
	{
		SearchMapFragment vFragment = new SearchMapFragment();
		if (aBundle != null)
		{
			vFragment.setArguments(aBundle);
		}
	
		return vFragment;
	}

	private GoogleMap mMap;
	private MapView mMapView;
	private HashMap<String, LocationModel> mHash;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_search_map, container, false);
		mLocationList = new ArrayList<>();
		mLocationList.addAll(((MainActivity) getActivity()).getLocationList());

		mHash = new HashMap<String, LocationModel>();
		
		mMapView = (MapView) view.findViewById(R.id.mapview);
	    mMapView.getMapAsync(new OnMapReadyCallback() {
			
			@Override
			public void onMapReady(GoogleMap googleMap) {
				mMap = googleMap;
				mMap.setMyLocationEnabled(true);
				populateMap();
			}
		});
	    
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		mMapView.onCreate(savedInstanceState);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroy() 
	{
		mMapView.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onLowMemory() 
	{
		mMapView.onLowMemory();
		super.onLowMemory();
	}

	@Override
	public void onPause() 
	{
		mMapView.onPause();
		super.onPause();
	}

	@Override
	public void onResume() 
	{
		mMapView.onResume();
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) 
	{
		if (mMapView != null)
			mMapView.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		MapsInitializer.initialize(getActivity().getApplicationContext());
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.search_menu_map, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu)
	{
		mListener.toggleActionMenu(new int[]
		{ R.id.action_change_view, R.id.action_filter });
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		switch (item.getItemId())
		{
			case R.id.action_change_view:
				mListener.loadFragment(Utils.TYPE_SEARCH_LIST, null, null);
				item.setIcon(R.drawable.ic_menu_map);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void populateMap()
	{
		mMap.clear();
		for (LocationModel location : mLocationList)
		{
			Marker vAdded = mMap.addMarker(new MarkerOptions()
					.position(new LatLng(location.getLocation().getLatitude(), location.getLocation().getLongitude()))
					.title(location.getName()).snippet(location.getPCat()));
			
			vAdded.setIcon(BitmapDescriptorFactory.defaultMarker(location.getType().equals(Utils.LOC_TYPE_POI) ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_RED));
			
			mHash.put(vAdded.getId(), location);
		}
		
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener()
		{	
			@Override
			public void onInfoWindowClick(Marker marker) 
			{
				LocationModel vModel = mHash.get(marker.getId());
				
				if(mListener != null && vModel != null)
				{
					mListener.loadFragment(Utils.TYPE_DETAIL, vModel.getBundle(), null);
				}
			}
		});

		Location currentLocation = ((MainActivity) getActivity()).getCurrentLocation();
		double radius = ((MainActivity) getActivity()).getCurrentRadiusFilter() * 1000;
		if (currentLocation != null)
		{
			LatLng latlngCenter = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

			Circle circle = mMap.addCircle(new CircleOptions().center(latlngCenter).radius(radius).fillColor(Color.parseColor("#330892d0")).strokeColor(Color.parseColor("#aa0892d0")).strokeWidth(3));
			float zoom = (float) (16 - Math.log(circle.getRadius() / 270) / Math.log(2));
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlngCenter, zoom));
		}

	}

	@Override
	public void notifyLocationsListChanged(ArrayList<LocationModel> aLocationsList)
	{
		if (mLocationList != null) {
			mLocationList.clear();
			mLocationList.addAll(aLocationsList);
			populateMap();
		}
	}
	
	@Override
	public void onAttach(Activity aActivity)
	{
		if(aActivity instanceof IFragment)
		{
			mListener = (IFragment) aActivity;
		}
		super.onAttach(aActivity);
	}
}
