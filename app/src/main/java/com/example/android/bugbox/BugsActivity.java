package com.example.android.bugbox;

import android.content.IntentFilter;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.android.bugbox.adapters.FragmentPagerAdapter;
import com.example.android.bugbox.adapters.LockableViewPager;
import com.example.android.bugbox.background.BugDownloadedBroadcastReceiver;
import com.example.android.bugbox.utilities.Geofencing;
import com.google.android.gms.location.LocationServices;

public class BugsActivity extends AppCompatActivity{

    private final String TAG = this.getClass().getSimpleName();

    private static FragmentPagerAdapter mAdapter;
    private TabLayout mTabLayout;
    private static LockableViewPager mViewPager;
    private Geofencing mGeofencing;
    private BugDownloadedBroadcastReceiver mReceiver;

    public static final String[] tabTitles = new String[2];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bugs);

        //get tabs titles
        tabTitles[0] = getResources().getString(R.string.map_tab_name);
        tabTitles[1] = getResources().getString(R.string.mybugs_tab_name);


        //tabs with help from https://www.truiton.com/2015/06/android-tabs-example-fragments-viewpager/
        //and https://medium.com/@droidbyme/android-material-design-tabs-tab-layout-with-swipe-884085ae80ff
        mTabLayout = findViewById(R.id.bugs_tab_layout);
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());

        mViewPager = findViewById(R.id.fragments_pager);
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setSwipeable(false);
        mTabLayout.setupWithViewPager(mViewPager);

        //register geofences
        mGeofencing = new Geofencing(this, LocationServices.getGeofencingClient(this));
        mGeofencing.createGeofenceList();
        mGeofencing.registerAllGeofences(BugsActivity.this);

        //register receiver to get notified when bug is done downloading
        // with help from https://www.101apps.co.za/articles/using-an-intentservice-to-do-background-work.html
        mReceiver = new BugDownloadedBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BugDownloadedBroadcastReceiver.SHOW_BUGS_ACTION);
        intentFilter.addAction(BugDownloadedBroadcastReceiver.SHOW_BUGS_ACTION);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        localBroadcastManager.registerReceiver(mReceiver, intentFilter);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        refreshMyBugs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregister receiver
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        localBroadcastManager.unregisterReceiver(mReceiver);
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
