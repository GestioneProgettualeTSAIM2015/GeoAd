package it.itskennedy.tsaim.geoad.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.Utils;
import it.itskennedy.tsaim.geoad.activities.MainActivity;
import it.itskennedy.tsaim.geoad.core.ConnectionManager;
import it.itskennedy.tsaim.geoad.core.ConnectionManager.JsonResponse;
import it.itskennedy.tsaim.geoad.core.LocationManager;
import it.itskennedy.tsaim.geoad.core.LocationManager.LocationListener;
import it.itskennedy.tsaim.geoad.customview.MultiSelectSpinner;
import it.itskennedy.tsaim.geoad.entity.LocationModel;
import it.itskennedy.tsaim.geoad.interfaces.IFilterDialogFragment;
import it.itskennedy.tsaim.geoad.interfaces.IFragment;
import it.itskennedy.tsaim.geoad.interfaces.ILocationsList;
import it.itskennedy.tsaim.geoad.services.GeoAdService;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import it.itskennedy.tsaim.geoad.services.GeoAdService.GeoAdBinder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class SearchListFragment extends Fragment implements ILocationsList
{

	private static IFragment mListener;

	public static SearchListFragment getInstance(Bundle aBundle, IFragment listener)
	{
		SearchListFragment vFragment = new SearchListFragment();
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

	private CustomListAdapter adapter;
	private ArrayList<LocationModel> mLocationList;
	private Location mCurrentLocation;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.search_menu_list, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu)
	{
		mListener.toggleActionMenu(new int[] { R.id.action_change_view, R.id.action_filter });
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        switch(item.getItemId()) {
    	case R.id.action_change_view:
    		Bundle vBundle = new Bundle();
    		for(LocationModel loc : mLocationList)
    		{
    			vBundle.putBundle(loc.getName(), loc.getBundle());
    		}
    		mListener.loadFragment(Utils.TYPE_SEARCH_MAP, vBundle, null);
    		item.setIcon(R.drawable.ic_menu_list);
    		return true;
			
        default:
            return super.onOptionsItemSelected(item);
        }
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_search_list, container, false);
		mLocationList = new ArrayList<LocationModel>();
		mLocationList.addAll(((MainActivity) getActivity()).getLocationList());

		ListView list = (ListView) view.findViewById(R.id.listView);
		adapter = new CustomListAdapter(getActivity(), mLocationList);
		list.setAdapter(adapter);
		
		return view;
	}
	
	@Override
	public void notifyLocationsListChanged(ArrayList<LocationModel> aLocationsList)
	{
		mLocationList.clear();
		mLocationList.addAll(aLocationsList);
		adapter.notifyDataSetChanged();
	}
}
