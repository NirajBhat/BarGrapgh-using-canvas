package com.nexttools.adapter;

import android.app.Application;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nexttools.fragments.ModulesFragment;

import java.util.ArrayList;

/**
 * Created by next on 28/4/17.
 *
 */
public class PagerAdapter extends FragmentPagerAdapter {
    //private static int NUM_ITEMS = 5;
    ArrayList<String> mArrayList;
    public PagerAdapter(FragmentManager fm, ArrayList<String> arrayList)
    {
        super(fm);
        mArrayList = arrayList;

    }


    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        return ModulesFragment.newInstance(position,mArrayList.get(position));
    }


    // Returns total number of pages
    @Override
    public int getCount() {
        return mArrayList.size();
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return mArrayList.get(position);
    }
}