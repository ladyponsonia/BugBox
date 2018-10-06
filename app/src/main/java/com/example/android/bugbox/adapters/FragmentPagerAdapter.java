package com.example.android.bugbox.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import com.example.android.bugbox.BugsActivity;
import com.example.android.bugbox.MapFragment;
import com.example.android.bugbox.MyBugsFragment;

import java.util.HashMap;


/* with help from https://www.truiton.com/2015/06/android-tabs-example-fragments-viewpager/
and https://stackoverflow.com/questions/18609261/getting-the-current-fragment-instance-in-the-viewpager*/

public class FragmentPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    MapFragment mTab1;
    MyBugsFragment mTab2;

    public FragmentPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                mTab1 = new MapFragment();
                return mTab1;
            case 1:
                mTab2 = new MyBugsFragment();
                return mTab2;
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


    public Fragment getFragment(int position) {
        if( position == 0 ){
            return mTab1;
        }else if ( position == 1){
            return mTab2;
        }else{
            return null;
        }
    }


}
