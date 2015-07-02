package it.itskennedy.tsaim.geoad.fragments;

import it.itskennedy.tsaim.geoad.OfferExpandableListAdapter;
import it.itskennedy.tsaim.geoad.OfferExpandableListAdapter.OfferDetail;
import it.itskennedy.tsaim.geoad.OfferExpandableListAdapter.ActionOfferListener;
import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.Utils;
import it.itskennedy.tsaim.geoad.core.ConnectionManager;
import it.itskennedy.tsaim.geoad.core.ConnectionManager.JsonResponse;
import it.itskennedy.tsaim.geoad.core.Engine;
import it.itskennedy.tsaim.geoad.core.Engine.LocationState;
import it.itskennedy.tsaim.geoad.core.Routes;
import it.itskennedy.tsaim.geoad.entity.LocationModel;
import it.itskennedy.tsaim.geoad.entity.Offer;
import it.itskennedy.tsaim.geoad.entity.Thumb;
import it.itskennedy.tsaim.geoad.fragments.EditLocationFragment.ActionType;

import java.util.ArrayList;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
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
	private TextView mName;
	private TextView mType;
	private List<Offer> mOffersList;
	private View mView;
	
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
			mOffersList = new ArrayList<>();
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
				String vName = mLoc.getName();  
				String uriBegin = "geo:" + mLoc.getLat() + "," + mLoc.getLng();  
				String query = mLoc.getLat() + "," + mLoc.getLng() + "(" + vName + ")";  
				String encodedQuery = Uri.encode(query);  
				String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";  
				Uri uri = Uri.parse(uriString);  
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				getActivity().startActivity(intent);
				break;
				
			case R.id.action_mark:
				LocationState vActual = Engine.get().getLocationState(mLoc.getId());
				boolean vIsIgnorable = mLoc.getType().equals(Utils.LOC_TYPE_CA);
				MarkingDialogFragment vDial = MarkingDialogFragment.get(vActual, vIsIgnorable);
				vDial.setTargetFragment(DetailFragment.this, LOCATION_STATE_RC);
				vDial.show(getFragmentManager(), MarkingDialogFragment.TAG);
				break;
				
			default:
				return super.onOptionsItemSelected(item);
		}
		
		return true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mView = inflater.inflate(R.layout.fragment_detail, container, false);
		mPCat = (TextView) mView.findViewById(R.id.textViewPCat);
		mSCat = (TextView) mView.findViewById(R.id.textViewSCat);
		mDesc = (TextView) mView.findViewById(R.id.textViewDesc);
		mThumbScroll = (LinearLayout) mView.findViewById(R.id.thumbContainer);
		mExpandable = (ExpandableListView) mView.findViewById(R.id.expandableListViewOffer);
		mProgressThumb = (ProgressBar) mView.findViewById(R.id.progressBarThumb);
		mName = (TextView) mView.findViewById(R.id.textViewName);
		mType = (TextView) mView.findViewById(R.id.textViewType);
		
		mAdapter = new OfferExpandableListAdapter(getActivity(), mOffersList, new ActionOfferListener()
		{	
			@Override
			public void onAction(ActionType actionType, OfferDetail aToAction)
			{
				switch (actionType) {
				case SHARE:
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_SEND);
	
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_TEXT, aToAction.toString() + "\nPresso " + mLoc.getName() + "\n\nCondiviso da GeoAd");
					startActivity(Intent.createChooser(intent, "Share"));
					
					break;
				case SHARE_FB:
					shareFacebook(aToAction.mId, aToAction.mDesc);
					break;

				default:
					break;
				}
			}
		});
		
		mExpandable.setAdapter(mAdapter);
		mExpandable.setEmptyView(mView.findViewById(R.id.textViewEmpty));
		
		mExpandable.setOnChildClickListener(new OnChildClickListener()
		{	
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
			{
				return false;
			}
		});
		
		mExpandable.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				Utils.setListViewHeight(parent, groupPosition);
				return false;
			}
		});
		FacebookSdk.sdkInitialize(getActivity());
		
		return mView;
	}

	@Override
	public void onStart()
	{
		populateFragment();
		super.onStart();
	}

	private void populateFragment()
	{
		getActivity().getActionBar().setTitle("GeoAd");
		mName.setText(mLoc.getName());
		
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
		mType.setText(vSub);
		
		mPCat.setText(mLoc.getPCat());
		if(mLoc.getSCat() != null && !mLoc.getSCat().isEmpty())
		{
			mSCat.setText(mLoc.getSCat());	
		}
		
		mDesc.setText(mLoc.getDesc());
				
		if(mOffersString == null)
		{
			ConnectionManager.obtain().get(Routes.OFFERS_FROM_LOCATION + mLoc.getId(), null, new JsonResponse()
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
		
		List<Thumb> vCachedThumb = Engine.get().getCache().getThumbs(mLoc.getId());
		if(vCachedThumb != null)
		{
			for(int i = 0; i < vCachedThumb.size(); ++i)
			{
				Thumb vT = vCachedThumb.get(i);
				vT.setOnClickListener(mThumbClickListener);
				mThumbScroll.addView(vT);
			}
		}
		else
		{
			TextView vView = (TextView) mView.findViewById(R.id.no_image);
			vView.setVisibility(TextView.VISIBLE);
		}
		
		if(!mThumbUpdated)
		{
			if(vCachedThumb == null)
			{
				mProgressThumb.setVisibility(ProgressBar.VISIBLE);
				TextView vView = (TextView) mView.findViewById(R.id.no_image);
				vView.setVisibility(TextView.INVISIBLE);
			}
			
			RequestParams vParams = new RequestParams();
			vParams.put("cache", Engine.get().getCache().getThumbIdList(mLoc.getId()).toString());
			
			ConnectionManager.obtain().get(Routes.PHOTO_FROM_LOCATION + mLoc.getId(), vParams, new JsonResponse()
			{
				@Override
				public void onResponse(boolean aResult, Object aResponse) 
				{
					if(aResult && aResponse != null)
					{
						JSONArray vThumbArray = (JSONArray) aResponse;
						
						if(vThumbArray.length() == 0)
						{
							TextView vView = (TextView) mView.findViewById(R.id.no_image);
							vView.setVisibility(TextView.VISIBLE);
						}
						
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
				}
			});
		}
	}
	
	private void fillOffersList(JSONArray aArray)
	{
		if (mOffersList != null) {
			mOffersList.clear();
			mOffersList.addAll(Offer.getListFromJsonArray(aArray));
			mAdapter.notifyDataSetChanged();	
		}
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
	
	private void shareFacebook(int aId, String aDesc)
	{
		if (ShareDialog.canShow(ShareLinkContent.class))
		{
			ShareLinkContent content = new ShareLinkContent.Builder()
				.setContentTitle("Offerta Geoad")
				.setContentDescription(aDesc)
				.setContentUrl(Uri.parse(Engine.SERVER_URL + Routes.OFFER_DETAIL_DASHBOARD + aId))
				.build();

			ShareDialog shareDialog = new ShareDialog(getActivity());
		    shareDialog.show(content);
		}		
	}
}
