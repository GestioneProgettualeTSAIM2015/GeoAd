package it.itskennedy.tsaim.geoad.activities;

import java.util.ArrayList;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.Utils;
import it.itskennedy.tsaim.geoad.R.array;
import it.itskennedy.tsaim.geoad.R.drawable;
import it.itskennedy.tsaim.geoad.R.id;
import it.itskennedy.tsaim.geoad.R.layout;
import it.itskennedy.tsaim.geoad.R.menu;
import it.itskennedy.tsaim.geoad.R.string;
import it.itskennedy.tsaim.geoad.fragment.ActivitiesFragment;
import it.itskennedy.tsaim.geoad.fragment.AugmentedRealityFragment;
import it.itskennedy.tsaim.geoad.fragment.LoginDialogFragment;
import it.itskennedy.tsaim.geoad.fragment.PreferenceFragment;
import it.itskennedy.tsaim.geoad.fragment.SearchListFragment;
import it.itskennedy.tsaim.geoad.fragment.SearchMapFragment;
import it.itskennedy.tsaim.geoad.interfaces.IFragment;
import it.itskennedy.tsaim.geoad.interfaces.ILoginDialogFragment;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements IFragment, ILoginDialogFragment
{
	private Menu mMenu;
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private ArrayList<String> mPlanetTitles;
    ArrayAdapter<String> mAdapter;
	private boolean isLogged;
	private int searchFragmentType;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = new ArrayList<>();
        for (String title : getResources().getStringArray(R.array.navigation_drawer_strings))
        {
        	mPlanetTitles.add(title);
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
//        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mAdapter = new ArrayAdapter<String>(this, R.layout.drawer_list_item, mPlanetTitles);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_launcher,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        
        switch(item.getItemId()) {
        	case R.id.action_change_view:
        		switch (searchFragmentType)
        		{
        			case Utils.TYPE_SEARCH_LIST:
    				    loadFragment(Utils.TYPE_SEARCH_MAP, null);
        				item.setIcon(R.drawable.ic_menu_list);
        				break;
        			case Utils.TYPE_SEARCH_MAP:
    				    loadFragment(Utils.TYPE_SEARCH_LIST, null);
        				item.setIcon(R.drawable.ic_menu_map);
        				break;
        		}
            return true;
        	case R.id.action_filter: 
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        loadFragment(position, null);

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        if(isLogged && (position == 3))
        {
        	String att = "Le mie attività";
        	mPlanetTitles.set(position, att);
        	setTitle(att);
        	mAdapter.notifyDataSetChanged();
        }
        else
        {
        	setTitle(mPlanetTitles.get(position));
        }
        
        mDrawerLayout.closeDrawer(mDrawerList);
    }
    
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

	@Override
	public void loadFragment(int fragmentType, Bundle bundle)
	{
		Fragment vFragment = null;
		
		switch (fragmentType)
		{
			case Utils.TYPE_SEARCH_LIST:
				searchFragmentType = Utils.TYPE_SEARCH_LIST;
				vFragment = SearchListFragment.getInstance(bundle, this);
				break;
			case Utils.TYPE_AUGMENTED_REALITY:
				vFragment = AugmentedRealityFragment.getInstance(bundle);
				break;
			case Utils.TYPE_PREFERENCE:
				vFragment = PreferenceFragment.getInstance(bundle);
				break;
			case Utils.TYPE_ACTIVITIES:
				if(isLogged)
				{
					vFragment = ActivitiesFragment.getInstance(bundle);
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
				vFragment = SearchMapFragment.getInstance(bundle, this);
				break;
			case Utils.TYPE_FILTER:
				LoginDialogFragment loginDialog = new LoginDialogFragment();
				loginDialog.setCancelable(false);
				loginDialog.show(getFragmentManager(), "LoginDialog");
				return;
		}
		
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, vFragment).commit();
	}

	@Override
	public void onLoginButtonPressed(String email, String password)
	{
		if(email.equals("email") && password.equals("password"))
		{
			isLogged = true;
			selectItem(Utils.TYPE_ACTIVITIES);
			Toast.makeText(this, "Loggato", Toast.LENGTH_SHORT).show();
		}
		else
		{
			selectItem(Utils.TYPE_ACTIVITIES);
			Toast.makeText(this, "Errore", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onRegisterButtonPressed()
	{
    	Intent vIntent  = new Intent(MainActivity.this, RegisterActivity.class);
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
		if (mMenu == null) return;
		
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		for (int optionId : options)
			mMenu.findItem(optionId).setVisible(!drawerOpen);
	}
}
