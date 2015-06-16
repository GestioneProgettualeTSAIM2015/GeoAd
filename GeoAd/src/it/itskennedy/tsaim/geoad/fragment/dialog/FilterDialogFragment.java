package it.itskennedy.tsaim.geoad.fragment.dialog;

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
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class FilterDialogFragment extends DialogFragment
{
	private IFilterDialogFragment mListener;

	MultiSelectSpinner primarySpinner;
	MultiSelectSpinner secondarySpinner;

	private Map<String, ArrayList<String>> mAllCategory;
	private ArrayList<String> mPrimaryCategory;
	private ArrayList<String> mSecondaryCategory;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder createProjectAlert = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.fragment_dialog_filter, null);

		setAllCategory();

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
		secondarySpinner = (MultiSelectSpinner) view.findViewById(R.id.spinnerSecondaria);
		final CheckBox vCategoryCheckBox = (CheckBox) view.findViewById(R.id.checkBoxCategory);
		vCategoryCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
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

		final LinearLayout vDistanceLayout = (LinearLayout) view.findViewById(R.id.layoutDistance);
		final CheckBox vDistanceCheckBox = (CheckBox) view.findViewById(R.id.checkBoxDistance);
		vDistanceCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
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

		final LinearLayout vNameLayout = (LinearLayout) view.findViewById(R.id.layoutName);
		final CheckBox vNameCheckBox = (CheckBox) view.findViewById(R.id.checkBoxName);
		vNameCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
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

		createProjectAlert.setView(view)
		.setPositiveButton("Filtra", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int id)
			{
				Bundle vBundle = new Bundle();
				vBundle.putStringArrayList(Utils.PRIMARY_CATEGORY, (ArrayList<String>)primarySpinner.getSelectedStrings());
				vBundle.putStringArrayList(Utils.SECONDARY_CATEGORY, (ArrayList<String>)secondarySpinner.getSelectedStrings());
				mListener.onFilterSave(vBundle);
			}
		}).setNegativeButton("Annulla", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int id)
			{
				dismiss();
			}
		}).setNeutralButton("Reset", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				mListener.onFilterReset();
			}
		}).setCancelable(false);

		return createProjectAlert.create();
	}

	private void setPrimaryCategory(Map<String, ArrayList<String>> aAllCategory)
	{
		mPrimaryCategory = new ArrayList<String>(aAllCategory.keySet());
		primarySpinner.setItems(mPrimaryCategory);
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
		
	}

	private void setAllCategory()
	{
		final Map<String, ArrayList<String>> categoryMap = new HashMap<String, ArrayList<String>>();
		Log.i("GET", "chiamata get");
		ConnectionManager.obtain().get("http://geoad.somee.com/api/categories", null, new JsonResponse()
		{
			@Override
			public void onResponse(boolean aResult, Object aResponse)
			{
				Log.i("GET", "risposta get");
				if (aResult)
				{
					JSONObject categoryJson = (JSONObject) aResponse;

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
					// for (Map.Entry<String, ArrayList<String>> pair :
					// categoryMap.entrySet()) {
					// Log.d("map", pair.getKey());
					// for (String s : pair.getValue())
					// Log.d("map", "- " + s);
					// }
				}
			}
		});
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		if (activity instanceof IFilterDialogFragment)
		{
			mListener = (IFilterDialogFragment) activity;
		}
	}
}
