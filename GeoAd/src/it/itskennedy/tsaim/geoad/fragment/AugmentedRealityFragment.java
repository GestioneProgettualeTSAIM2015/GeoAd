package it.itskennedy.tsaim.geoad.fragment;

import it.itskennedy.tsaim.geoad.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AugmentedRealityFragment extends Fragment
{

	public static final String TAG = "augmentedrealityfragment";

	public static AugmentedRealityFragment getInstance(Bundle aBundle)
	{
		AugmentedRealityFragment vFragment = new AugmentedRealityFragment();
		if(aBundle != null)
		{
			vFragment.setArguments(aBundle);
		}
		return vFragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_augmented_reality, container, false);
		return view;
	}
}
