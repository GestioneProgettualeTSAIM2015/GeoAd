package it.itskennedy.tsaim.geoad;

import it.itskennedy.tsaim.geoad.entity.Offer;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class OfferExpandableListAdapter extends BaseExpandableListAdapter 
{
	private Context mContext;
	private List<ArrayList<OfferDetail>> mContents = new ArrayList<ArrayList<OfferDetail>>();
	
	public OfferExpandableListAdapter(Context context, List<Offer> vList)
	{
		super();
		
		for(int i = 0; i < vList.size(); ++i)
		{
			mContents.add(new ArrayList<OfferDetail>());
			mContents.get(i).add(new OfferDetail(vList.get(i).getId(), vList.get(i).getName(), vList.get(i).getDesc(), vList.get(i).getExpTime()));
		}
		
		mContext = context;
	}

	@Override
	public OfferDetail getChild(int groupPosition, int childPosition) 
	{
		return mContents.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		return mContents.get(groupPosition).get(childPosition).mId;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
	{
		if (convertView == null) 
		{
	        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        convertView = inflater.inflate(R.layout.child_offer_row, null);
	    }

	    TextView vDesc = (TextView) convertView.findViewById(R.id.textViewChildDesc);
	    vDesc.setText(mContents.get(groupPosition).get(childPosition).mDesc);

	    TextView vExp = (TextView) convertView.findViewById(R.id.textViewChildExp);
	    vExp.setText(mContext.getString(R.string.expire) + ": " + mContents.get(groupPosition).get(childPosition).mExp);
	    
	    return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{
		return mContents.get(groupPosition).size();
	}

	@Override
	public List<OfferDetail> getGroup(int groupPosition) 
	{
		return mContents.get(groupPosition);
	}

	@Override
	public int getGroupCount() 
	{
		return mContents.size();
	}

	@Override
	public long getGroupId(int groupPosition)
	{
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,	View convertView, ViewGroup parent) 
	{
		if (convertView == null) 
		{
	        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        convertView = inflater.inflate(R.layout.offer_view_row, null);
	    }

	    TextView vTitle = (TextView) convertView.findViewById(R.id.textViewGroupRowTitle);
	    vTitle.setText(mContents.get(groupPosition).get(0).mName);

	    return convertView;
	}

	@Override
	public boolean hasStableIds()
	{
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) 
	{	
		return true;
	}
	
	public int getFavourite()
	{
		return mContents.get(0).size();
	}
	
	public int getIgnored()
	{
		return mContents.get(1).size();
	}
	
	public void remove(int aGroup, int aId)
	{
		List<OfferDetail> vElem = mContents.get(aGroup);
		for(int i = 0; i < vElem.size(); ++i)
		{
			if(vElem.get(i).mId == aId)
			{
				vElem.remove(i);			
				return;
			}
		}
	}
	
	class OfferDetail
	{
		public final String mDesc;
		public final String mName;
		public final String mExp;
		public final int mId;
		
		public OfferDetail(int aId, String aName, String aDesc, String aExp)
		{
			mName = aName;
			mId = aId;
			mDesc = aDesc;
			mExp = aExp;
		}
	}
}