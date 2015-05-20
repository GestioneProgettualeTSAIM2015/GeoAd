package it.itskennedy.tsaim.geoad.activity;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.core.augmentedreality.AugmentedRealityLocation;
import it.itskennedy.tsaim.geoad.core.augmentedreality.AugmentedRealityManager;
import it.itskennedy.tsaim.geoad.core.augmentedreality.CameraController;
import it.itskennedy.tsaim.geoad.core.augmentedreality.AugmentedRealityManager.ArgumentedRealityListener;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class AugmentedRealityActivity extends Activity
{
	private CameraController mCameraSurface;
	private AugmentedRealityManager mArgReality;
	
	private ArgumentedRealityListener mArgReaListener = new ArgumentedRealityListener()
    {		
		@Override
		public void onNewData(List<AugmentedRealityLocation> aToDraw, int aPitch, int aRoll)
		{
			
		}
		
		@Override
		public void deviceNotPointed()
		{
			
		}

		@Override
		public void tooLowAccuracy() 
		{
			
		}

		@Override
		public void onUnreliableSensor()
		{
			//TODO cadorin
			//sembra che la migliore soluzione quando il sensore impazzisce sia quella di
			//esporre un popup che invita l'utente a muovere il cell a 8
		}
	};

	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aug_reality);
        
        mArgReality = new AugmentedRealityManager(this);
        
        mCameraSurface = new CameraController(this);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camerapreview);
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
