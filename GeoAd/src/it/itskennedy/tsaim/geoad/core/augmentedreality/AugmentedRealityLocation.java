package it.itskennedy.tsaim.geoad.core.augmentedreality;

public class AugmentedRealityLocation 
{
	public final double mDistance;
	public final double mAngleDifference;
	public final String mName;
	public final int mId;
	
	public AugmentedRealityLocation(int aId, String aName, double aDistance, double aAngleDiff)
	{
		mId = aId;
		mName = aName;
		mDistance = aDistance;
		mAngleDifference = aAngleDiff;
	}
}
