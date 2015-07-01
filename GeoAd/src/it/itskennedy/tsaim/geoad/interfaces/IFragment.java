package it.itskennedy.tsaim.geoad.interfaces;

import android.app.Fragment;
import android.os.Bundle;

public interface IFragment
{
	void setILocationsList(ILocationsList f);
	
	void loadFragment(int fragmentType, Bundle bundle, Fragment target);
	
	void toggleActionMenu(int... options);
	
	void checkAuth();
}
