package it.itskennedy.tsaim.geoad.fragments;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.entity.LocationModel;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomListAdapter extends BaseAdapter
{

	private List<LocationModel> mLocationList;
	private Context mContext;

	public CustomListAdapter(Context aContext, List<LocationModel> aLocationList)
	{
		mContext = aContext;
		mLocationList = aLocationList;
	}

	@Override
	public int getCount()
	{
		return mLocationList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return mLocationList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LocationModel location = mLocationList.get(position);
		ViewHolder viewHolder;
		if (convertView == null)
		{
			viewHolder = new ViewHolder();
			LayoutInflater layoutInflater = LayoutInflater.from(mContext);
			convertView = layoutInflater.inflate(R.layout.list_item, null, false);

			viewHolder.mName = (TextView) convertView.findViewById(R.id.textViewName);
			viewHolder.mPrimaryCategory = (TextView) convertView.findViewById(R.id.textViewFirstCategory);
			viewHolder.mSecondaryCategory = (TextView) convertView.findViewById(R.id.textViewSecondaCategory);

			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.mName.setText(location.getName());
		viewHolder.mPrimaryCategory.setText("Primaria: " + location.getmPCat());
		viewHolder.mSecondaryCategory.setText(location.getmSCat() != null ? "Secondaria: " + location.getmSCat() : "");
		return convertView;
	}

	private class ViewHolder
	{
		TextView mName;
		TextView mPrimaryCategory;
		TextView mSecondaryCategory;
	}

}
