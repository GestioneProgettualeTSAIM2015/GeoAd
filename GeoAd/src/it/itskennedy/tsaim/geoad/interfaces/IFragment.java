package it.itskennedy.tsaim.geoad.interfaces;

import android.os.Bundle;

public interface IFragment
{
	void loadFragment(int fragmentType, Bundle bundle);
	
	void toggleActionMenu(int... options);
	
	void checkAuth();
}
