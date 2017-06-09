package com.nexttools.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.nexttools.R;
import com.nexttools.constants.NextToolConstants;
import com.nexttools.storing.SharedpreferenceUtils;

import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkView;

public class WebViewActivity extends AppCompatActivity {
    WebView mWebView;
    String mURLToLoad;
    private XWalkView mXWalkView;
    public static final String TAG="display";
    String urltodownload,mUnzipLoc,dirforUNZiptargetlocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        urltodownload = getIntent().getExtras().getString("UrlModule");
        Log.i("urlmodule", "onCreate: "+urltodownload);
       // dirforUNZiptargetlocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/Unzip/" + mModuleUrl+"/index.html";
        dirforUNZiptargetlocation= SharedpreferenceUtils.getInstance(this).getModuleLocation(urltodownload);
        mWebView= (WebView) findViewById(R.id.webViewid);


       /* if(Build.VERSION.SDK_INT >= 15 && Build.VERSION.SDK_INT<=19) {*/

            mXWalkView= (XWalkView) findViewById(R.id.webViewidd);
            mXWalkView.setVisibility(View.VISIBLE);

            mUnzipLoc = SharedpreferenceUtils.getInstance(this).getModuleLocation(urltodownload);

             mURLToLoad = NextToolConstants.unZippath+urltodownload+"/index.html";
            mXWalkView.load("file:///"+ mURLToLoad,null);
            Log.i(TAG, "onCreate: "+ "file:///"+ mUnzipLoc);
            XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        // creoss walk
        }
   /*     else {

            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setLoadsImagesAutomatically(true);
            //mWebView.addJavascriptInterface(new WebViewJavaScriptInterface(this),"app" );
            mWebView.setWebViewClient(new WebViewClient());
            mWebView.setWebChromeClient(new WebChromeClient());
            // mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
            mWebView.getSettings().setLoadWithOverviewMode(true);
            mWebView.getSettings().setUseWideViewPort(true);
            mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            mWebView.setScrollbarFadingEnabled(false);
            mWebView.getSettings().setBuiltInZoomControls(true);
            mWebView.getSettings().setDomStorageEnabled(true);
            mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

            // mModuleUrl=  getIntent().getStringExtra("UrlModule");

            mWebView.loadUrl("file:///" + mURLToLoad);
        }*/

    }

