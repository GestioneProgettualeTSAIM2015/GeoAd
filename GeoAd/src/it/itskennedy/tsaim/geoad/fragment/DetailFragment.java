package it.itskennedy.tsaim.geoad.fragment;

import it.itskennedy.tsaim.geoad.OfferExpandableListAdapter;
import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.Utils;
import it.itskennedy.tsaim.geoad.core.ConnectionManager;
import it.itskennedy.tsaim.geoad.core.ConnectionManager.JsonResponse;
import it.itskennedy.tsaim.geoad.core.Engine;
import it.itskennedy.tsaim.geoad.core.Engine.LocationState;
import it.itskennedy.tsaim.geoad.entity.LocationModel;
import it.itskennedy.tsaim.geoad.entity.Offer;
import it.itskennedy.tsaim.geoad.entity.Thumb;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

public class DetailFragment extends Fragment
{
	public static final String TAG = "detail_fragment";
	public static final int LOCATION_STATE_RC = 0;
	
	private static final String LOCATION = "location";
	private static final String OFFERS = "offers";
	private static final String THUMB = "thumb";
	private LocationModel mLoc;
	private TextView mPCat;
	private TextView mSCat;
	private TextView mDesc;
	private LinearLayout mThumbScroll;
	private ExpandableListView mExpandable;
	private OfferExpandableListAdapter mAdapter;
	private ProgressBar mProgressThumb;
	private String mOffersString;
	private boolean mThumbUpdated = false;
	
	private OnClickListener mThumbClickListener = new OnClickListener()
	{	
		@Override
		public void onClick(View v)
		{
			if(v instanceof Thumb)
			{
				ImageDialog vDialog = ImageDialog.get(((Thumb) v).mId);
				vDialog.setTargetFragment(DetailFragment.this, LOCATION_STATE_RC);
				vDialog.show(getActivity().getFragmentManager(), ImageDialog.TAG);	
			}
		}
	};
	
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
		if(savedInstanceState == null)
		{
			mLoc = LocationModel.fromBundle(getArguments());
		}
		else
		{
			mThumbUpdated = savedInstanceState.getBoolean(THUMB);
			mLoc = LocationModel.fromBundle(savedInstanceState.getBundle(LOCATION));
			mOffersString = savedInstanceState.getString(OFFERS);
		}
		
		setHasOptionsMenu(true);
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
        inflater.inflate(R.menu.detail_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.action_map:
			{ 
				String vName = mLoc.getName();  
				String uriBegin = "geo:" + mLoc.getLat() + "," + mLoc.getLng();  
				String query = mLoc.getLat() + "," + mLoc.getLng() + "(" + vName + ")";  
				String encodedQuery = Uri.encode(query);  
				String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";  
				Uri uri = Uri.parse(uriString);  
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				getActivity().startActivity(intent);
			
				return true;
			}
			case R.id.action_mark:
			{
				LocationState vActual = Engine.get().getLocationState(mLoc.getId());
				boolean vIsIgnorable = mLoc.getType().equals(Utils.LOC_TYPE_CA);
				MarkingDialogFragment vDial = MarkingDialogFragment.get(vActual, vIsIgnorable);
				vDial.setTargetFragment(DetailFragment.this, LOCATION_STATE_RC);
				vDial.show(getFragmentManager(), MarkingDialogFragment.TAG);
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_detail, container, false);
		mPCat = (TextView) view.findViewById(R.id.textViewPCat);
		mSCat = (TextView) view.findViewById(R.id.textViewSCat);
		mDesc = (TextView) view.findViewById(R.id.textViewDesc);
		mThumbScroll = (LinearLayout) view.findViewById(R.id.thumbContainer);
		mExpandable = (ExpandableListView) view.findViewById(R.id.expandableListViewOffer);
		mProgressThumb = (ProgressBar) view.findViewById(R.id.progressBarThumb);
		
		return view;
	}

	@Override
	public void onStart()
	{
		populateFragment();
		super.onStart();
	}

