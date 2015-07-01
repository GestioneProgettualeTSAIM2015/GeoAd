package it.itskennedy.tsaim.geoad.fragment;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.itskennedy.tsaim.geoad.OfferExpandableListAdapter;
import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.Utils;
import it.itskennedy.tsaim.geoad.OfferExpandableListAdapter.OfferDetail;
import it.itskennedy.tsaim.geoad.OfferExpandableListAdapter.ActionOfferListener;
import it.itskennedy.tsaim.geoad.activities.MainActivity;
import it.itskennedy.tsaim.geoad.core.ConnectionManager;
import it.itskennedy.tsaim.geoad.core.Engine;
import it.itskennedy.tsaim.geoad.core.Routes;
import it.itskennedy.tsaim.geoad.core.ConnectionManager.JsonResponse;
import it.itskennedy.tsaim.geoad.core.Engine.LocationState;
import it.itskennedy.tsaim.geoad.entity.LocationModel;
import it.itskennedy.tsaim.geoad.entity.Offer;
import it.itskennedy.tsaim.geoad.entity.Thumb;
import it.itskennedy.tsaim.geoad.fragment.dialogs.DeleteImageAlertDialog;
import it.itskennedy.tsaim.geoad.fragment.dialogs.EditLocationDescriptionDialogFragment;
import it.itskennedy.tsaim.geoad.fragment.dialogs.EditLocationDescriptionDialogFragment.IEditLocationDescriptionDialogFragment;
import it.itskennedy.tsaim.geoad.fragment.dialogs.EditLocationNameDialogFragment;
import it.itskennedy.tsaim.geoad.fragment.dialogs.EditLocationNameDialogFragment.IEditLocationNameDialogFragment;
import it.itskennedy.tsaim.geoad.fragment.dialogs.NewOfferDialogFragment;
import it.itskennedy.tsaim.geoad.fragment.dialogs.NewOfferDialogFragment.INewOfferDialogFragment;
import it.itskennedy.tsaim.geoad.interfaces.IFragment;
import it.itskennedy.tsaim.geoad.localdb.DataFavContentProvider;
import it.itskennedy.tsaim.geoad.localdb.DataOffersContentProvider;
import it.itskennedy.tsaim.geoad.localdb.OffersHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.beyondar.android.util.ImageUtils;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.RequestParams;

