package com.example.android.bugbox.background;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.util.Log;

import com.example.android.bugbox.BugsActivity;
import com.example.android.bugbox.MyBugsFragment;

public class BugDownloadedBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getSimpleName();

    public static final String SHOW_BUGS_ACTION = "show-bugs";

    public BugDownloadedBroadcastReceiver(){

    }
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "OnReceive called");
        String action = intent.getAction();
        if (SHOW_BUGS_ACTION.equals(action)) {
            //start bugs activity and show MyBugs tab
            Intent myBugsIntent = new Intent(context, BugsActivity.class);
            context.startActivity(myBugsIntent);
            BugsActivity.switchToMyBugsTab();
            MyBugsFragment frag = (MyBugsFragment)BugsActivity.getFragmentFromPager(1);
            frag.scrollToNewBug();
        }
    }
}
