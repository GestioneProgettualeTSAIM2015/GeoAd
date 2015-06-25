package it.itskennedy.tsaim.geoad.fragment;

import it.itskennedy.tsaim.geoad.MarkedExpandableListAdapter;
import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.Utils;
import it.itskennedy.tsaim.geoad.core.Engine;
import it.itskennedy.tsaim.geoad.core.SettingsManager;
import it.itskennedy.tsaim.geoad.entity.LocationModel;
import it.itskennedy.tsaim.geoad.interfaces.IFragment;
import it.itskennedy.tsaim.geoad.localdb.DataFavContentProvider;
import it.itskennedy.tsaim.geoad.localdb.FavoritesHelper;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class MarkedLocationFragment extends Fragment
{	
	public static final String TAG = "marked_loc";
	
	private ExpandableListView mExpandable;
	private MarkedExpandableListAdapter mAdapter;

	private IFragment mListener;

	public static MarkedLocationFragment getInstance(Bundle aBundle) 
	{
		MarkedLocationFragment vFragment = new MarkedLocationFragment();
		if (aBundle != null) 
		{
			vFragment.setArguments(aBundle);
		}
		return vFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_marked, container, false);

		if(SettingsManager.get(getActivity()).getHelpPref())
		{
			MarkedHelpDialog vD = MarkedHelpDialog.get();
			vD.show(getFragmentManager(), MarkedHelpDialog.TAG);
		}
		
		mExpandable = (ExpandableListView) view
				.findViewById(R.id.expandableListViewMarked);

		mAdapter = new MarkedExpandableListAdapter(getActivity());
		mExpandable.setAdapter(mAdapter);

		mExpandable.setOnGroupClickListener(new OnGroupClickListener()
		{
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
			{
				if(groupPosition == 0 && mAdapter.getFavourite() > 0)
				{
					return false;
				}
				
				if(groupPosition == 1 && mAdapter.getIgnored() > 0)
				{
					return false;
				}
				
				return true;
			}
		});
		
		mExpandable.setOnChildClickListener(new OnChildClickListener()
		{	
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
			{
				if(groupPosition == 0 && mListener != null)
				{
					Cursor vC = getActivity().getContentResolver().query(DataFavContentProvider.FAVORITES_URI, null, FavoritesHelper._ID + " = " + id, null, null);
					
					if(vC.moveToFirst())
					{
						int vDescIndex = vC.getColumnIndex(FavoritesHelper.DESC);
						int vLatIndex = vC.getColumnIndex(FavoritesHelper.LAT);
						int vLngIndex = vC.getColumnIndex(FavoritesHelper.LNG);
						int vNameIndex = vC.getColumnIndex(FavoritesHelper.NAME);
						int vPCatIndex = vC.getColumnIndex(FavoritesHelper.PCAT);
						int vSCatIndex = vC.getColumnIndex(FavoritesHelper.SCAT);
						int vTypeIndex = vC.getColumnIndex(FavoritesHelper.TYPE);
						
						String vDesc = vC.getString(vDescIndex);
						double vLat = vC.getDouble(vLatIndex);
						double vLng = vC.getDouble(vLngIndex);
						String vName = vC.getString(vNameIndex);
						String vPCat = vC.getString(vPCatIndex);
						String vSCat = vC.getString(vSCatIndex);
						String vType = vC.getString(vTypeIndex);
						
						vC.close();
						
						LocationModel vLoc = new LocationModel((int)id, vPCat,vSCat, vName, vLat, vLng, vDesc, vType);
						
						mListener.loadFragment(Utils.TYPE_DETAIL, vLoc.getBundle());
					}
				}
				return true;
			}
		});
		
		
		mExpandable.setOnItemLongClickListener(new OnItemLongClickListener()
		{
		    @Override
		    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) 
		    {
		        if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) 
		        {
		        	long packedPosition = ((ExpandableListView) mExpandable).getExpandableListPosition(position);
		            int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
		            
		            DialogDelete vDialogDelete = DialogDelete.getInstance(groupPosition, (int)id);
					vDialogDelete.setTargetFragment(MarkedLocationFragment.this, DialogDelete.DELETE_CODE);
					vDialogDelete.show(getFragmentManager(), DialogDelete.TAG);
					return true;
				}

		        return false;
		    }
		});
		
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if(requestCode == DialogDelete.DELETE_CODE && resultCode == Activity.RESULT_OK && data != null)
		{
			int vGroup = data.getIntExtra(DialogDelete.GROUP_ID, 0);
			int vId = data.getIntExtra(DialogDelete.LOCATION_ID, 0);
			
			mAdapter.remove(vGroup, vId);
			
			if(vGroup == 0)
			{
				if(mAdapter.getFavourite() == 0)
				{
					mExpandable.collapseGroup(0);
				}
			}
			else
			{
				if(mAdapter.getIgnored() == 0)
				{
					mExpandable.collapseGroup(1);
				}
			}
			
			Engine.get().setNeutralLocationState(vId);
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
