package it.itskennedy.tsaim.geoad.fragment;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.fragment.dialogs.EditLocationDescriptionDialogFragment;
import it.itskennedy.tsaim.geoad.fragment.dialogs.EditLocationNameDialogFragment;
import it.itskennedy.tsaim.geoad.fragment.dialogs.EditLocationPositionDialogFragment;
import it.itskennedy.tsaim.geoad.fragment.dialogs.NewOfferDialogFragment;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

public class EditLocationFragment extends Fragment {
	
	public final static String NEW_OFFER_DIALOG_FRAGMENT_TAG = "newofferdialogfragmenttag",
							   EDIT_LOCATION_NAME_FRAGMENT_TAG = "editlocationnamefragmenttag",
							   EDIT_LOCATION_DESCRIPTION_FRAGMENT_TAG = "editlocationdescriptionfragmenttag",
							   EDIT_LOCATION_POSITION_FRAGMENT_TAG = "editlocationpositionfragmenttag";
	
	public final static String NAME_TAG = "nametag",
							   DESCRIPTION_TAG = "descriptiontag",
							   POSITION_TAG = "positiontag";
	
	public static EditLocationFragment getNewInstance(Bundle aBundle) {
		EditLocationFragment editLocationFragment = new EditLocationFragment();
		if (aBundle != null) editLocationFragment.setArguments(aBundle);
		return editLocationFragment;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(android.R.layout.simple_list_item_1, container, false); //TODO
		
		setHasOptionsMenu(true);
		
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.edit_location_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
		
	    switch (item.getItemId()) {
	        case R.id.add_photo:
	            break;
	            
	        case R.id.add_offering:
	        	ft.add(NewOfferDialogFragment.getNewInstance(), NEW_OFFER_DIALOG_FRAGMENT_TAG);
	            break;
	            
	        case R.id.edit_location_name:
	        	ft.add(EditLocationNameDialogFragment.getNewInstance(getArguments().getString(NAME_TAG)),
	        		   EDIT_LOCATION_NAME_FRAGMENT_TAG);
	            break;
	            
	        case R.id.edit_location_description:
	        	ft.add(EditLocationDescriptionDialogFragment.getNewInstance(getArguments().getString(DESCRIPTION_TAG)),
	        		   EDIT_LOCATION_DESCRIPTION_FRAGMENT_TAG);
	            break;
	            
	        case R.id.edit_location_position:
	        	ft.add(EditLocationPositionDialogFragment.getNewInstance((LatLng) (getArguments().getParcelable(POSITION_TAG))),
	        		   EDIT_LOCATION_POSITION_FRAGMENT_TAG);
	            break;
	            
            default:
            	return false;
	    }
	    
	    ft.commit();
	    return true;
	}
	
	//--------------------------------------

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
}
