package it.itskennedy.tsaim.geoad.core;

import it.itskennedy.tsaim.geoad.core.ConnectionManager.JsonResponse;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Marco Zeni on 13/05/2015.
 */
public class ConnectionManager extends BroadcastReceiver
{
	private static ConnectionManager mInstance;
    private static AsyncHttpClient mClient;

    private boolean mIsConnection = true;

    private enum HttpMethod { GET, POST, PUT, DELETE };
    
    public interface JsonResponse
    {
        void onResponse(boolean aResult, Object aResponse);
    }

    public static ConnectionManager obtain()
    {
        if(mInstance == null)
        {
            mInstance = new ConnectionManager();
        }

        return mInstance;
    }
    
    public ConnectionManager()
    {
        mClient = new AsyncHttpClient();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);        
        Engine.get().registerReceiver(this, filter);
    }

	private void updateConnectionState() 
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) 
        		Engine.get().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        mIsConnection = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        
        Log.d(Engine.APP_NAME, "Connection State Change: " + mIsConnection);
	}

	public void getNoKey(String aUrl, RequestParams vParams, JsonResponse jsonResponse)
    {
    	send(HttpMethod.GET, aUrl, vParams, jsonResponse, false);
    }
	
	public void get(String aUrl, RequestParams vParams, JsonResponse jsonResponse)
    {
    	send(HttpMethod.GET, aUrl, vParams, jsonResponse, true);
    }
    
    public void post(String aUrl, RequestParams vParams, JsonResponse jsonResponse)
    {
    	send(HttpMethod.POST, aUrl, vParams, jsonResponse, true);
    }
    
    public void put(String aUrl, RequestParams vParams, JsonResponse jsonResponse)
    {
    	send(HttpMethod.PUT, aUrl, vParams, jsonResponse, true);
    }
    
    public void delete(String aUrl, JsonResponse jsonResponse)
    {
    	send(HttpMethod.DELETE, aUrl, null, jsonResponse, true);
    }

    private void send(HttpMethod aType, String aUrl, RequestParams aParams, final JsonResponse aListener, boolean aSendKey)
    {
    	if(mIsConnection)
    	{
	    	String vKey = Engine.get().getKey();
	    	setAuthHeader();
	    	
	    	if(vKey != null)
	    	{
                if(aParams == null)
                {
                	aParams = new RequestParams();
                }
                
                if(aSendKey)
                {
                	aParams.add("key", vKey);
                }

                Log.d(Engine.APP_NAME, "URL: " + Engine.SERVER_URL + aUrl);

                JsonHttpResponseHandler vResponseHandler = new MyJsonHttpResponseHandler(aListener);
                      
                switch(aType)
                {
					case DELETE:
					{					
						mClient.delete(Engine.SERVER_URL + aUrl, vResponseHandler);
						break;
					}
					case GET:
					{
						mClient.get(Engine.SERVER_URL + aUrl, aParams, vResponseHandler);
						break;
					}
					case POST:
					{
						mClient.post(Engine.SERVER_URL + aUrl, aParams, vResponseHandler);
						break;
					}
					case PUT:
					{
						mClient.put(Engine.SERVER_URL + aUrl, aParams, vResponseHandler);
						break;
					}
                }	            
	    	}
	    	else
	    	{
	    		if(aListener != null)
	    		{
	    			aListener.onResponse(false, null);
	    		}
	    	}
    	}
    }

	private void setAuthHeader() 
	{
		String vToken = Engine.get().getToken();
    	if(vToken != null)
    	{
    		mClient.addHeader("Authorization", "Bearer " + Engine.get().getToken());	
    	}
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		updateConnectionState();
	}
}
