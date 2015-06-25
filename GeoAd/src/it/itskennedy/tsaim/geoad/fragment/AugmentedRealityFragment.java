package it.itskennedy.tsaim.geoad.fragment;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.activities.AugmentedRealityActivity;
import it.itskennedy.tsaim.geoad.activities.MainActivity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
		
		Button btnAug = (Button) view.findViewById(R.id.btn_augmented);
		
		btnAug.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), AugmentedRealityActivity.class);
				startActivity(i);
			}
		});
		return view;
	}
}
