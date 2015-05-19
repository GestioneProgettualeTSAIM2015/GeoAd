package it.itskennedy.tsaim.geoad.core.argumentedreality;

public class ArgumentedRealityLocation 
{
	public final double mDistance;
	public final double mAngleDifference;
	public final String mName;
	public final int mId;
	
	public ArgumentedRealityLocation(int aId, String aName, double aDistance, double aAngleDiff)
	{
		mId = aId;
		mName = aName;
		mDistance = aDistance;
		mAngleDifference = aAngleDiff;
	}
}
