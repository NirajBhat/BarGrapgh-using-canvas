package com.nexttools.constants;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.nexttools.activity.WebViewActivity;
import com.nexttools.dao.Tabs;

import java.util.ArrayList;

/**
 * Created by next on 9/5/17.
 */
public class NextToolConstants {
   //Contains the TAB data as arrayList set as constants
    public static  ArrayList<Tabs> tabsArrayList;
    public static long fileSize;
    public static int i=0;
    public static final String BASE_URL = "http://192.168.10.14/download/nexttools/mobile/NextToolsModuleBuilds/";
    public static final String unZippath= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/Unzip/";
    public static final String zipPath= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/download/nexttools/mobile/NextToolsModuleBuilds/" ;

    public static boolean internetConnectvity(Context mContext) {
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return true;
        }
        return false;
    }



}
