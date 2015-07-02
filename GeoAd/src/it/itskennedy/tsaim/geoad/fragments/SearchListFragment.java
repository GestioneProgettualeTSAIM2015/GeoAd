package it.itskennedy.tsaim.geoad.fragments;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.Utils;
import it.itskennedy.tsaim.geoad.activities.MainActivity;
import it.itskennedy.tsaim.geoad.entity.LocationModel;
import it.itskennedy.tsaim.geoad.interfaces.IFragment;
import it.itskennedy.tsaim.geoad.interfaces.ILocationsList;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SearchListFragment extends Fragment implements ILocationsList
{
	public static final String TAG = "search_list_fragment";
	private IFragment mListener;

	public static SearchListFragment getInstance(Bundle aBundle)
	{
		SearchListFragment vFragment = new SearchListFragment();
		if (aBundle != null)
		{
			vFragment.setArguments(aBundle);
		}
		
		return vFragment;
	}

	private CustomListAdapter adapter;
	private ArrayList<LocationModel> mLocationList;
	
	@Override
	public void onResume()
	{
		if (mListener != null) mListener.setILocationsList(this);
		super.onResume();
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
		
		list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int aPos, long arg3)
			{
				if(mListener != null)
				{
					mListener.loadFragment(Utils.TYPE_DETAIL, adapter.getItem(aPos).getBundle(), null);
				}
			}
		});
		
		return view;
	}
	
	@Override
	public void notifyLocationsListChanged(ArrayList<LocationModel> aLocationsList)
	{
		if (mLocationList != null) {
			mLocationList.clear();
			mLocationList.addAll(aLocationsList);
			adapter.notifyDataSetChanged();
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
