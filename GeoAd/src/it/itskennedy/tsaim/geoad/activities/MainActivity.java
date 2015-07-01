package it.itskennedy.tsaim.geoad.activities;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.Utils;
import it.itskennedy.tsaim.geoad.core.ConnectionManager;
import it.itskennedy.tsaim.geoad.core.ConnectionManager.JsonResponse;
import it.itskennedy.tsaim.geoad.core.Engine;
import it.itskennedy.tsaim.geoad.core.LocationManager;
import it.itskennedy.tsaim.geoad.core.Routes;
import it.itskennedy.tsaim.geoad.core.LocationManager.LocationListener;
import it.itskennedy.tsaim.geoad.core.SettingsManager;
import it.itskennedy.tsaim.geoad.entity.LocationModel;
import it.itskennedy.tsaim.geoad.fragments.DetailFragment;
import it.itskennedy.tsaim.geoad.fragments.EditLocationFragment;
import it.itskennedy.tsaim.geoad.fragments.MarkedLocationFragment;
import it.itskennedy.tsaim.geoad.fragments.MyLocationFragment;
import it.itskennedy.tsaim.geoad.fragments.SearchListFragment;
import it.itskennedy.tsaim.geoad.fragments.SearchMapFragment;
import it.itskennedy.tsaim.geoad.fragments.dialogs.FilterDialogFragment;
import it.itskennedy.tsaim.geoad.fragments.dialogs.LoginDialogFragment;
import it.itskennedy.tsaim.geoad.interfaces.IFilterDialogFragment;
import it.itskennedy.tsaim.geoad.interfaces.IFragment;
import it.itskennedy.tsaim.geoad.interfaces.ILocationsList;
import it.itskennedy.tsaim.geoad.interfaces.ILoginDialogFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

public class MainActivity extends Activity implements IFragment, ILoginDialogFragment, LocationListener, IFilterDialogFragment
{
	public static final String DETAIL_ACTION = "detail_action";
	public static final String DETAIL_DATA = "detail_data";
	public static final String MY_LOCATION_ACTION = "location_action";
	private static final String LOCATION_LIST = "locationList";
	private static final String POSITION_LAT = "positionlat";
	private static final String POSITION_LNG = "positionlng";
	private Menu mMenu;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private ArrayList<String> mNavActions;
	ArrayAdapter<String> mAdapter;
	private boolean isLogged;
	private int searchFragmentType;
	
	private boolean isClosingForAr = false;
	private String mCategoryJson = "";
	private Location mCurrentLocation;
	private ArrayList<LocationModel> mLocationList;
	
	ILocationsList mLocationListListener;

	private Map<String, Object> mFilterMap;

	private ProgressBar mProgressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mLocationList = new ArrayList<LocationModel>();
		setAllCategory();
		
		isLogged = SettingsManager.get(this).isUserLogged();

