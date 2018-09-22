package com.example.android.bugbox.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.example.android.bugbox.BugsActivity;
import com.example.android.bugbox.MapFragment;
import com.example.android.bugbox.MyBugsFragment;


/* with help from https://www.truiton.com/2015/06/android-tabs-example-fragments-viewpager/*/
public class FragmentPagerAdapter extends FragmentStatePagerAdapter {

        int mNumOfTabs;

        public FragmentPagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    MapFragment tab1 = new MapFragment();
                    return tab1;
                case 1:
                    MyBugsFragment tab2 = new MyBugsFragment();
                    return tab2;
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
}
