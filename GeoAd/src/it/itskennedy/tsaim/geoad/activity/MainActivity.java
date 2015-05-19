package it.itskennedy.tsaim.geoad.activity;

import it.itskennedy.tsaim.geoad.R;
import it.itskennedy.tsaim.geoad.core.Engine;
import it.itskennedy.tsaim.geoad.core.argumentedreality.ArgumentedRealityLocation;
import it.itskennedy.tsaim.geoad.core.argumentedreality.ArgumentedRealityManager;
import it.itskennedy.tsaim.geoad.core.argumentedreality.ArgumentedRealityManager.ArgumentedRealityListener;
import it.itskennedy.tsaim.geoad.core.argumentedreality.CameraController;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends Activity
{
	private CameraController mCameraSurface;
	private ArgumentedRealityManager mArgReality;
	private TextView mText;
	
	private ArgumentedRealityListener mArgReaListener = new ArgumentedRealityListener()
    {		
		@Override
		public void onNewData(/*List<ArgumentedRealityLocation> aToDraw*/int aDir)
		{
			if(mCameraSurface != null)
			{
				mText.setText("" + aDir);
				mText.setTextColor(Color.GREEN);
				mCameraSurface.startPreview();	
			}
			
			Log.d(Engine.APP_NAME, "new data");
		}
		
		@Override
		public void deviceNotPointed()
		{
			if(mCameraSurface != null)
			{
				mText.setText("PUNTA IL CELL");
				mText.setTextColor(Color.RED);
				mCameraSurface.stopPreview();	
			}
			
			Log.d(Engine.APP_NAME, "not pointed");
		}

		@Override
		public void tooLowAccuracy() 
		{
			mText.setText("ACCURACY");
			mText.setTextColor(Color.RED);
		}
	};

	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mArgReality = new ArgumentedRealityManager(this);
        
        mText = (TextView) findViewById(R.id.textView1);
        
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
	protected void onStop() 
	{
		super.onStop();
		mArgReality.onPause();
		mArgReality.setListener(null);
		if(mCameraSurface != null)
		{
			mCameraSurface.release();
			mCameraSurface = null;	
		}
	}
}
