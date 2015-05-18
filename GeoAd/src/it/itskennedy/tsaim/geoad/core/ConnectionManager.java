package it.itskennedy.tsaim.geoad.core;

import android.content.Context;
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

    private ArrayList<String> mUrls;
    private ArrayList<RequestParams> mRequests;
    private ArrayList<JsonResponse> mListeners;

    private boolean mIsPending = false;

    public interface JsonResponse
    {
        void onResponse(boolean aResult, JSONArray aResponse);
    }

    public static ConnectionManager get(Context aContext)
    {
        if(mInstance == null)
        {
            mInstance = new ConnectionManager(aContext);
        }

        return mInstance;
    }
    
    public ConnectionManager(Context aContext)
    {
        mClient = new AsyncHttpClient();

        mUrls = new ArrayList<String>();
        mRequests = new ArrayList<RequestParams>();
        mListeners = new ArrayList<JsonResponse>();
    }

    public void send(String aUrl, RequestParams aParams, final JsonResponse aListener)
    {
        if(!mIsPending)
        {
            mIsPending = true;

            Log.d(Engine.APP_NAME, "URL: " + Engine.SERVER_URL + aUrl);

            mClient.post(Engine.SERVER_URL + aUrl, aParams, new JsonHttpResponseHandler()
            {
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

                }
            });
        }
        else
        {
            enqueue(aUrl, aParams, aListener);
        }
    }

    private void executeQueue()
    {
        mIsPending = false;

        if(mRequests.size() > 0)
        {
            send(mUrls.remove(0), mRequests.remove(0), mListeners.remove(0));
        }
    }

    private void enqueue(String aUrl, RequestParams aParams, JsonResponse aListener)
    {
        mUrls.add(aUrl);
        mRequests.add(aParams);
        mListeners.add(aListener);
    }
}
