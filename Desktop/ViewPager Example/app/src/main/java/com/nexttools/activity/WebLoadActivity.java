package com.nexttools.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.nexttools.R;

/**
 * Created by next on 17/5/17.
 */
public class WebLoadActivity extends AppCompatActivity {
  public static final String TAG ="WebLoadActivity";
    WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_load);
        mWebView = (WebView) findViewById(R.id.webView);
        String URL = getIntent().getStringExtra("url");
        Log.i(TAG, "onCreate: In WEBVIEW------------>"+URL);
            mWebView.loadUrl(URL);
        setWebViewSettings(mWebView);

        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setInitialScale(100);
        mWebView.setWebViewClient(new MyWebView());
        mWebView.setWebChromeClient(new WebChromeClient());
        // mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);

    }

    private class MyWebView extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private void setWebViewSettings(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        // WideViewport that  loads the WebView completely
        webSettings.setUseWideViewPort(true);
        //DOMStorage for local storage
        webSettings.setDomStorageEnabled(true);


    }
}
