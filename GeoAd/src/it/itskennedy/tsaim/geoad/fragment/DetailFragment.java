package it.itskennedy.tsaim.geoad.fragment;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.ThumbWithDim;
import it.itskennedy.tsaim.geoad.core.ConnectionManager;
import it.itskennedy.tsaim.geoad.core.Engine;
import it.itskennedy.tsaim.geoad.core.ConnectionManager.JsonResponse;
import it.itskennedy.tsaim.geoad.entity.LocationModel;
import it.itskennedy.tsaim.geoad.services.GeoAdService;
import it.itskennedy.tsaim.geoad.services.GeoAdService.GeoAdBinder;
import it.itskennedy.tsaim.geoad.services.GeoAdService.GetLocationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DetailFragment extends Fragment
{
	public static final String LOCATION_ID = "location_id";
	private int mLocationId;
	private LocationModel mLoc;
	private ServiceConnection mServiceConnection = new ServiceConnection()
	{	
		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) 
		{
			Log.d(Engine.APP_NAME, "Service Binded");
			((GeoAdBinder) service).getService().getLocationById(mLocationId, new GetLocationListener()
			{	
				@Override
				public void onLoad(LocationModel aLocation)
				{
					Log.d(Engine.APP_NAME, "Location Load");
					mLoc = aLocation;
					populateFragment(mLoc);
				}
			});
			
			getActivity().unbindService(this);
		}
	};
	private TextView mPCat;
	private TextView mSCat;
	private TextView mType;
	private TextView mDesc;
	private LinearLayout mThumbScroll;
	
	public static DetailFragment getInstance(Bundle aBundle)
	{
		DetailFragment vFragment = new DetailFragment();
		if(aBundle != null)
		{
			vFragment.setArguments(aBundle);
		}
		return vFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		mLocationId = getArguments().getInt(LOCATION_ID);
		setHasOptionsMenu(true);
		
		Intent vBind = new Intent(getActivity(), GeoAdService.class);
		getActivity().bindService(vBind, mServiceConnection , Context.BIND_AUTO_CREATE);
		
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_detail, container, false);
		mPCat = (TextView) view.findViewById(R.id.textViewPCat);
		mSCat = (TextView) view.findViewById(R.id.textViewSCat);
		mType = (TextView) view.findViewById(R.id.textViewType);
		mDesc = (TextView) view.findViewById(R.id.textViewDesc);
		mThumbScroll = (LinearLayout) view.findViewById(R.id.thumbContainer);
		
		return view;
	}

	private void populateFragment(LocationModel mLocation)
	{
		getActivity().setTitle(mLocation.getName());
		mPCat.setText(mLocation.getPCat());
		
		if(!mLocation.getSCat().isEmpty())
		{
			mSCat.setText(mLocation.getSCat());	
		}
		
		mType.setText(mLocation.getType());
		mDesc.setText(mLocation.getDesc());
		
		ConnectionManager.obtain().get("api/photos/fromlocation/" + mLocation.getId(), null, new JsonResponse()
		{
			@Override
			public void onResponse(boolean aResult, Object aResponse) 
			{
				if(aResult && aResponse != null)
				{
					JSONArray vThumbArray = (JSONArray) aResponse;
					for(int i = 0; i < vThumbArray.length(); ++i)
					{
						int vW, vH;
						String vBase64;
						
						try
						{
							JSONObject vImage = vThumbArray.getJSONObject(i);
							
							vW = vImage.getInt("Width");
							vH = vImage.getInt("Height");
							vBase64 = vImage.getString("Base64Thumbnail");
							ThumbWithDim vThumb = new ThumbWithDim(getActivity(), vW, vH, vBase64);
							
							mThumbScroll.addView(vThumb);
						}
						catch (JSONException e)
						{
						}
					}
				}
			}
		});
	}
}
