package it.itskennedy.tsaim.geoad.core;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

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
    private ArrayList<RequestParams> mRequests;
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
        mRequests = new ArrayList<RequestParams>();
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

                JsonHttpResponseHandler vResponseHandler = new JsonHttpResponseHandler()
                {
                    @Override
					public void onSuccess(int statusCode, Header[] headers,	JSONObject response)
                    {
                    	if(aListener != null)
                        {
                            aListener.onResponse(true, response);
                        }

                        executeQueue();
					}

					@Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response)
                    {
                        if(aListener != null)
                        {
                            aListener.onResponse(true, response);
                        }

                        executeQueue();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse)
                    {
                    	if(aListener != null)
                    	{
                    		aListener.onResponse(false, null);
                    	}
                    	
                    	executeQueue();
                    }
                };
                
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

    private void executeQueue()
    {
        mIsPending = false;

        if(mRequests.size() > 0)
        {
            send(mTypes.remove(0), mUrls.remove(0), mRequests.remove(0), mListeners.remove(0));
        }
    }

    private void enqueue(HttpMethod aType, String aUrl, RequestParams aParams, JsonResponse aListener)
    {
    	mTypes.add(aType);
        mUrls.add(aUrl);
        mRequests.add(aParams);
        mListeners.add(aListener);
    }
}
