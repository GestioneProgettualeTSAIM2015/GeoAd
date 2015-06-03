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
import android.widget.RelativeLayout;

public class AugmentedRealityActivity extends Activity
{
	private CameraController mCameraSurface;
	private AugmentedRealityManager mArgReality;
	private RelativeLayout preview;
	
	private float horizontalDegreesFOV;
	private float verticalDegreesFOV;
	
	
	private AugmentedRealityListener mArgReaListener = new AugmentedRealityListener()
    {		
		@Override
		public void onNewData(List<AugmentedRealityLocation> aToDraw, float aPitch, float aRoll)
		{
			System.out.println("NEW DATA");
			for (AugmentedRealityLocation arl : aToDraw) {
				ImageView currentMarker = (ImageView) findViewById(arl.mId);
				if (Math.abs(arl.mAngleDifference) > horizontalDegreesFOV / 2) {
					if (currentMarker != null)  {
						preview.removeView(currentMarker);
						System.out.println("REMOVED " + arl.mAngleDifference);
						System.out.println("-------------");
					}
				} else {
					if (currentMarker == null) {
						ImageView markerImageView = new ImageView(getBaseContext());
						markerImageView.setId(arl.mId);
						markerImageView.setImageResource(drawable.star_big_on);
						RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(60,60);
						markerImageView.setLayoutParams(parms);
						
						currentMarker = markerImageView;
						
						preview.addView(markerImageView);
						markerImageView.bringToFront();
					}

					float oldX = currentMarker.getX();
					float oldY = currentMarker.getY();
					float newX = getXPosition(arl.mAngleDifference) - 30;
					float newY = getYPosition(aPitch) - 30;
					if (Math.abs(oldX - newX) > 50) currentMarker.setX(newX);
					if (Math.abs(oldY - newY) > 50) currentMarker.setY(newY);
//					currentMarker.setX(newX);
//					currentMarker.setY(newY);
					System.out.println("x pos: " + newX + " y pos: " + newY);
				}
			}

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
        preview = (RelativeLayout) findViewById(R.id.camerapreview);
//        preview.addView(mCameraSurface);
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
