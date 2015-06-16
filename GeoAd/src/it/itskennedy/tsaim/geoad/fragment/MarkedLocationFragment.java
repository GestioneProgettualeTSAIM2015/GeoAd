package it.itskennedy.tsaim.geoad.fragment;

import it.itskennedy.tsaim.geoad.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MarkedLocationFragment extends Fragment
{

	public static MarkedLocationFragment getInstance(Bundle aBundle)
	{
		MarkedLocationFragment vFragment = new MarkedLocationFragment();
		if(aBundle != null)
		{
			vFragment.setArguments(aBundle);
		}
		return vFragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_preference, container, false);
		return view;
	}
}