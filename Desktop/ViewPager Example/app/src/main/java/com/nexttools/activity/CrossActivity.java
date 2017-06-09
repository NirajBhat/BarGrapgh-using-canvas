package com.nexttools.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.nexttools.R;
import com.nexttools.storing.SharedpreferenceUtils;

import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkView;

public class CrossActivity extends AppCompatActivity{
    private XWalkView mXWalkView;
    String mUnzipLoc, mModuleUrl;
    private static final String TAG = "CrossActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xwlak);
        mXWalkView= (XWalkView) findViewById(R.id.xwalk);
        //  mXWalkView.setScrollbarFadingEnabled(true);
       mModuleUrl =  getIntent().getStringExtra("UrlModule");

        mUnzipLoc = SharedpreferenceUtils.getInstance(this).getModuleLocation(mModuleUrl);
        mXWalkView.load("file:///"+ mUnzipLoc,null);
        Log.i(TAG, "onCreate: "+ "file:///"+ mUnzipLoc);
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);

    }


}
