package com.example.android.bugbox;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.android.bugbox.adapters.TabsFragmentPagerAdapter;
import com.example.android.bugbox.adapters.LockableViewPager;
import com.example.android.bugbox.background.BugDownloadedBroadcastReceiver;
import com.example.android.bugbox.model.Bug;
import com.example.android.bugbox.utilities.Geofencing;
import com.example.android.bugbox.utilities.NotificationUtils;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class BugsActivity extends AppCompatActivity{

    private static final String TAG = "BugsActivity";
    private static final int ACCESS_LOCATION_CODE = 444;

    private TabsFragmentPagerAdapter mAdapter;
    private TabLayout mTabLayout;
    private LockableViewPager mViewPager;
    private Geofencing mGeofencing;
    private BugDownloadedBroadcastReceiver mReceiver;
    private boolean mDeleteInstrShown;

    public static final String[] tabTitles = new String[2];

    //dummy data for bug locations
    public static final ArrayList<Bug> bugsList = new ArrayList<Bug>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bugs);

        Log.d(TAG, "OnCreate");
        //initialize dummy bug data
        bugsList.add( new Bug(getString(R.string.stag_beetle_name), getString(R.string.stag_beetle_info),"4yufxgZ1QQ2",0.006f, 33.930277f , -118.434982f , 50.0f));
        bugsList.add( new Bug(getString(R.string.anaconda_name), getString(R.string.anaconda_info),"1pi9DfAbsz0", 0.03f, 33.930527f , -118.423598f , 50.0f));
        bugsList.add( new Bug(getString(R.string.ladybug_name), getString(R.string.ladybug_info),"4K7V5f9ntfu", 0.005f,33.969689f , -118.434464f , 50.0f));
        bugsList.add( new Bug(getString(R.string.hornet_name), getString(R.string.hornet_info),"6h7-AWppj5e",0.004f,33.920791f, -118.413011f, 100.0f));

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
        mAdapter = new TabsFragmentPagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setSwipeable(false);
        mTabLayout.setupWithViewPager(mViewPager);

        //if myBugs tab selected show delete instructions toast
        //with help from https://stackoverflow.com/questions/31172729/how-to-get-current-selected-tab-index-in-tablayout
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                //show delete instructions when MyBugs tab is selected,
                // if there are bugs in the rv and if the message wasn't already shown
                int position = tab.getPosition();
                int bugs = 0;
                String tag = (String) mViewPager.getTag(position);
                MyBugsFragment frag = (MyBugsFragment) getSupportFragmentManager().findFragmentByTag(tag) ;
                if (frag!=null) {
                    bugs = frag.getAdapterCount();
                }
                if(position == 1 && bugs>0 && !mDeleteInstrShown){
                    Toast.makeText(BugsActivity.this, R.string.delete_bug_instructions, Toast.LENGTH_LONG).show();
                    mDeleteInstrShown = true;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        //get intent
        Intent intent = this.getIntent();
        String extra;
        if (intent.hasExtra(BugDownloadedBroadcastReceiver.MYBUGS_TAB_KEY)){
            Log.d(TAG, "Intent received from Broadcast Receiver");
            switchToMyBugsTab();
        }

        //register geofences
        mGeofencing = new Geofencing(this, LocationServices.getGeofencingClient(this));
        mGeofencing.createGeofenceList();
        mGeofencing.registerAllGeofences(BugsActivity.this);


        //register receiver to get notified when bug is done downloading
        // with help from https://www.101apps.co.za/articles/using-an-intentservice-to-do-background-work.html
        mReceiver = new BugDownloadedBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BugDownloadedBroadcastReceiver.OPEN_BUGS_ACTION);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        localBroadcastManager.registerReceiver(mReceiver, intentFilter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshMyBugs();
    }

    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregister receiver
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        localBroadcastManager.unregisterReceiver(mReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Geofencing.ACCESS_FINE_LOCATION_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
                //register geofences
                mGeofencing.registerAllGeofences(BugsActivity.this);
                //move map
                String tag = (String) mViewPager.getTag(0);
                MapFragment frag = (MapFragment) getSupportFragmentManager().findFragmentByTag(tag) ;
                if (frag!=null) {
                    frag.centerOnUserLocation();
                }
            } else {
                // permission denied
                Toast.makeText(this, R.string.geofence_location_permission_denied,
                        Toast.LENGTH_LONG).show();
                finish();//go back to main activity
                }
            }
        }

    //refresh myBugs recycler view
    public void refreshMyBugs(){
        String tag = (String) mViewPager.getTag(1);
        MyBugsFragment frag = (MyBugsFragment) getSupportFragmentManager().findFragmentByTag(tag) ;
        if (frag!=null) {
            frag.refreshData();
        }
    }

    public void switchToMyBugsTab(){
        mViewPager.setCurrentItem(1);
        //scroll to newest bug
        String tag = (String) mViewPager.getTag(1);
        Log.d(TAG, "switchToMyBugsTab tag: "+ tag);
        MyBugsFragment frag = (MyBugsFragment) getSupportFragmentManager().findFragmentByTag(tag) ;
        if (frag!=null) {
            Log.d(TAG, "switchToMyBugsTab: "+ frag);
            frag.scrollToNewBug();
        }
    }
}
