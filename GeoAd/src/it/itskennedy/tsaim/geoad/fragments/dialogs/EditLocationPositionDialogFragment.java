package it.itskennedy.tsaim.geoad.fragments.dialogs;

import it.itskennedy.tsaim.geoad.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class EditLocationPositionDialogFragment extends DialogFragment {
	
	private final static String MAP_FRAGMENT_TAG = "mapfragmenttag";
	private final static String CUR_POS_TAG = "curpostag";
	
	public interface IEditLocationPositionDialogFragment {
		public void changePosition(LatLng newPos);
	}
	
	private IEditLocationPositionDialogFragment iHandler;
	
	public static EditLocationPositionDialogFragment getNewInstance(LatLng curPos) {
		EditLocationPositionDialogFragment instance = new EditLocationPositionDialogFragment();
		Bundle args = new Bundle();
		args.putParcelable(CUR_POS_TAG, curPos);
		instance.setArguments(args);
		instance.iHandler = new IEditLocationPositionDialogFragment() { public void changePosition(LatLng newPos) {} };
		return instance;
	}
	
	private MyMapFragment mMapFragment;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		View vView = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_edit_location_position, null);
		
		builder
		.setTitle(getResources().getString(R.string.edit_position))
		.setView(vView)
		.setPositiveButton(getResources().getString(R.string.edit), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (iHandler != null) {
					iHandler.changePosition(null); //TODO
				}
			}
		})
		.setNegativeButton(getResources().getString(R.string.cancel), null);
		
		if (savedInstanceState == null) {
			mMapFragment = MyMapFragment.getNewInstance(getCurrentPosition());
			getChildFragmentManager().beginTransaction()
				.add(R.id.mapContainer, mMapFragment, MAP_FRAGMENT_TAG).commit();
		} else {
			mMapFragment = (MyMapFragment) getChildFragmentManager().findFragmentByTag(MAP_FRAGMENT_TAG);
		}
		
		return builder.create();
	}
	
	private LatLng getCurrentPosition() {
		Bundle args = getArguments();
		if (args != null) return args.getParcelable(CUR_POS_TAG);
		return null;
	}
	
	@Override
	public void setTargetFragment(Fragment fragment, int requestCode) {
		
		if (fragment instanceof IEditLocationPositionDialogFragment)
			iHandler = (IEditLocationPositionDialogFragment) fragment;
		
		super.setTargetFragment(fragment, requestCode);
	}
	
	static class MyMapFragment extends MapFragment {
		
		public static MyMapFragment getNewInstance(LatLng curPos) {
			MyMapFragment instance = new MyMapFragment();
			Bundle args = new Bundle();
			args.putParcelable(CUR_POS_TAG, curPos);
			instance.setArguments(args);
			return instance;
		}
		
		@Override
		public View onCreateView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
		    View v = super.onCreateView(arg0, arg1, arg2);
		    
		    LatLng curPos = getCurrentPosition();
		    if (curPos != null) {
		    	getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(curPos, 16));
		    	getMap().addMarker(new MarkerOptions().position(curPos));
		    }
		    
		    return v;
		}
		
		private LatLng getCurrentPosition() {
			Bundle args = getArguments();
			if (args != null) return args.getParcelable(CUR_POS_TAG);
			return null;
		}
	}
}
