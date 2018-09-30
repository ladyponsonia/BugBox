package com.example.android.bugbox;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.android.bugbox.adapters.FragmentPagerAdapter;
import com.example.android.bugbox.adapters.LockableViewPager;
import com.example.android.bugbox.services.Geofencing;
import com.example.android.bugbox.utilities.NotificationUtils;
import com.google.android.gms.location.LocationServices;

public class BugsActivity extends AppCompatActivity{

    private final String TAG = this.getClass().getSimpleName();

    private static FragmentPagerAdapter mAdapter;
    private TabLayout mTabLayout;
    private static LockableViewPager mViewPager;
    private Geofencing mGeofencing;

    public static final String[] tabTitles = new String[2];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bugs);

        //get tabs titles
        tabTitles[0] = getResources().getString(R.string.map_tab_name);
        tabTitles[1] = getResources().getString(R.string.mybugs_tab_name);

        /*set toolbar
        Toolbar toolbar = findViewById(R.id.bugs_toolbar);
        setSupportActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);*/

        //tabs with help from https://www.truiton.com/2015/06/android-tabs-example-fragments-viewpager/
        //and https://medium.com/@droidbyme/android-material-design-tabs-tab-layout-with-swipe-884085ae80ff
        mTabLayout = findViewById(R.id.bugs_tab_layout);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.map_tab_name));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.mybugs_tab_name));

        mViewPager = findViewById(R.id.fragments_pager);
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setSwipeable(false);
        mTabLayout.setupWithViewPager(mViewPager);

        //register geofences
        mGeofencing = new Geofencing(this, LocationServices.getGeofencingClient(this));
        mGeofencing.createGeofenceList();
        mGeofencing.registerAllGeofences(BugsActivity.this);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        refreshMyBugs();
    }

    //refresh myBugs recycler view
    public static void refreshMyBugs() {
        MyBugsFragment frag = (MyBugsFragment) mAdapter.getFragment(1);
        frag.refreshData();
    }

    public static void switchToMyBugsTab(){
        Log.d("BUGS ACTIVITY", "switchToMyBugsTab called");
        mViewPager.setCurrentItem(1, true);
    }
}
