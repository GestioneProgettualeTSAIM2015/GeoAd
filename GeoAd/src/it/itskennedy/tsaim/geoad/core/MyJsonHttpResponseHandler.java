package it.itskennedy.tsaim.geoad.core;

import it.itskennedy.tsaim.geoad.core.ConnectionManager.JsonResponse;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

public class MyJsonHttpResponseHandler extends JsonHttpResponseHandler
{
	private JsonResponse mListener;

	public MyJsonHttpResponseHandler(JsonResponse aListener)
	{
		mListener = aListener;
	}
	
	@Override
	public void onSuccess(int statusCode, Header[] headers,	JSONObject response)
    {
    	if(mListener != null)
        {
            mListener.onResponse(true, response);
        }
	}

	@Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response)
    {
        if(mListener != null)
        {
            mListener.onResponse(true, response);
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse)
    {
    	if(mListener != null)
    	{
    		mListener.onResponse(false, errorResponse);
    		Log.d("http_failed", throwable.getMessage() + " >>>>>>>>>> " + errorResponse);
    	}
    }
    
    @Override
    public void onFailure(int statusCode, Header[] headers,	String responseString, Throwable throwable)
	{
		if(mListener != null && statusCode == 200)
		{
			mListener.onResponse(true, responseString);
		}
		else if(mListener != null)
		{
			mListener.onResponse(false, responseString);
			Log.d("http_failed", throwable.getMessage() + " >>>>>>>>>> " + responseString);
		}
	}

	@Override
	public void onFailure(int statusCode, Header[] headers,	Throwable throwable, JSONArray errorResponse) 
	{
		if(mListener != null && statusCode == 200)
		{
			mListener.onResponse(true, errorResponse);
		}
		else if(mListener != null)
		{
			mListener.onResponse(false, errorResponse);
			Log.d("http_failed", throwable.getMessage() + " >>>>>>>>>> " + errorResponse);
		}
	}
}
