package it.itskennedy.tsaim.geoad.core;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Marco Zeni on 13/05/2015.
 */
@SuppressWarnings("deprecation")
public class ConnectionManager
{
    private static ConnectionManager mInstance;
    private static AsyncHttpClient mClient;

	private ArrayList<HttpMethod> mTypes;
    private ArrayList<String> mUrls;
    private ArrayList<Object> mRequests;
    private ArrayList<JsonResponse> mListeners;

    private boolean mIsPending = false;

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
        mRequests = new ArrayList<Object>();
        mListeners = new ArrayList<JsonResponse>();
    }
    
    public void get(String aUrl, RequestParams vParams, JsonResponse jsonResponse)
    {
    	send(HttpMethod.GET, aUrl, vParams, jsonResponse);
    }
    
    public void post(String aUrl, RequestParams vParams, JsonResponse jsonResponse)
    {
    	send(HttpMethod.POST, aUrl, vParams, jsonResponse);
    }
    
    public void post(String aUrl, JSONObject vParams, JsonResponse jsonResponse)
    {
    	send(HttpMethod.POST, aUrl, vParams, jsonResponse);
    }
    
    public void put(String aUrl, RequestParams vParams, JsonResponse jsonResponse)
    {
    	send(HttpMethod.PUT, aUrl, vParams, jsonResponse);
    }
    
    public void delete(String aUrl, RequestParams vParams, JsonResponse jsonResponse)
    {
    	send(HttpMethod.DELETE, aUrl, vParams, jsonResponse);
    }

    private void send(HttpMethod aType, String aUrl, RequestParams aParams, final JsonResponse aListener)
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
                
                aParams.add("Key", vKey);

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
    }
    
    private void send(HttpMethod aType, String aUrl, JSONObject aParams, final JsonResponse aListener)
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
                	aParams = new JSONObject();
                }
                
                try 
                {
					aParams.put("Key", vKey);
				} 
                catch (JSONException e)
                {
				}

                Log.d(Engine.APP_NAME, "URL: " + Engine.SERVER_URL + aUrl);
                
                StringEntity entity = null;
				try 
				{
					entity = new StringEntity(aParams.toString());
					entity.setContentType("application/json");
				} 
				catch (UnsupportedEncodingException e)
				{
				}

                MyJsonHttpResponseHandler vResponseHandler = new MyJsonHttpResponseHandler(aListener, this);
                
                switch(aType)
                {
					case POST:
					{
						mClient.post(Engine.get(), Engine.SERVER_URL + aUrl, entity, "application/json", vResponseHandler);
						break;
					}
					
					default:
					try 
					{
						throw new MethodNotSupportedException("");
					} 
					catch (MethodNotSupportedException e)
					{
						Log.e(Engine.APP_NAME, "NOT IMPLEMENTED");
					}
                }
            }
            else
            {
                enqueue(aType, aUrl, aParams, aListener);
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
        	Object vP = mRequests.remove(0);
        	JsonResponse vR = mListeners.remove(0);
        	
        	if(vP instanceof RequestParams)
        	{
        		send(vM, vU, (RequestParams) vP, vR);
        	}
        	else
        	{
        		send(vM, vU, (JSONObject) vP, vR);	
        	}   
        }
    }

    private void enqueue(HttpMethod aType, String aUrl, Object aParams, JsonResponse aListener)
    {
    	mTypes.add(aType);
        mUrls.add(aUrl);
        mRequests.add(aParams);
        mListeners.add(aListener);
    }
}
