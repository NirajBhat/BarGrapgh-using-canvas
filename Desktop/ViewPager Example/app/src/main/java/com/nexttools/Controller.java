package com.nexttools;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.nexttools.constants.NextToolConstants;
import com.nexttools.interfaces.OnParseListener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by next on 19/5/17.
 */
public class Controller extends AsyncTask<Void,Void,Object> {
    Context mContext;
    String BaseURL;
    OnParseListener parseListener;
    public static final String TAG = "Controller Class ";

    public Controller(Context mContext, String baseUrl, OnParseListener listener) {
        this.mContext = mContext;
        this.BaseURL = baseUrl;
        parseListener =listener;
    }




    /*@Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        parseListener.onSuccess(o);
        Log.i(TAG, "onPostExecute: its in Controller of onPostExecute " +parseListener);
    }*/

    @Override
    protected Object doInBackground(Void... params) {
        long contentFileSize = 0;
        try {
            URL url = new URL(BaseURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            String responseGiven = urlConnection.getResponseMessage();
            Log.i(TAG, "makeServerCall: " + responseGiven);
            contentFileSize  = urlConnection.getContentLength();
            Log.i(TAG, "makeServerCall: file size is available" + NextToolConstants.fileSize);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentFileSize;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        parseListener.onSuccess(o);
        Log.i(TAG, "onPostExecute: its in Controller of onPostExecute " +parseListener);
    }
}
