package it.itskennedy.tsaim.geoad.interfaces;

import java.util.Map;

import android.os.Bundle;

public interface IFilterDialogFragment
{
	Map<String, Object> getFiltersMap();
	
	String getJsonCategoriesString();
	
	void onFilterSave(Bundle aFilter);

	void onFilterReset();
}
