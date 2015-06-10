package it.itskennedy.tsaim.geoad.fragment;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.interfaces.IFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

public class SearchMapFragment extends Fragment
{

	private static IFragment mListener;

	public static SearchMapFragment getInstance(Bundle aBundle, IFragment listener)
	{
		SearchMapFragment vFragment = new SearchMapFragment();
		if(aBundle != null)
		{
			vFragment.setArguments(aBundle);
		}
		if (listener instanceof IFragment)
		{
			mListener = listener;
		}
		return vFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_search_map, container, false);
		return view;
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
		mListener.toggleActionMenu(new int[] {R.id.action_change_view, R.id.action_filter});
    }
}