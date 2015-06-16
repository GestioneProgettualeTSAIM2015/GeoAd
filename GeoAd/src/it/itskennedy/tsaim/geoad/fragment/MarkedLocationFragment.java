package it.itskennedy.tsaim.geoad.fragment;

import it.itskennedy.tsaim.geoad.MarkedExpandableListAdapter;
import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.localdb.DataFavContentProvider;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class MarkedLocationFragment extends Fragment
{	
	public static final int DELETE_CODE = 1;
	
	private ExpandableListView mExpandable;
	private MarkedExpandableListAdapter mAdapter;

	public static MarkedLocationFragment getInstance(Bundle aBundle) {
		MarkedLocationFragment vFragment = new MarkedLocationFragment();
		if (aBundle != null) {
			vFragment.setArguments(aBundle);
		}
		return vFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_marked, container, false);

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
				DialogDelete vDialogDelete = DialogDelete.getInstance(groupPosition, (int)id);
				vDialogDelete.setTargetFragment(MarkedLocationFragment.this, DELETE_CODE);
				vDialogDelete.show(getFragmentManager(), DialogDelete.TAG);
				return true;
			}
		});
		
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if(requestCode == DELETE_CODE && resultCode == Activity.RESULT_OK && data != null)
		{
			int vGroup = data.getIntExtra(DialogDelete.GROUP_ID, 0);
			int vId = data.getIntExtra(DialogDelete.LOCATION_ID, 0);
			
			mAdapter.remove(vGroup, vId);
			
			if(vGroup == 0)
			{
				getActivity().getContentResolver().delete(DataFavContentProvider.FAVORITES_URI, BaseColumns._ID + " = " + vId, null);
				if(mAdapter.getFavourite() == 0)
				{
					mExpandable.collapseGroup(0);
				}
			}
			else
			{
				getActivity().getContentResolver().delete(DataFavContentProvider.IGNORED_URI, BaseColumns._ID + " = " + vId, null);
				if(mAdapter.getIgnored() == 0)
				{
					mExpandable.collapseGroup(1);
				}
			}
		
			mAdapter.notifyDataSetChanged();
		}
	}	
}
