package com.nexttools.storing;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by next on 20/5/17.
 */
public class SharedpreferenceUtils {



    private static SharedpreferenceUtils _instance;
    private SharedPreferences _sharedPreferences;

    private SharedpreferenceUtils(Context context)
    {
        _sharedPreferences = context.getSharedPreferences("keyID", Context.MODE_PRIVATE);
    }

    public static synchronized SharedpreferenceUtils getInstance(Context context)
    {
        if (_instance == null)
        {
            return _instance = new SharedpreferenceUtils(context);
        }
        return _instance;
    }

    private void storingLong(String key, long value)
    {
        SharedPreferences.Editor editor = _sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    private void storingString(String key, String value)
    {
        SharedPreferences.Editor editor = _sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void storeNodeId(long value)
    {
        storingLong(AppConstants.PREF_TIME_TAMPERING, value);
    }

    public long getNodeId()
    {
        return _sharedPreferences.getLong(AppConstants.PREF_TIME_TAMPERING, 0);
    }

    public  void storingModuleLocation(String key,String path){
        storingString(key, path);
    }
    public  String getModuleLocation(String key){
        return _sharedPreferences.getString(key, null);
    }
}
