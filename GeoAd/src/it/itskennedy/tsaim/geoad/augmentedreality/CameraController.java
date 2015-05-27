package it.itskennedy.tsaim.geoad.augmentedreality;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressWarnings({ "deprecation" })
public class CameraController extends SurfaceView implements SurfaceHolder.Callback 
{
	private Camera mCamera;
	private SurfaceHolder mHolder;
	
	public CameraController(Context context) 
	{
		super(context, null);
		mCamera = Camera.open();
		mHolder = getHolder();
	    mHolder.addCallback(this);
	}

	public void surfaceCreated(SurfaceHolder holder)
	{
		try 
		{
			mCamera.setPreviewDisplay(holder);
		}
		catch (IOException e)
		{
			
		}
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
	{
	    mCamera.startPreview();
	}
	   
	public void surfaceDestroyed(SurfaceHolder arg0)
	{
		if (mCamera != null)
		{
	        mCamera.stopPreview();
	        mCamera.release();
	    }
	}
	
	public float getHorizontalFov() {
		return mCamera.getParameters().getHorizontalViewAngle();
	}
	
	public float getVerticalFov() {
		return mCamera.getParameters().getVerticalViewAngle();
	}
	
}