	private void populateFragment()
	{
		getActivity().setTitle(mLoc.getName());
		
		String vSub = "";
		switch(mLoc.getType())
		{
			case Utils.LOC_TYPE_CA:
			{
				vSub = getString(R.string.ca);
				break;
			}
			case Utils.LOC_TYPE_POI:
			{
				vSub = getString(R.string.poi);
				break;
			}
		}
		getActivity().getActionBar().setSubtitle(vSub);
		
		mPCat.setText(mLoc.getPCat());
		if(!mLoc.getSCat().isEmpty())
		{
			mSCat.setText("(" + mLoc.getSCat() + ")");	
		}
		
		mDesc.setText(mLoc.getDesc());
		
		List<Thumb> vCachedThumb = Engine.get().getCache().getThumbs(mLoc.getId());
		if(vCachedThumb == null || vCachedThumb.size() == 0)
		{
			mProgressThumb.setVisibility(ProgressBar.VISIBLE);
		}
		else
		{
			for(int i = 0; i < vCachedThumb.size(); ++i)
			{
				Thumb vT = vCachedThumb.get(i);
				vT.setOnClickListener(mThumbClickListener);
				mThumbScroll.addView(vT);
			}
		}
				
		if(mOffersString == null)
		{
			ConnectionManager.obtain().get("api/offerings/fromlocation/" + mLoc.getId(), null, new JsonResponse()
			{	
				@Override
				public void onResponse(boolean aResult, Object aResponse)
				{
					if(aResult)
					{
						mOffersString = ((JSONArray) aResponse).toString();
						fillOffersList((JSONArray)aResponse);
					}
					else
					{
						
					}
				}
			});
		}
		else
		{
			try 
			{
				fillOffersList(new JSONArray(mOffersString));
			} 
			catch (JSONException e)
			{
			}
		}
		
		if(!mThumbUpdated)
		{
			RequestParams vParams = new RequestParams();
			vParams.put("cache", Engine.get().getCache().getThumbIdList(mLoc.getId()).toString());
			
			ConnectionManager.obtain().get("api/photos/fromlocation/" + mLoc.getId(), vParams, new JsonResponse()
			{
				@Override
				public void onResponse(boolean aResult, Object aResponse) 
				{
					if(aResult && aResponse != null)
					{
						JSONArray vThumbArray = (JSONArray) aResponse;
						for(int i = 0; i < vThumbArray.length(); ++i)
						{
							int vId;
							String vBase64;
							
							try
							{
								JSONObject vImage = vThumbArray.getJSONObject(i);
								
								vId = vImage.getInt("Id");
							
								if(vId > 0)
								{
									vBase64 = vImage.getString("Base64Thumbnail");
									
									if(!alreadyAdded(vId))
									{
										Thumb vThumb = new Thumb(getActivity(), vId, vBase64);
										vThumb.setOnClickListener(mThumbClickListener);
										
										Engine.get().getCache().cacheThumb(vThumb, mLoc.getId());
										mThumbScroll.addView(vThumb);
									}
								}
								else
								{
									removeThumb(-vId);
								}
							}
							catch (JSONException e)
							{
							}
						}
						
						mThumbUpdated = true;
						mProgressThumb.setVisibility(ProgressBar.INVISIBLE);
					}
					else
					{
						
					}
				}
			});
		}
	}
	
	private void fillOffersList(JSONArray aArray)
	{
		List<Offer> vList = Offer.getListFromJsonArray(aArray);
		mAdapter = new OfferExpandableListAdapter(getActivity(), vList);
		mExpandable.setAdapter(mAdapter);
		mExpandable.setEmptyView(getView().findViewById(R.id.textViewEmpty));
		
		mExpandable.setOnChildClickListener(new OnChildClickListener()
		{	
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
			{
				// TODO dialog condivisione? topas 
				return false;
			}
		});
	}
	
	private boolean alreadyAdded(int aThumbId)
	{
		for(int j = 0; j < mThumbScroll.getChildCount(); ++j)
		{
			View vView = mThumbScroll.getChildAt(j);
			
			if(vView instanceof Thumb)
			{
				if(((Thumb)vView).mId == aThumbId)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	private void removeThumb(int aId)
	{
		for(int j = 0; j < mThumbScroll.getChildCount(); ++j)
		{
			View vView = mThumbScroll.getChildAt(j);
			
			if(vView instanceof Thumb)
			{
				if(((Thumb)vView).mId == aId)
				{
					mThumbScroll.removeView(vView);
					--j;
					continue;
				}
			}
		}
		
		Engine.get().getCache().remove(aId, mLoc.getId());
	}
	
	@Override
	public void onStop()
	{
		mThumbScroll.removeAllViews();
		getActivity().getActionBar().setSubtitle(null);
		super.onStop();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		outState.putBoolean(THUMB, mThumbUpdated);
		outState.putBundle(LOCATION, getArguments());
		outState.putString(OFFERS, mOffersString);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == LOCATION_STATE_RC && resultCode == Activity.RESULT_OK && data != null)
		{
			Engine.get().updateLocationState(mLoc, LocationState.values()[data.getIntExtra(MarkingDialogFragment.LOCATION_STATE, 0)]);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}	
}
