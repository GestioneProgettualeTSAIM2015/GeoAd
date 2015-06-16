package it.itskennedy.tsaim.geoad.fragment;

import it.itskennedy.tsaim.geoad.entity.LocationModel;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CustomListAdapter extends BaseAdapter
{
	
	private List<LocationModel> mLocationList;

	public CustomListAdapter(List<LocationModel> locationList)
	{
		mLocationList = locationList;
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
