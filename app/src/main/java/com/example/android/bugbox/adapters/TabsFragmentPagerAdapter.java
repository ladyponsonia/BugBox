package com.example.android.bugbox.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.example.android.bugbox.BugsActivity;
import com.example.android.bugbox.MapFragment;
import com.example.android.bugbox.MyBugsFragment;

import java.util.HashMap;
import java.util.Map;


/* with help from https://www.truiton.com/2015/06/android-tabs-example-fragments-viewpager/
and https://stackoverflow.com/questions/18609261/getting-the-current-fragment-instance-in-the-viewpager*/

public class TabsFragmentPagerAdapter extends FragmentPagerAdapter {

    int mNumOfTabs;
    public static Map<Integer, String> mFragmentTags = new HashMap<>();

    public TabsFragmentPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new MapFragment();
            case 1:
                return new MyBugsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String[] tabsTitles = BugsActivity.tabTitles;
        return tabsTitles[position];
    }

    //override method to be able to get the current fragment from the activity
    //with help from https://stackoverflow.com/questions/14035090/how-to-get-existing-fragments-when-using-fragmentpageradapter
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
        // get the tags set by FragmentPagerAdapter
        switch (position) {
            case 0:
                String mapTag = createdFragment.getTag();
                //save tag
                mFragmentTags.put(0, mapTag);
                break;
            case 1:
                String bugsTag = createdFragment.getTag();
                mFragmentTags.put(1, bugsTag);
                break;
        }

        return createdFragment;
    }


    public String getTag(int position){
        return mFragmentTags.get(position);
    }

    /*
    public Fragment getFragment(int position) {
        if( position == 0 ){
            return mTab1;
        }else if ( position == 1){
            return mTab2;
        }else{
            return null;
        }
    }*/


}
