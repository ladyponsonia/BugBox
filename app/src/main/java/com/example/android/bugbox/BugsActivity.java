package com.example.android.bugbox;

import android.content.IntentFilter;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.android.bugbox.adapters.FragmentPagerAdapter;
import com.example.android.bugbox.adapters.LockableViewPager;
import com.example.android.bugbox.background.BugDownloadedBroadcastReceiver;
import com.example.android.bugbox.model.Bug;
import com.example.android.bugbox.utilities.Geofencing;
import com.example.android.bugbox.utilities.NotificationUtils;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class BugsActivity extends AppCompatActivity{

    private final String TAG = this.getClass().getSimpleName();

    private static FragmentPagerAdapter mAdapter;
    private TabLayout mTabLayout;
    private static LockableViewPager mViewPager;
    private Geofencing mGeofencing;
    private BugDownloadedBroadcastReceiver mReceiver;

    public static final String[] tabTitles = new String[2];

    //dummy data for bug locations
    public static final ArrayList<Bug> bugsList = new ArrayList<Bug>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bugs);

        //initialize bug data
        bugsList.add( new Bug("Beetle","4yufxgZ1QQ2", 33.930277f , -118.434982f , 50.0f));
        bugsList.add( new Bug("Grasshopper","e3HTaUWGeWs", 33.920791f , -118.413011f , 50.0f));
        bugsList.add( new Bug("Ladybug","4K7V5f9ntfu", 33.969689f , -118.434464f , 50.0f));
        bugsList.add( new Bug("Caterpillar","64T47O5VHAB",33.930527f, -118.423598f, 100.0f));

        //create notification channel
        NotificationUtils.createNotificationChannel(this);

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
        /*final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {

            }
        };
        handler.postDelayed(r, 1000);*/




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
