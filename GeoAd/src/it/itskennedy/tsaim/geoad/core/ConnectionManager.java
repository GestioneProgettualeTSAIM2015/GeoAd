package it.itskennedy.tsaim.geoad.core;

import java.util.ArrayList;

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

	private ArrayList<HttpMethod> mTypes;
    private ArrayList<String> mUrls;
    private ArrayList<RequestParams> mRequests;
    private ArrayList<JsonResponse> mListeners;

    private boolean mIsPending = false;
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

        mTypes = new ArrayList<HttpMethod>();
        mUrls = new ArrayList<String>();
        mRequests = new ArrayList<RequestParams>();
        mListeners = new ArrayList<JsonResponse>();
        
        updateConnectionState();
        
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
    
    @Override
	protected void finalize() throws Throwable 
	{
    	Engine.get().unregisterReceiver(this);
		super.finalize();
	}

	public void get(String aUrl, RequestParams vParams, JsonResponse jsonResponse)
    {
    	send(HttpMethod.GET, aUrl, vParams, jsonResponse);
    }
    
    public void post(String aUrl, RequestParams vParams, JsonResponse jsonResponse)
    {
    	send(HttpMethod.POST, aUrl, vParams, jsonResponse);
    }
    
    public void put(String aUrl, RequestParams vParams, JsonResponse jsonResponse)
    {
    	send(HttpMethod.PUT, aUrl, vParams, jsonResponse);
    }
    
    public void delete(String aUrl, JsonResponse jsonResponse)
    {
    	send(HttpMethod.DELETE, aUrl, null, jsonResponse);
    }

    private void send(HttpMethod aType, String aUrl, RequestParams aParams, final JsonResponse aListener)
    {
    	if(mIsConnection)
    	{
	    	String vKey = Engine.get().getKey();
    	setAuthHeader();
	    	
	    	if(vKey != null)
	    	{
	    		if(!mIsPending)
	            {	
	                mIsPending = true;
	                
	                if(aParams == null)
	                {
	                	aParams = new RequestParams();
	                }
	                
	                aParams.add("key", vKey);
	
	                Log.d(Engine.APP_NAME, "URL: " + Engine.SERVER_URL + aUrl);
	
	                JsonHttpResponseHandler vResponseHandler = new MyJsonHttpResponseHandler(aListener, this);
	                      
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
	                enqueue(aType, aUrl, aParams, aListener);
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

    void executeQueue()
    {
        mIsPending = false;

        if(mRequests.size() > 0)
        {
        	HttpMethod vM = mTypes.remove(0);
        	String vU = mUrls.remove(0);
        	RequestParams vP = mRequests.remove(0);
        	JsonResponse vR = mListeners.remove(0);
        	
        	send(vM, vU, vP, vR);   
        }
    }

    private void enqueue(HttpMethod aType, String aUrl, RequestParams aParams, JsonResponse aListener)
    {
    	mTypes.add(aType);
        mUrls.add(aUrl);
        mRequests.add(aParams);
        mListeners.add(aListener);
    }

	@Override
	public void onReceive(Context context, Intent intent)
	{
		updateConnectionState();
	}
}
