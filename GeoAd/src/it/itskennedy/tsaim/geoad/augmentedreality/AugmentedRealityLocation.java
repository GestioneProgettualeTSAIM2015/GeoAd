package it.itskennedy.tsaim.geoad.augmentedreality;

public class AugmentedRealityLocation 
{
	public final float mDistance;
	public final float mAngleDifference;
	public final String mName;
	public final int mId;
	
	public AugmentedRealityLocation(int aId, String aName, float aDistance, float aAngleDiff)
	{
		mId = aId;
		mName = aName;
		mDistance = aDistance;
		mAngleDifference = aAngleDiff;
	}
}
