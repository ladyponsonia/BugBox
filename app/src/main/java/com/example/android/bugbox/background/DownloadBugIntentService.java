package com.example.android.bugbox.background;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.example.android.bugbox.utilities.DownloadBugUtils;
import com.example.android.bugbox.utilities.NotificationUtils;

public class DownloadBugIntentService extends IntentService {

    //constructor
    public DownloadBugIntentService(){super(DownloadBugIntentService.class.getSimpleName());}

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String polyAssetId = null;
        float scale = 0.0f;
        String action = intent.getAction();
        Context context = getApplicationContext();
        if (intent.hasExtra(NotificationUtils.POLY_ASSET_ID_KEY)) {
            polyAssetId = intent.getStringExtra(NotificationUtils.POLY_ASSET_ID_KEY);
        }
        if (intent.hasExtra(NotificationUtils.SCALE_KEY)) {
            scale = intent.getFloatExtra(NotificationUtils.SCALE_KEY, 0.006f);
        }

        //handle selected notification action
        if (NotificationUtils.ACTION_DOWNLOAD_BUG.equals(action) && polyAssetId != null) {
            DownloadBugUtils.downloadBug(context, polyAssetId, scale);
            NotificationUtils.clearAllNotifications(context);

            //send local broadcast to DownloadedBugReceiver so it can start bugs activity
            Intent bugsIntent= new Intent(BugDownloadedBroadcastReceiver.SHOW_BUGS_ACTION);
            bugsIntent.setAction(BugDownloadedBroadcastReceiver.SHOW_BUGS_ACTION);
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
            localBroadcastManager.sendBroadcast(bugsIntent);

        } else if (NotificationUtils.ACTION_DISMISS.equals(action)) {
            NotificationUtils.clearAllNotifications(context);
        }
    }
}