		mTitle = mDrawerTitle = getTitle();
		mNavActions = new ArrayList<>();
		Collections.addAll(mNavActions, getResources().getStringArray(R.array.navigation_drawer_strings));
		mNavActions.add(getResources().getString(isLogged ? R.string.my_activities : R.string.login));
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		
		mAdapter = new ArrayAdapter<String>(this, R.layout.drawer_list_item, mNavActions);
		mDrawerList.setAdapter(mAdapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_launcher, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		)
		{
			public void onDrawerClosed(View view)
			{
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
                if (isClosingForAr) {
                	isClosingForAr = false;
                	Intent i = new Intent(MainActivity.this, AugmentedRealityActivity.class);
    				startActivity(i);
                }
			}

			public void onDrawerOpened(View drawerView)
			{
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		Intent vLauncher = getIntent();
		
		if (savedInstanceState == null) 
		{
			selectItem(Utils.TYPE_SEARCH_LIST);
			mCurrentLocation = LocationManager.get(this).getLocation();
		} else {
			double vLat = savedInstanceState.getDouble(POSITION_LAT);
			double vLng = savedInstanceState.getDouble(POSITION_LNG);
			mCurrentLocation = new Location("");
			mCurrentLocation.setLatitude(vLat);
			mCurrentLocation.setLongitude(vLng);
			Serializable vList = savedInstanceState.getSerializable(LOCATION_LIST);
			if(vList != null) mLocationList = (ArrayList<LocationModel>) vList;
			
			if (vLauncher.getAction().equals(MainActivity.DETAIL_ACTION))
				loadFragment(Utils.TYPE_DETAIL, vLauncher.getBundleExtra(DETAIL_DATA), null);
		}
		
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		if(mCurrentLocation != null)
		{
			setFilterLocation(null, mCurrentLocation);
		}
		
		Fragment search = getFragmentManager().findFragmentByTag(SearchListFragment.class.toString());
		if (search != null && search.isVisible()) mLocationListListener = (ILocationsList) search;
		else {
			search = getFragmentManager().findFragmentByTag(SearchMapFragment.class.toString());
			if (search != null && search.isVisible()) mLocationListListener = (ILocationsList) search;
		}
	}

    @Override
	protected void onStart()
	{
		LocationManager.get(this).addListener(this);		
		super.onStart();
	}

	@Override
	protected void onNewIntent(Intent intent) 
	{
		super.onNewIntent(intent);
		switch (intent.getAction())
		{
		case DETAIL_ACTION:
			loadFragment(Utils.TYPE_DETAIL, intent.getBundleExtra(DETAIL_DATA), null);
			break;
		case MY_LOCATION_ACTION:
			loadFragment(Utils.TYPE_LOCATIONS, null, null);
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		mMenu = menu;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (mDrawerToggle.onOptionsItemSelected(item))
		{
			return true;
		}

		switch (item.getItemId())
		{
	    	case R.id.action_filter:
	    		loadFragment(Utils.TYPE_FILTER, null, null);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			selectItem(position);
		}
	}

	private void selectItem(int position)
	{
		loadFragment(position, null, null);

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		
		if (position != mNavActions.size() - 1 || isLogged) {
			setTitle(mNavActions.get(position));
			mAdapter.notifyDataSetChanged();
		}

		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title)
	{
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void loadFragment(int fragmentType, Bundle bundle, Fragment target)
	{
		Fragment vFragment = null;
		String vTag = "";

		switch (fragmentType)
		{
			case Utils.TYPE_SEARCH_LIST:
				searchFragmentType = Utils.TYPE_SEARCH_LIST;
				vFragment = SearchListFragment.getInstance(bundle);
				mLocationListListener = (ILocationsList) vFragment;
				break;
				
			case Utils.TYPE_AUGMENTED_REALITY:
				isClosingForAr = true;
				mDrawerLayout.closeDrawer(mDrawerList);
				return;
				
			case Utils.TYPE_PREFERENCE:
				vFragment = MarkedLocationFragment.getInstance(bundle);
				vTag = MarkedLocationFragment.TAG;
				break;
				
			case Utils.TYPE_LOCATIONS:
				if(isLogged)
				{
					vFragment = MyLocationFragment.getInstance();
				}
				else
				{
					LoginDialogFragment loginDialog = new LoginDialogFragment();
					loginDialog.setCancelable(false);
					loginDialog.show(getFragmentManager(), "LoginDialog");
					return;
				}
				break;
				
			case Utils.TYPE_SEARCH_MAP:
				searchFragmentType = Utils.TYPE_SEARCH_MAP;
				vFragment = SearchMapFragment.getInstance(bundle);
				mLocationListListener = (ILocationsList) vFragment;
				break;
				
			case Utils.TYPE_DETAIL:
			{
				if (!Engine.get().imLocationOwner(LocationModel.fromBundle(bundle).getId()))
				{
					vFragment = DetailFragment.getInstance(bundle);
					vTag = DetailFragment.TAG;	
				}
				else 
				{
					vFragment = EditLocationFragment.getInstance(bundle);
					vTag = EditLocationFragment.TAG;	
				}
			
				break;
			}
			case Utils.TYPE_FILTER:
				FilterDialogFragment filterDialog = new FilterDialogFragment(mCategoryJson, mFilterMap  );
				filterDialog.show(getFragmentManager(), "FilterDialog");
				return;
		}

		FragmentManager fragmentManager = getFragmentManager();
		String TAG = vFragment.getClass().toString();
		fragmentManager.beginTransaction().replace(R.id.content_frame, vFragment, TAG).addToBackStack(TAG).commit();
	}

	@Override
	public void onLoginButtonPressed(String email, String password)
	{
		RequestParams vParams = new RequestParams();

		vParams.put("UserName", email);
		vParams.put("password", password);
		vParams.put("grant_type", "password");

		ConnectionManager.obtain().post(Routes.TOKEN, vParams, new JsonResponse()
		{
			@Override
			public void onResponse(boolean aResult, Object aResponse)
			{
				if (aResult && aResponse != null)
				{
					String aToken = "";
					try
					{
						aToken = ((JSONObject) aResponse).getString("access_token");
						SettingsManager.get(MainActivity.this).saveToken(aToken);
						Engine.get().onLogin(aToken);

						Toast.makeText(MainActivity.this, "Loggato", Toast.LENGTH_SHORT).show();
						isLogged = true;
						mNavActions.set(mNavActions.size() - 1, getResources().getString(R.string.my_activities));
						mAdapter.notifyDataSetChanged();
					}
					catch (JSONException e)
					{
						Log.e(Engine.APP_NAME, "JSON Decode Errore");
					}
				}
				else
				{
					selectItem(Utils.TYPE_LOCATIONS);
					Toast.makeText(MainActivity.this, "Errore", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void onRegisterButtonPressed()
	{
		Intent vIntent = new Intent(MainActivity.this, RegisterActivity.class);
		startActivity(vIntent);
	}

	@Override
	public void onCancelButtonPressed()
	{
		selectItem(searchFragmentType);
	}

	@Override
	public void toggleActionMenu(int... options)
	{
		if (mMenu == null)
			return;

		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		for (int optionId : options)
			mMenu.findItem(optionId).setVisible(!drawerOpen);
	}
	
	@Override
	public void onStop()
	{
		LocationManager.get(this).removeListener(this);
		super.onStop();
	}
	
	@Override
	public void onLocationUpdated(Location aLocation)
	{
		if (aLocation == null) return;
		
		mCurrentLocation = aLocation;
		setFilterLocation(null, aLocation);
//		LocationManager.get(this).removeListener(this);
	}

	@Override
	public void onFilterSave(Bundle aFilter)
	{
		mLocationList.clear();
		mLocationListListener.notifyLocationsListChanged(mLocationList);
		Map<String, Object> vFilterMap = new HashMap<>();
		if (aFilter != null)
		{
			for (String key : aFilter.keySet()) {
				vFilterMap.put(key, aFilter.get(key));
			}
			setFilterLocation(vFilterMap, mCurrentLocation);
		}
		mFilterMap = vFilterMap;
	}

	@Override
	public void onFilterReset()
	{
		mLocationList.clear();
		mLocationListListener.notifyLocationsListChanged(mLocationList);
		setFilterLocation(null, mCurrentLocation);
		mFilterMap = null;
	}
	
	private void setFilterLocation(Map<String, Object> aFilterMap, Location aLocation)
	{
		String vLat = String.valueOf(aLocation.getLatitude());
		String vLng = String.valueOf(aLocation.getLongitude());
		String vR = String.valueOf(Utils.DEFAULT_RADIUS);
		String vFilter = "";
		if(aFilterMap != null)
		{
			if (aFilterMap.containsKey(Utils.RADIUS))
			{
				vR = aFilterMap.get(Utils.RADIUS).toString();
			}
			
			boolean appendAnd = false;
			
			if (aFilterMap.containsKey(Utils.SECONDARY_CATEGORY))
			{
				if (!appendAnd) vFilter += "&$filter=";
				appendAnd = true;
				
				vFilter += "(";
				ArrayList<String> vCategoryList = (ArrayList<String>) aFilterMap.get(Utils.SECONDARY_CATEGORY);
				for (String cat : vCategoryList)
				{
					vFilter += String.format("SCat eq '%s' or ", cat);
				}
				vFilter = vFilter.substring(0, (vFilter.length() - 4));
				vFilter += ")";
			}
			else if (aFilterMap.containsKey(Utils.PRIMARY_CATEGORY))
			{
				if (!appendAnd) vFilter += "&$filter=";
				appendAnd = true;
				
				vFilter += "(";
				ArrayList<String> vCategoryList = (ArrayList<String>) aFilterMap.get(Utils.PRIMARY_CATEGORY);
				for (String cat : vCategoryList)
				{
					vFilter += String.format("PCat eq '%s' or ", cat);
				}
				vFilter = vFilter.substring(0, (vFilter.length() - 4));
				vFilter += ")";
			}
			
			if (aFilterMap.containsKey(Utils.TYPE))
			{
				if (!appendAnd) vFilter += "&$filter=";
				else vFilter += " and ";
				
				vFilter += String.format("Type eq '%s'", aFilterMap.get(Utils.TYPE).toString());
				appendAnd = true;
			}
			
			if (aFilterMap.containsKey(Utils.NAME))
			{
				if (!appendAnd) vFilter += "&$filter=";
				else vFilter += " and ";
				
				vFilter += String.format("startswith(tolower(Name), tolower('%s'))", aFilterMap.get(Utils.NAME).toString());
				appendAnd = true;
			}
		}
		
		mProgressBar.setVisibility(View.VISIBLE);
		String vUrl = String.format(Utils.LOCATION_URL_TEMPLATE, vLat, vLng, vR, vFilter);
		ConnectionManager.obtain().get(vUrl, null, new JsonResponse()
		{
			@Override
			public void onResponse(boolean aResult, Object aResponse)
			{
				if (aResult)
				{
					JSONArray locationArray = (JSONArray) aResponse;
					mLocationList.clear();
					mLocationList.addAll(LocationModel.getListFromJsonArray(locationArray));
					if (mLocationListListener != null) mLocationListListener.notifyLocationsListChanged(mLocationList);
					mProgressBar.setVisibility(View.GONE);
				}
			}
		});
	}
	
	private void setAllCategory()
	{
		ConnectionManager.obtain().get("api/categories", null, new JsonResponse()
		{
			@Override
			public void onResponse(boolean aResult, Object aResponse)
			{
				if (aResult)
				{
					JSONObject categoryJson = (JSONObject) aResponse;
					mCategoryJson = categoryJson.toString();
				}
			}
		});
	}
	
	public ArrayList<LocationModel> getLocationList()
	{
		return mLocationList;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) 
	{
		if (mLocationList != null)
			outState.putSerializable(LOCATION_LIST, mLocationList);
		
		if (mCurrentLocation != null) {
			outState.putSerializable(POSITION_LAT, mCurrentLocation.getLatitude());
			outState.putSerializable(POSITION_LNG, mCurrentLocation.getLongitude());
		}
		super.onSaveInstanceState(outState);
	}

	public Location getCurrentLocation()
	{
		return mCurrentLocation;
	}
	
	public double getCurrentRadiusFilter()
	{
		return mFilterMap != null ? Double.parseDouble(mFilterMap.get(Utils.RADIUS).toString()) : 8;
	}

	@Override
	public void checkAuth()
	{
		ConnectionManager.obtain().get(Routes.AUTH, null, new JsonResponse()
		{	
			@Override
			public void onResponse(boolean aResult, Object aResponse) 
			{
				if(!aResult)
				{
					Toast.makeText(MainActivity.this, getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
					isLogged = false;
					mNavActions.set(mNavActions.size() - 1, getResources().getString(R.string.login));
					mAdapter.notifyDataSetChanged();
					SettingsManager.get(MainActivity.this).saveToken(null);
					loadFragment(Utils.TYPE_LOCATIONS, null, null);
				}
			}
		});
	}
}
