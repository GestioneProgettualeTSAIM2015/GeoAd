package it.itskennedy.tsaim.geoad;

import it.itskennedy.tsaim.geoad.localdb.MyLocationHelper;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyLocationAdapter extends CursorAdapter
{
	public MyLocationAdapter(Context aContext) 
	{
		super(aContext, null, 0);
	}

	private class ViewHolder 
	{
		public TextView mName;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View newView(Context aContext, Cursor aCursor, ViewGroup aParent)
	{
		LayoutInflater inflater = LayoutInflater.from(aContext);
		View vRowView = inflater.inflate(R.layout.my_loc_row, null);
		ViewHolder vViewHolder = new ViewHolder();
		
		vViewHolder.mName = (TextView) vRowView.findViewById(R.id.textViewName);
		
		vRowView.setTag(vViewHolder);
		
		return vRowView;
	}
	
	@Override
	public void bindView(View aView, Context aContext, Cursor aCursor)
	{
		int vNameIndex = aCursor.getColumnIndex(MyLocationHelper.NAME);
		String vName = aCursor.getString(vNameIndex);
		
		ViewHolder vViewHolder = (ViewHolder) aView.getTag();
		
		vViewHolder.mName.setText(vName);
	}
}