public class EditLocationFragment extends Fragment implements INewOfferDialogFragment, 
IEditLocationNameDialogFragment, IEditLocationDescriptionDialogFragment {
	
	public static final String TAG = "edit_fragment";
	
	public final static String NEW_OFFER_DIALOG_FRAGMENT_TAG = "newofferdialogfragmenttag",
							   EDIT_LOCATION_NAME_FRAGMENT_TAG = "editlocationnamefragmenttag",
							   EDIT_LOCATION_DESCRIPTION_FRAGMENT_TAG = "editlocationdescriptionfragmenttag";
	
	public enum ActionType {SHARE, DELETE}
	
	public static final int LOCATION_STATE_RC = 0;
	
	private static final String LOCATION = "location";
	private static final String OFFERS = "offers";
	private static final String THUMB = "thumb";

	protected static final int TAKE_PHOTO = 1;
	protected static final int PHOTO_FROM_GALLERY = 2;
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
	
	private File mPhotoFile;
	private Bitmap mSelectedImage;
	private String mPath;
	
	private String mUriString;
	
	public static EditLocationFragment getInstance(Bundle aBundle)
	{
		EditLocationFragment vFragment = new EditLocationFragment();
		if(aBundle != null)
		{
			vFragment.setArguments(aBundle);
		}
		return vFragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_detail, container, false);
		mPCat = (TextView) view.findViewById(R.id.textViewPCat);
		mSCat = (TextView) view.findViewById(R.id.textViewSCat);
		mDesc = (TextView) view.findViewById(R.id.textViewDesc);
		mThumbScroll = (LinearLayout) view.findViewById(R.id.thumbContainer);
		mExpandable = (ExpandableListView) view.findViewById(R.id.expandableListViewOffer);
		mProgressThumb = (ProgressBar) view.findViewById(R.id.progressBarThumb);
		setHasOptionsMenu(true);
		
		mPhotoFile = Utils.createPhotoFile();
		mPath = mPhotoFile.getAbsolutePath();
		
		mExpandable.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				Utils.setListViewHeight(parent, groupPosition);
				return false;
			}
		});
		
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.edit_location_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
		Fragment vFrag = null;
		
	    switch (item.getItemId()) {
	        case R.id.add_photo:
	        	selectPhoto();
	        	return false;
	        	
	        case R.id.add_offering:
	        	ft.add((vFrag = NewOfferDialogFragment.getNewInstance()), NEW_OFFER_DIALOG_FRAGMENT_TAG);
	            break;
	            
	        case R.id.edit_location_name:
	        	ft.add(vFrag = EditLocationNameDialogFragment.getNewInstance(getArguments().getString(LocationModel.NAME)),
	        		   EDIT_LOCATION_NAME_FRAGMENT_TAG);
	            break;
	        case R.id.edit_location_description:
	        	ft.add(vFrag = EditLocationDescriptionDialogFragment.getNewInstance(getArguments().getString(LocationModel.DESC)),
	        		   EDIT_LOCATION_DESCRIPTION_FRAGMENT_TAG);
	            break;
	           	            
            default:
            	return false;
	    }
	    
	    vFrag.setTargetFragment(this, 0);
	    ft.commit();
	    return true;
	}
	
	private OnClickListener mThumbClickListener = new OnClickListener()
	{	
		@Override
		public void onClick(View v)
		{
			if(v instanceof Thumb)
			{
				ImageDialog vDialog = ImageDialog.get(((Thumb) v).mId);
				vDialog.setTargetFragment(EditLocationFragment.this, LOCATION_STATE_RC);
				vDialog.show(getActivity().getFragmentManager(), ImageDialog.TAG);	
			}
		}
	};
	
	private OnLongClickListener mThumbLongClickListener = new OnLongClickListener()
	{

		@Override
		public boolean onLongClick(View v) {
			if(v instanceof Thumb)
			{
				Thumb vThumb = (Thumb) v;
				
				DeleteImageAlertDialog vDialog = DeleteImageAlertDialog.getInstance(vThumb.mId);
				vDialog.setTargetFragment(EditLocationFragment.this, 0);
				vDialog.show(getActivity().getFragmentManager(), DeleteImageAlertDialog.TAG);

			}
			return false;
		}	

	};

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
				vT.setOnLongClickListener(mThumbLongClickListener);
				mThumbScroll.addView(vT);
			}
		}
		if(!mThumbUpdated) {
			if(vCachedThumb == null)
				mProgressThumb.setVisibility(ProgressBar.VISIBLE);
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
										vThumb.setOnLongClickListener(mThumbLongClickListener);

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
	
	private void selectPhoto()
	{
		final CharSequence[] items =
		{ getActivity().getString(R.string.take_photo), getActivity().getString(R.string.choose_image) };

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setItems(items, new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int item)
			{
				switch (item)
				{
					case 0:
					{
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						if (intent.resolveActivity(getActivity().getPackageManager()) != null)
						{
							if (mPhotoFile != null)
							{
								intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
								startActivityForResult(intent, TAKE_PHOTO);
							}
						}
						break;
					}
					case 1:
					{
						Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						intent.setType("image/*");
						startActivityForResult(intent, PHOTO_FROM_GALLERY);
						break;
					}
				}
			}
		});
			
		builder.setNegativeButton(getActivity().getString(R.string.cancel), null);
		builder.show();
	}
	

	private void manageUri(Uri vUri)
	{
		Toast.makeText(getActivity() ,"Upload in corso...", Toast.LENGTH_SHORT).show();

		mUriString = vUri.toString();
		mSelectedImage = null; 
		try
		{
			mSelectedImage = Utils.getCorrectlyOrientedImage(getActivity(), Uri.parse(mUriString));
		}
		catch (IOException e)
		{
			Log.d("IMAGE ERROR","Image Error");
		}
		
		String vBase64 = Utils.BitmapToBase64(mSelectedImage);

		RequestParams vParams = new RequestParams();
		vParams.put("LocationId", mLoc.getId());
		vParams.put("Base64Data", vBase64);
		
		ConnectionManager.obtain().post(Routes.PHOTO_FROM_LOCATION, 
				vParams, new JsonResponse() {

			@Override
			public void onResponse(boolean aResult, Object aResponse) {
				if (aResult) 
				{
					Toast.makeText(getActivity(), getResources().getString(R.string.edit_upload_finished), Toast.LENGTH_SHORT).show();
					JSONObject json = (JSONObject) aResponse;
					int thId = -1;
					String thData = null;
					try {
						thData = json.getString("Base64Thumbnail");
						thId = json.getInt("Id");
					} catch (JSONException e) {
						
						e.printStackTrace();
					}
					
					Thumb vThumb = new Thumb(getActivity(), thId, thData);
					vThumb.setOnClickListener(mThumbClickListener);
					vThumb.setOnLongClickListener(mThumbLongClickListener);
					mThumbScroll.addView(vThumb);
					Engine.get().getCache().cacheThumb(vThumb, mLoc.getId());
				}
				resetPhotoFile();
			}});
		
	}
	
	public void resetPhotoFile()
	{
		if(mPath != null)
		{
			File file = new File(mPath);
			file.delete();
			mPath = null;
		}
	}
	
	private void fillOffersList(JSONArray aArray)
	{
		List<Offer> vList = Offer.getListFromJsonArray(aArray);
		mAdapter = new OfferExpandableListAdapter(getActivity(), vList, new ActionOfferListener() {
			
			@Override
			public void onAction(ActionType actionType, final OfferDetail aToAction) {
				switch (actionType) {
				case SHARE:
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_SEND);
	
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_TEXT, "Location: " + mLoc.getName() + " aToShare: " + aToAction.toString());
					startActivity(Intent.createChooser(intent, "Share"));
					break;
				case DELETE:
					ConnectionManager.obtain().delete(Routes.OFFERS + "/" + aToAction.mId, new JsonResponse() {

						@Override
						public void onResponse(boolean aResult,Object aResponse) {
							if (aResult) {
								Toast.makeText(getActivity(), getResources().getString(R.string.edit_offer_deleted), Toast.LENGTH_SHORT).show();
								mAdapter.remove(aToAction.mId);
								getActivity().getContentResolver().delete(DataOffersContentProvider.OFFERS_URI, OffersHelper.OFF_ID + " = " + aToAction.mId, null);
							}
							
						}});
				
					break;

				default:
					break;
				}
				
			}
		});

		mExpandable.setAdapter(mAdapter);
		mExpandable.setEmptyView(getView().findViewById(R.id.textViewEmpty));
		
		mExpandable.setOnChildClickListener(new OnChildClickListener()
		{	
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
			{
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
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {	
			case LOCATION_STATE_RC:
			{
				if (resultCode == Activity.RESULT_OK && data != null) 
				{
					Engine.get().updateLocationState(mLoc, LocationState.values()[data.getIntExtra(MarkingDialogFragment.LOCATION_STATE, 0)]);
				}
				break;
			}
			case PHOTO_FROM_GALLERY:
			{
				if (resultCode == Activity.RESULT_OK)
				{
					Uri vUri = data.getData();
					manageUri(vUri);
				}
				break;
			}
			case TAKE_PHOTO:
			{
				if (resultCode == Activity.RESULT_OK)
				{
					File vPhotoFile = new File(mPath);
					Uri vUri = Uri.fromFile(vPhotoFile);
					manageUri(vUri);
				}
				break;
			}
		}
	}
	
	//--------------------------------------

	@Override
	public void onStart() {
		populateFragment();
		super.onStart();
	}

	@Override
	public void newOffer(final String name, final String desc, final long expDate) {
		RequestParams vParams = new RequestParams();
		       
		vParams.put("Name", name);
		vParams.put("Desc", desc);
		vParams.put("LocationId", mLoc.getId());
		vParams.put("ExpDateMillis", expDate);

		ConnectionManager.obtain().post(Routes.OFFERS, vParams, new JsonResponse()
		{	
			@Override
			public void onResponse(boolean aResult, Object aResponse)
			{
				if(aResult)
				{
					Toast.makeText(getActivity(), getResources().getString(R.string.edit_offer_inserted), Toast.LENGTH_SHORT).show();
					int id = Integer.parseInt((String)aResponse);

					Offer vOffer = new Offer(id, name, mLoc.getId(), mLoc.getName(), desc, 0, expDate);
					getActivity().getContentResolver().insert(DataOffersContentProvider.OFFERS_URI, vOffer.getContentValues());
				}
			}
		});
	}

	@Override
	public void changeName(final String newName) {
		RequestParams vParams = new RequestParams();
		vParams.put("Name", newName);
		
		vParams.put("PCat", mLoc.getPCat());
		vParams.put("SCat", mLoc.getSCat());
		vParams.put("Lat", mLoc.getLat());
		vParams.put("Lng", mLoc.getLng());
		vParams.put("Desc", mLoc.getDesc());
		getActivity().setTitle(newName);
		
		ConnectionManager.obtain().put(Routes.LOCATIONS + "/" + mLoc.getId(), vParams, new JsonResponse()
		{	
			@Override
			public void onResponse(boolean aResult, Object aResponse)
			{
				if(aResult)
				{
					ContentValues cv = new ContentValues();
					cv.put("Name", newName);
					getActivity().getContentResolver().update(DataFavContentProvider.MYLOC_URI, cv, "_ID = " + mLoc.getId(), null);
					getArguments().putString(LocationModel.NAME, newName);
				}
			}
		});
	}

	@Override
	public void changeDesc(final String newDesc) {
		RequestParams vParams = new RequestParams();
		vParams.put("Desc", newDesc);
		
		vParams.put("Name", mLoc.getName());
		vParams.put("PCat", mLoc.getPCat());
		vParams.put("SCat", mLoc.getSCat());
		vParams.put("Lat", mLoc.getLat());
		vParams.put("Lng", mLoc.getLng());
		
		mDesc.setText(newDesc);
		
		ConnectionManager.obtain().put(Routes.LOCATIONS + "/" + mLoc.getId(), vParams, new JsonResponse()
		{	
			@Override
			public void onResponse(boolean aResult, Object aResponse)
			{
				if(aResult)
				{
					ContentValues cv = new ContentValues();
					cv.put("Desc", newDesc);
					getActivity().getContentResolver().update(DataFavContentProvider.MYLOC_URI, cv, "_ID = " + mLoc.getId(), null);
					getArguments().putString(LocationModel.DESC, newDesc);
				}
			}
		});
	}

	public void deletePhoto(final int id) {
		ConnectionManager.obtain().delete(Routes.PHOTOS + id, new JsonResponse(){

			@Override
			public void onResponse(boolean aResult, Object aResponse) {
				if (aResult)
					removeThumb(id);
			}});
	}
}
