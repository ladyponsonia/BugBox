package com.example.android.bugbox.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.android.bugbox.BugsActivity;

public class BugDownloadedBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getSimpleName();

    public static final String OPEN_BUGS_ACTION = "open-bugs-activity";
    public static final String MYBUGS_TAB_KEY= "start-on-mybugs-tab";

    public BugDownloadedBroadcastReceiver(){

    }
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "OnReceive called");
        Log.d(TAG, "action received: " + intent.getAction());
        //String action = intent.getAction();
        //if (OPEN_BUGS_ACTION.equals(action)) {
            //start bugs activity and show MyBugs tab
            Intent myBugsIntent = new Intent(context, BugsActivity.class);
            myBugsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            myBugsIntent.putExtra(MYBUGS_TAB_KEY, MYBUGS_TAB_KEY);//to start activity on mybugs tab
            context.startActivity(myBugsIntent);



        //}
    }
}
