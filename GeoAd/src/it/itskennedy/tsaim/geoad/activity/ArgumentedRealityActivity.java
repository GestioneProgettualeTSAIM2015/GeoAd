package it.itskennedy.tsaim.geoad.activity;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.core.argumentedreality.ArgumentedRealityLocation;
import it.itskennedy.tsaim.geoad.core.argumentedreality.ArgumentedRealityManager;
import it.itskennedy.tsaim.geoad.core.argumentedreality.ArgumentedRealityManager.ArgumentedRealityListener;
import it.itskennedy.tsaim.geoad.core.argumentedreality.CameraController;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class ArgumentedRealityActivity extends Activity
{
	private CameraController mCameraSurface;
	private ArgumentedRealityManager mArgReality;
	
	private ArgumentedRealityListener mArgReaListener = new ArgumentedRealityListener()
    {		
		@Override
		public void onNewData(List<ArgumentedRealityLocation> aToDraw, int aPitch, int aRoll)
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
        setContentView(R.layout.activity_arg_reality);
        
        mArgReality = new ArgumentedRealityManager(this);
        
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
