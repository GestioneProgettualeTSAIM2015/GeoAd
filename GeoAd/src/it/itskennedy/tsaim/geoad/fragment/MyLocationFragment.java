package it.itskennedy.tsaim.geoad.fragment;

import it.itskennedy.tsaim.geoad.MyLocationAdapter;
import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.Utils;
import it.itskennedy.tsaim.geoad.core.ConnectionManager;
import it.itskennedy.tsaim.geoad.core.ConnectionManager.JsonResponse;
import it.itskennedy.tsaim.geoad.core.Engine;
import it.itskennedy.tsaim.geoad.entity.LocationModel;
import it.itskennedy.tsaim.geoad.interfaces.IFragment;
import it.itskennedy.tsaim.geoad.localdb.DataFavContentProvider;
import it.itskennedy.tsaim.geoad.localdb.FavoritesHelper;
import it.itskennedy.tsaim.geoad.localdb.MyLocationHelper;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class MyLocationFragment extends Fragment implements LoaderCallbacks<Cursor>
{
	private static final int LOADER_ID = 0;
	private MyLocationAdapter mAdapter;
	protected IFragment mListener;
	
	public static MyLocationFragment getInstance()
	{
		MyLocationFragment vFragment = new MyLocationFragment();
		return vFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_mylocation, container, false);
		
		ListView vScroll = (ListView) view.findViewById(R.id.ListViewMyLoc);
		mAdapter = new MyLocationAdapter(getActivity());
		vScroll.setEmptyView(view.findViewById(R.id.textViewEmptyMy));
		vScroll.setAdapter(mAdapter);
		
		vScroll.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int aPos, long aId)
			{
				Cursor vC = getActivity().getContentResolver().query(DataFavContentProvider.MYLOC_URI, null, MyLocationHelper._ID + " = " + aId, null, null);
				
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
					
					LocationModel vLoc = new LocationModel((int)aId, vPCat,vSCat, vName, vLat, vLng, vDesc, vType);
					
					mListener.loadFragment(Utils.TYPE_DETAIL, vLoc.getBundle());
				}
			}
		});
		
		vScroll.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long aId)
			{
				DialogDelete vDialogDelete = DialogDelete.getInstance(0, (int)aId, getString(R.string.remove_myloc));
				vDialogDelete.setTargetFragment(MyLocationFragment.this, DialogDelete.DELETE_CODE);
				vDialogDelete.show(getFragmentManager(), DialogDelete.TAG);
				return true;
			}
		});
		
		getLoaderManager().initLoader(LOADER_ID, null, this);
		
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if(requestCode == DialogDelete.DELETE_CODE && resultCode == Activity.RESULT_OK && data != null)
		{
			final int vId = data.getIntExtra(DialogDelete.LOCATION_ID, 0);
			
			ConnectionManager.obtain().delete("api/locations?Id=" + vId, new JsonResponse()
			{	
				@Override
				public void onResponse(boolean aResult, Object aResponse)
				{
					if(aResult)
					{
						Engine.get().removeMyLocation(vId);
					}
				}
			});
		}
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int aId, Bundle aBundle)
	{
		if(aId == LOADER_ID)
		{
			return new CursorLoader(getActivity(), DataFavContentProvider.MYLOC_URI, null, null, null, null);	
		}
		
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> aLoader, Cursor aCursor)
	{
		mAdapter.swapCursor(aCursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> aLoader)
	{
		mAdapter.swapCursor(null);
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
