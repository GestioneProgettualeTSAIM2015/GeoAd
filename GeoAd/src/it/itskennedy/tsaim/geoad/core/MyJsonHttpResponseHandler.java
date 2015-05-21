package it.itskennedy.tsaim.geoad.core;

import it.itskennedy.tsaim.geoad.core.ConnectionManager.JsonResponse;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;

public class MyJsonHttpResponseHandler extends JsonHttpResponseHandler
{
	private JsonResponse mListener;
	private ConnectionManager mManager;

	public MyJsonHttpResponseHandler(JsonResponse aListener, ConnectionManager connectionManager)
	{
		mListener = aListener;
		mManager = connectionManager;
	}
	
	@Override
	public void onSuccess(int statusCode, Header[] headers,	JSONObject response)
    {
    	if(mListener != null)
        {
            mListener.onResponse(true, response);
        }

        mManager.executeQueue();
	}

	@Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response)
    {
        if(mListener != null)
        {
            mListener.onResponse(true, response);
        }

        mManager.executeQueue();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse)
    {
    	if(mListener != null)
    	{
    		mListener.onResponse(false, null);
    	}
    	
    	mManager.executeQueue();
    }
    
    public void onFailure(int statusCode, Header[] headers,	String responseString, Throwable throwable)
	{
		if(mListener != null && statusCode == 200)
		{
			mListener.onResponse(true, null);
		}
		else if(mListener != null)
		{
			mListener.onResponse(false, null);
		}
		
		mManager.executeQueue();
	}
}
