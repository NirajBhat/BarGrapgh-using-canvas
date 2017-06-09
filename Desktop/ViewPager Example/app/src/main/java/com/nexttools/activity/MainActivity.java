package com.nexttools.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nexttools.R;
import com.nexttools.adapter.PagerAdapter;
import com.nexttools.dao.Tabs;
import com.nexttools.interfaces.OnParseListener;
import com.nexttools.threads.HomeAsyncTask;

import java.util.ArrayList;
import java.util.List;

/**Purpose:

        * It Is The UI Class Which Hold The UI Elements.
        * It Listens To Action Performed In UI class.
        * It Implements And The Observer Pattern To Listen Changes In The View .
        * It Holds The View  To Update Its State Of The UI.
        * It is The Activity Which Need To Be Included In Manifest.xml File.
 * */

public class MainActivity extends AppCompatActivity implements OnParseListener {
    TabLayout mTabLayout;
    ViewPager mViewPager;
    PagerAdapter myPagerAdapter;
    ImageView mQuikLaunch_Image;
    ImageButton mImageButton;

    //ArrayList to set the Tabs Names
    ArrayList<String>  stringArrayList = new ArrayList<>();
   List<Tabs> tabsList;
 //   List<String> titleInCard = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null){

        }
        setContentView(R.layout.activity_main);

        //Adding toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mQuikLaunch_Image= (ImageView) findViewById(R.id.quicklaunch);



        //Initializing the tablayout
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);

        stringArrayList.add("Maths");
        stringArrayList.add("Chemistry");
        stringArrayList.add("Social");
        stringArrayList.add("English");
        stringArrayList.add("Primary");

        //Initializing viewPager
        mViewPager = (ViewPager) findViewById(R.id.Viewpager);
        /*myPagerAdapter=new PagerAdapter(getSupportFragmentManager(), stringArrayList);
        mViewPager.setAdapter(myPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);*/

        //AysncTask function to parse the Json data
        HomeAsyncTask homeAsyncTask = new HomeAsyncTask(this,this);
        homeAsyncTask.execute();


        mImageButton = (ImageButton) findViewById(R.id.searchButton);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SearchScreenActivity.class);
                startActivity(intent);
            }
        });


    }
    //Interface method used to set the names and the subject data to the adapter
    @Override
    public void onSuccess(Object object) {


       // addTextListerner();
        myPagerAdapter=new PagerAdapter(getSupportFragmentManager(), stringArrayList);
        mViewPager.setAdapter(myPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        myPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onException() {
        Toast.makeText(MainActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }
}
