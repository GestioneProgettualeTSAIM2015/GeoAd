package it.itskennedy.tsaim.geoad.customview;

import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class CheckBoxList
{
	LinearLayout mLayout;
	Map<String, Object> mList;
	
	public CheckBoxList(LinearLayout aLayout)
	{
		mLayout = aLayout;
	}
	
	public void createCheckBoxList(Map<String, Object> aMap)
	{
		mList = aMap;

	    Iterator it = mList.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, Object> pair = (Map.Entry<String, Object>)it.next();
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	}
	
	
	class CustomCheckBox extends CheckBox
	{
		private boolean isChildrenChecked = false;

		public CustomCheckBox(Context context)
		{
			super(context);
		}

		@Override
		public void toggle()
		{
			if(isChildrenChecked)
			{
				super.toggle();
			}
		}		
	}

}
