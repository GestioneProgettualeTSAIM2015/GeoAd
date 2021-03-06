package it.itskennedy.tsaim.geoad.fragments.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.Utils;
import it.itskennedy.tsaim.geoad.core.ConnectionManager;
import it.itskennedy.tsaim.geoad.core.ConnectionManager.JsonResponse;
import it.itskennedy.tsaim.geoad.customview.MultiSelectSpinner;
import it.itskennedy.tsaim.geoad.interfaces.IFilterDialogFragment;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class FilterDialogFragment extends DialogFragment
{
	private IFilterDialogFragment mListener;

	MultiSelectSpinner primarySpinner;
	MultiSelectSpinner secondarySpinner;
	SeekBar distanceBar;
	EditText nameFilter;
	RadioGroup mRadioGroup;
	
	private Map<String, ArrayList<String>> mAllCategory;
	private ArrayList<String> mPrimaryCategory;
	private ArrayList<String> mSecondaryCategory;
	
	CheckBox mCategoryCheckBox;
	CheckBox mDistanceCheckBox;
	CheckBox mNameCheckBox;
	CheckBox mTypeCheckBox;
	
	public static FilterDialogFragment getNewInstance() {
		FilterDialogFragment filterDialogFragment = new FilterDialogFragment();
		return filterDialogFragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder createFilterDialog = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.fragment_dialog_filter, null);

		final LinearLayout vCategoryLayout = (LinearLayout) view.findViewById(R.id.layoutCategory);
		primarySpinner = (MultiSelectSpinner) view.findViewById(R.id.spinnerPrimaria);
		primarySpinner.setOnMultiSelectSpinnerDismissListener(new MultiSelectSpinner.OnMultiSelectSpinnerDismissListener()
		{
			
			@Override
			public void onDismiss(List<String> selectedStrings)
			{
				setSecondaryCategory(mAllCategory, selectedStrings);
			}
		});
		primarySpinner.setEnabled(false);
		secondarySpinner = (MultiSelectSpinner) view.findViewById(R.id.spinnerSecondaria);
		mCategoryCheckBox = (CheckBox) view.findViewById(R.id.checkBoxCategory);
		mCategoryCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if (isChecked)
				{
					vCategoryLayout.setVisibility(View.VISIBLE);
				}
				else
				{
					vCategoryLayout.setVisibility(View.GONE);
				}
			}
		});
		secondarySpinner.setEnabled(false);
		
		setAllCategory(mListener.getJsonCategoriesString());
		
		distanceBar = (SeekBar) view.findViewById(R.id.seekBar);
		distanceBar.setProgress(8);
		distanceBar.incrementProgressBy(1);
		distanceBar.setMax(15);
		final TextView seekBarValue = (TextView)view.findViewById(R.id.seekbarValue);
		seekBarValue.setText("8");
		distanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
		    @Override
		    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		    	String vProgress = progress == 0 ? "0.5" : String.valueOf(progress);
		        seekBarValue.setText(vProgress);
		    }
		    @Override
		    public void onStartTrackingTouch(SeekBar seekBar) {
		    }
		    @Override
		    public void onStopTrackingTouch(SeekBar seekBar) {
		    }
		});
		final LinearLayout vDistanceLayout = (LinearLayout) view.findViewById(R.id.layoutDistance);
		mDistanceCheckBox = (CheckBox) view.findViewById(R.id.checkBoxDistance);
		mDistanceCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if (isChecked)
				{
					vDistanceLayout.setVisibility(View.VISIBLE);
				}
				else
				{
					vDistanceLayout.setVisibility(View.GONE);
				}
			}
		});

		nameFilter = (EditText)view.findViewById(R.id.editText);
		final LinearLayout vNameLayout = (LinearLayout) view.findViewById(R.id.layoutName);
		mNameCheckBox = (CheckBox) view.findViewById(R.id.checkBoxName);
		mNameCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if (isChecked)
				{
					vNameLayout.setVisibility(View.VISIBLE);
				}
				else
				{
					vNameLayout.setVisibility(View.GONE);
				}
			}
		});
		
		mRadioGroup = (RadioGroup) view.findViewById(R.id.layoutRadioGroupType);
		mTypeCheckBox = (CheckBox) view.findViewById(R.id.checkBoxType);
		mTypeCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if (isChecked)
				{
					mRadioGroup.setVisibility(View.VISIBLE);
				}
				else
				{
					mRadioGroup.setVisibility(View.GONE);
				}
			}
		});

		createFilterDialog.setView(view)
		.setPositiveButton(R.string.filter, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int id)
			{
				FilterDialogFragment.this.setCancelable(false);
				Bundle vBundle = new Bundle();
				
				vBundle.putString(Utils.RADIUS, String.valueOf(distanceBar.getProgress() == 0 ? "0.5" : distanceBar.getProgress()));
				
				if (mCategoryCheckBox.isChecked()) {
					ArrayList<String> vPrimaryCategory = new ArrayList<>(primarySpinner.getSelectedStrings());
					ArrayList<String> vSecondaryCategory = new ArrayList<>(secondarySpinner.getSelectedStrings());
					
					if (vSecondaryCategory.size() > 0)
						vBundle.putStringArrayList(Utils.SECONDARY_CATEGORY, vSecondaryCategory);
					else if (vPrimaryCategory.size() > 0)
						vBundle.putStringArrayList(Utils.PRIMARY_CATEGORY, vPrimaryCategory);
				}
				
				String name = nameFilter.getText().toString();
				if (mNameCheckBox.isChecked() && name.length() > 0)
					vBundle.putString(Utils.NAME, name);
				
				if(mTypeCheckBox.isChecked())
					switch (mRadioGroup.getCheckedRadioButtonId())
					{
						case R.id.radioButtonCA:
							vBundle.putString(Utils.TYPE, Utils.LOC_TYPE_CA);
							break;
						case R.id.radioButtonPOI:
							vBundle.putString(Utils.TYPE, Utils.LOC_TYPE_POI);
							break;
					}
				
				
				mListener.onFilterSave(vBundle);
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int id)
			{
				dismiss();
			}
		}).setNeutralButton(R.string.reset, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				mListener.onFilterReset();
			}
		}).setCancelable(false);

		return createFilterDialog.create();
	}

	@Override
	public void onResume()
	{
		Map<String, Object> filtersMap = mListener.getFiltersMap();
		String xx = mListener.getJsonCategoriesString();
		setAllCategory(mListener.getJsonCategoriesString());
		if (filtersMap != null)
		{
			if (filtersMap.containsKey(Utils.PRIMARY_CATEGORY))
			{
				ArrayList<String> vPrimary = (ArrayList<String>) filtersMap.get(Utils.PRIMARY_CATEGORY);
				
				primarySpinner.setSelection(vPrimary);
				primarySpinner.setEnabled(true);
				mCategoryCheckBox.setChecked(true);
			}
			if (filtersMap.containsKey(Utils.SECONDARY_CATEGORY))
			{
				ArrayList<String> vSecondary = (ArrayList<String>) filtersMap.get(Utils.SECONDARY_CATEGORY);
				secondarySpinner.setSelection(vSecondary);
				secondarySpinner.setEnabled(true);
				mCategoryCheckBox.setChecked(true);
			}
			if (filtersMap.containsKey(Utils.RADIUS))
			{
				double vRadius = Double.valueOf(filtersMap.get(Utils.RADIUS).toString());
				distanceBar.setProgress((int)vRadius);
				mDistanceCheckBox.setChecked(true);
			}
			if (filtersMap.containsKey(Utils.NAME))
			{
				String vName = filtersMap.get(Utils.NAME).toString();
				nameFilter.setText(vName);
				mNameCheckBox.setChecked(true);
			}
			if (filtersMap.containsKey(Utils.TYPE))
			{
				String vType = filtersMap.get(Utils.TYPE).toString();
				int vId = 0;
				switch (vType)
				{
					case Utils.LOC_TYPE_CA:
						vId = R.id.radioButtonCA;
						break;
					case Utils.LOC_TYPE_POI:
						vId = R.id.radioButtonPOI;
						break;
				}
				mRadioGroup.check(vId);
				mTypeCheckBox.setChecked(true);
			}
		}
		super.onResume();
	}

	private void setPrimaryCategory(Map<String, ArrayList<String>> aAllCategory)
	{
		mPrimaryCategory = new ArrayList<String>(aAllCategory.keySet());
		primarySpinner.setItems(mPrimaryCategory);
		primarySpinner.setEnabled(true);
	}

	private void setSecondaryCategory(Map<String, ArrayList<String>> aAllCategory, List<String> primaryCategory)
	{
		ArrayList<String> secondaryCategory = new ArrayList<String>();
		for (Map.Entry<String, ArrayList<String>> pair : aAllCategory.entrySet())
		{
			if (primaryCategory == null || primaryCategory.size() == 0)
			{
				for (String s : pair.getValue())
				{
					secondaryCategory.add(s);
				}
			}
			else
			{
				if (primaryCategory.contains(pair.getKey()))
				{
					for (String s : pair.getValue())
					{
						secondaryCategory.add(s);
					}
				}
			}
		}
		mSecondaryCategory = secondaryCategory;
		secondarySpinner.setItems(mSecondaryCategory);
		secondarySpinner.setEnabled(true);
	}

	private void setAllCategory(String aCategoryJson)
	{
		if (aCategoryJson == null) return;
		
		final Map<String, ArrayList<String>> categoryMap = new HashMap<String, ArrayList<String>>();
		JSONObject categoryJson = null;
		try
		{
			categoryJson = new JSONObject(aCategoryJson);
		}
		catch (JSONException e1)
		{
			Log.d("parsing_error", aCategoryJson);
			e1.printStackTrace();
			return;
		}
		
		Iterator<String> iter = categoryJson.keys();
		while (iter.hasNext())
		{
			String key = iter.next();
			try
			{
				ArrayList<String> listdata = new ArrayList<String>();
				JSONArray jArray = categoryJson.getJSONArray(key);
				if (jArray != null)
				{
					for (int i = 0; i < jArray.length(); i++)
					{
						listdata.add(jArray.get(i).toString());
					}
				}
				categoryMap.put(key, listdata);
			}
			catch (JSONException e)
			{
				Log.e("JSON", e.getMessage());
			}
		}
		mAllCategory = categoryMap;
		setPrimaryCategory(mAllCategory);
		setSecondaryCategory(mAllCategory, null);
	}

	@Override
	public void onAttach(Activity activity)
	{
		if (activity instanceof IFilterDialogFragment) {
			mListener = (IFilterDialogFragment) activity;
		} else mListener = new IFilterDialogFragment() {
			public void onFilterSave(Bundle aFilter) {}
			public void onFilterReset() {}
			public String getJsonCategoriesString() { return null; }
			public Map<String, Object> getFiltersMap() { return null; }
		};
		
		super.onAttach(activity);
	}

	@Override
	public void onDetach()
	{
		mListener = null;
		super.onDetach();
	}
}
