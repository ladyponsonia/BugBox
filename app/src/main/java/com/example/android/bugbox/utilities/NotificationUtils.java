package com.example.android.bugbox.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.Action;
import android.util.Log;

import com.example.android.bugbox.BugsActivity;
import com.example.android.bugbox.R;
import com.example.android.bugbox.model.Bug;
import com.example.android.bugbox.background.DownloadBugIntentService;

public class NotificationUtils {

    public static final String CHANNEL_ID = "BugBox_download_notifications_channel";
    public static final String ACTION_DOWNLOAD_BUG = "download-bug";
    public static final String ACTION_DISMISS = "dismiss-notification";
    private static final int NOTIFICATION_ID = 100;
    private static final int ACTION_DOWNLOAD_PENDING_INTENT_ID = 200;
    private static final int ACTION_DISMISS_PENDING_INTENT_ID = 205;
    private static final int OPEN_BUGS_PENDING_INTENT_ID = 210;
    public static final String POLY_ASSET_ID_KEY = "poly-asset-id";
    public static final String SCALE_KEY = "bug-scale";

    //create notification channel
    //with help from https://developer.android.com/training/notify-user/build-notification
    public static void createNotificationChannel(Context context) {

        CharSequence name = context.getString(R.string.notification_channel_name);
        String description = context.getString(R.string.notification_channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    //Notification to download bug
    //with help from https://developer.android.com/training/notify-user/build-notification
    //and https://stackoverflow.com/questions/48260875/android-how-to-create-a-notification-with-action
    public static void sendNotification(Context context, Bug bug) {

        String bugName = bug.getName();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setChannelId(CHANNEL_ID)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_text)+ bugName)
                .setColor(context.getColor(R.color.colorPrimaryDark))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent(context))
                .addAction(downloadAction(context, bug))
                .addAction(dismissAction(context))
                .setAutoCancel(true);

        //show notification

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    //  create action for dismiss button
    private static Action dismissAction(Context context) {
        // Intent to launch DownloadBugIntentService
        Intent dismissIntent = new Intent(context, DownloadBugIntentService.class);
        //Set the action of the intent
        dismissIntent.setAction(ACTION_DISMISS);
        //PendingIntent
        PendingIntent dismissPendingIntent = PendingIntent.getService( context, ACTION_DISMISS_PENDING_INTENT_ID,
                dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Create an Action and return it
        return new Action(0,context.getString(R.string.dismiss_action_text), dismissPendingIntent);

    }

    // create action for download button
    private static Action downloadAction(Context context, Bug bug) {
        // Intent to launch DownloadBugIntentService
        Intent downloadBugIntent = new Intent(context, DownloadBugIntentService.class);
        //send polyAssetId and scale as extras
        downloadBugIntent.putExtra(POLY_ASSET_ID_KEY, bug.getPolyAssetID());
        downloadBugIntent.putExtra(SCALE_KEY, bug.getScale());
        // Set the action of the intent
        downloadBugIntent.setAction(NotificationUtils.ACTION_DOWNLOAD_BUG);
        //Pending intent
        PendingIntent downloadBugPendingIntent = PendingIntent.getService(context,ACTION_DOWNLOAD_PENDING_INTENT_ID,
                downloadBugIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        //create action and return it
        return new Action(0, context.getString(R.string.collect_action_text), downloadBugPendingIntent);
    }

    //pending intent to open bugs activity
    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, BugsActivity.class);
        return PendingIntent.getActivity( context, OPEN_BUGS_PENDING_INTENT_ID, startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }


    // clear all notifications
    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }



}
