package it.itskennedy.tsaim.geoad.activities;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.augmentedreality.AugmentedRealityLocation;
import it.itskennedy.tsaim.geoad.augmentedreality.AugmentedRealityManager;
import it.itskennedy.tsaim.geoad.augmentedreality.AugmentedRealityManager.AugmentedRealityListener;
import it.itskennedy.tsaim.geoad.augmentedreality.CameraController;

import java.util.List;

import android.R.drawable;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class AugmentedRealityActivity extends Activity
{
	private CameraController mCameraSurface;
	private AugmentedRealityManager mArgReality;
	private FrameLayout preview;
	
	private Context mContext = this;
	private float horizontalDegreesFOV;
	private float verticalDegreesFOV;
	
	
	private AugmentedRealityListener mArgReaListener = new AugmentedRealityListener()
    {		
		@Override
		public void onNewData(List<AugmentedRealityLocation> aToDraw, float aPitch, float aRoll)
		{
//			System.out.println("NEW DATA");
//			for (AugmentedRealityLocation arl : aToDraw) {
//				ImageView currentMarker = (ImageView) findViewById(arl.mId);
//				if (Math.abs(arl.mAngleDifference) > horizontalDegreesFOV / 2) {
//					if (currentMarker != null)  {
////						preview.removeView(currentMarker);
//						System.out.println("REMOVED " + arl.mAngleDifference);
//						System.out.println("-------------");
//					}
//				} else {
//					if (currentMarker == null) {
//						ImageView markerImageView = new ImageView(mContext);
//						markerImageView.setId(arl.mId);
//						markerImageView.setImageResource(drawable.star_big_on);
//						FrameLayout.LayoutParams parms = new FrameLayout.LayoutParams(60,60);
//						markerImageView.setLayoutParams(parms);
//						
//						currentMarker = markerImageView;
//						
//						preview.addView(markerImageView);
//						markerImageView.bringToFront();
//					}
//
//					currentMarker.setX(getXPosition(arl.mAngleDifference) - 30);
//					currentMarker.setY(getYPosition(aPitch) - 30);
//					System.out.println("x pos: " + getXPosition(arl.mAngleDifference) + " y pos: " + getYPosition(aPitch));
//				}
//			}

		}
		
		public float getXPosition(double angleDifference) {
			float x = (float) (angleDifference + horizontalDegreesFOV / 2);
			float ratio = preview.getWidth() / horizontalDegreesFOV;
			return x * ratio;
		}
		
		public float getYPosition(double pitch) {
			pitch = -pitch;
			float x = (float) (pitch + verticalDegreesFOV / 2);
			float ratio = preview.getHeight() / verticalDegreesFOV;
			return x * ratio;
		}

		@Override
		public void tooLowAccuracy() 
		{
			System.out.println("LOW ACCURACY");
		}

		@Override
		public void onUnreliableSensor()
		{
			System.out.println("UNRELIABLE SENSOR");
		}
	};

	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_augmented_reality);
        
        mArgReality = new AugmentedRealityManager(this);

        mCameraSurface = new CameraController(this);
        horizontalDegreesFOV = mCameraSurface.getHorizontalFov();
        verticalDegreesFOV = mCameraSurface.getVerticalFov();
        preview = (FrameLayout) findViewById(R.id.camerapreview);
        preview.addView(mCameraSurface);
    }

	@Override
	protected void onResume() 
	{
		mArgReality.onResume();
		mArgReality.setListener(mArgReaListener);
		super.onResume();
	}

	@Override
	protected void onPause() 
	{
		super.onPause();
		mArgReality.onPause();
		mArgReality.setListener(null);
		if(mCameraSurface != null)
		{
			mCameraSurface = null;	
		}
	}
}
