package it.itskennedy.tsaim.geoad;

import java.util.ArrayList;
import java.util.List;

import it.itskennedy.tsaim.geoad.localdb.DataFavContentProvider;
import it.itskennedy.tsaim.geoad.localdb.FavoritesHelper;
import it.itskennedy.tsaim.geoad.localdb.IgnoredHelper;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MarkedExpandableListAdapter extends BaseExpandableListAdapter 
{
	private static final String[] TITLES = new String[2];
	private static final int[] ICONS = new int[] { R.drawable.fav, R.drawable.ign };
	
	private Context mContext;
	private List<ArrayList<ElementHolder>> mContents = new ArrayList<ArrayList<ElementHolder>>();
	
	public MarkedExpandableListAdapter(Context context)
	{
		super();
		
		TITLES[0] = context.getString(R.string.favorite_loc);
		TITLES[1] = context.getString(R.string.ignored_loc);

		mContents.add(new ArrayList<ElementHolder>());
		mContents.add(new ArrayList<ElementHolder>());
		
		Cursor vFav = context.getContentResolver().query(DataFavContentProvider.FAVORITES_URI, null, null, null, null);
		
		int vIdIndex = vFav.getColumnIndex(FavoritesHelper._ID);
		int vNameIndex = vFav.getColumnIndex(FavoritesHelper.NAME);
		
		while(vFav.moveToNext())
		{
			int vId = vFav.getInt(vIdIndex);
			String vName = vFav.getString(vNameIndex);
			
			mContents.get(0).add(new ElementHolder(vId, vName));
		}
		
		vFav.close();
		
		Cursor vIgn = context.getContentResolver().query(DataFavContentProvider.IGNORED_URI, null, null, null, null);
		
		vIdIndex = vIgn.getColumnIndex(IgnoredHelper._ID);
		vNameIndex = vIgn.getColumnIndex(IgnoredHelper.COLUMN_NAME);
		
		while(vIgn.moveToNext())
		{
			int vId = vIgn.getInt(vIdIndex);
			String vName = vIgn.getString(vNameIndex);
			
			mContents.get(1).add(new ElementHolder(vId, vName));
		}
		
		vIgn.close();
		
		mContext = context;
	}

	@Override
	public ElementHolder getChild(int groupPosition, int childPosition) 
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
	        convertView = inflater.inflate(R.layout.child_view_row, null);
	    }

	    TextView vTitle = (TextView) convertView.findViewById(R.id.textViewChildName);
	    vTitle.setText(mContents.get(groupPosition).get(childPosition).mName);

	    return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{
		return mContents.get(groupPosition).size();
	}

	@Override
	public List<ElementHolder> getGroup(int groupPosition) 
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
	        convertView = inflater.inflate(R.layout.group_view_row, null);
	    }

	    TextView vTitle = (TextView) convertView.findViewById(R.id.textViewGroupRowTitle);
	    ImageView vIcon = (ImageView) convertView.findViewById(R.id.imageViewGroupRow);
	    vTitle.setText(TITLES[groupPosition]);
	    vIcon.setImageResource(ICONS[groupPosition]);

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
		List<ElementHolder> vElem = mContents.get(aGroup);
		for(int i = 0; i < vElem.size(); ++i)
		{
			if(vElem.get(i).mId == aId)
			{
				vElem.remove(i);			
				return;
			}
		}
	}

	
	class ElementHolder
	{
		public final int mId;
		public final String mName;
		
		public ElementHolder(int aId, String aName)
		{
			mId = aId;
			mName = aName;
		}
	}
	}