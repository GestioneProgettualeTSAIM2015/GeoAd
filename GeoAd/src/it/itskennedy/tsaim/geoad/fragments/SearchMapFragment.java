package it.itskennedy.tsaim.geoad.fragments;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.Utils;
import it.itskennedy.tsaim.geoad.activities.MainActivity;
import it.itskennedy.tsaim.geoad.entity.LocationModel;
import it.itskennedy.tsaim.geoad.interfaces.IFragment;
import it.itskennedy.tsaim.geoad.interfaces.ILocationsList;
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

public class SearchMapFragment extends Fragment implements ILocationsList
{

	private static IFragment mListener;
	private ArrayList<LocationModel> mLocationList;

	// GoogleMap map;
	// MapView vMapView;

	public static SearchMapFragment getInstance(Bundle aBundle, IFragment listener)
	{
		SearchMapFragment vFragment = new SearchMapFragment();
		if (aBundle != null)
		{
			vFragment.setArguments(aBundle);
		}
		if (listener instanceof IFragment)
		{
			mListener = listener;
		}
		return vFragment;
	}

	private GoogleMap map;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_search_map, container, false);
		mLocationList = new ArrayList<>();
		mLocationList.addAll(((MainActivity) getActivity()).getLocationList());

		map = ((MapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);

		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		populateMap();
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
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
		map.clear();
		for (LocationModel location : mLocationList)
		{
			Marker vMarker = map.addMarker(new MarkerOptions()
					.position(new LatLng(location.getLocation().getLatitude(), location.getLocation().getLongitude())).title(location.getName()));

		}

		Location currentLocation = ((MainActivity) getActivity()).getCurrentLocation();
		int radius = ((MainActivity) getActivity()).getCurrentRadiusFilter() * 1000;
		if (currentLocation != null)
		{
			LatLng latlngCenter = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

			Circle circle = map.addCircle(new CircleOptions().center(latlngCenter).radius(radius).fillColor(Color.parseColor("#330892d0")).strokeColor(Color.parseColor("#aa0892d0")).strokeWidth(3));
			float zoom = (float) (16 - Math.log(circle.getRadius() / 270) / Math.log(2));
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlngCenter, zoom));
		}

	}

	@Override
	public void notifyLocationsListChanged(ArrayList<LocationModel> aLocationsList)
	{
		mLocationList.clear();
		mLocationList.addAll(aLocationsList);
		populateMap();
	}
}
