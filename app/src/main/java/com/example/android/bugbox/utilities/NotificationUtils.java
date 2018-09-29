package com.example.android.bugbox.utilities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.Action;

import com.example.android.bugbox.BugsActivity;
import com.example.android.bugbox.MainActivity;
import com.example.android.bugbox.R;
import com.example.android.bugbox.services.DownloadBugIntentService;

public class NotificationUtils {

    public static final String ACTION_DOWNLOAD_BUG = "download-bug";
    public static final String ACTION_DISMISS = "dismiss-notification";
    private static final int NOTIFICATION_ID = 100;
    private static final int ACTION_DOWNLOAD_PENDING_INTENT_ID = 200;
    private static final int ACTION_DISMISS_PENDING_INTENT_ID = 205;
    private static final int OPEN_BUGS_PENDING_INTENT_ID = 210;

    //Notification to download bug
    //with help from https://developer.android.com/training/notify-user/build-notification
    //and https://stackoverflow.com/questions/48260875/android-how-to-create-a-notification-with-action
    public static void sendNotification(Context context) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setChannelId(MainActivity.CHANNEL_ID)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_text))
                .setColor(context.getColor(R.color.colorPrimaryDark))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent(context))
                .addAction(downloadAction(context))
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
        Action dismissAction = new Action(0,context.getString(R.string.dismiss_action_text),
                dismissPendingIntent);
        return dismissAction;
    }

    // create action for download button
    private static Action downloadAction(Context context) {
        // Intent to launch DownloadBugIntentService
        Intent downloadBugIntent = new Intent(context, DownloadBugIntentService.class);
        // Set the action of the intent
        downloadBugIntent.setAction(NotificationUtils.ACTION_DOWNLOAD_BUG);
        //Pending intent
        PendingIntent downloadBugPendingIntent = PendingIntent.getService(context,ACTION_DOWNLOAD_PENDING_INTENT_ID,
                downloadBugIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        //create action and return it
        Action downloadAction = new Action(0, context.getString(R.string.collect_action_text),
                downloadBugPendingIntent);
        return downloadAction;
    }

    //pending intent to open bugs activity
    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, BugsActivity.class);
        return PendingIntent.getActivity( context, OPEN_BUGS_PENDING_INTENT_ID, startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void executeAction(Context context, String action) {
        if (ACTION_DOWNLOAD_BUG.equals(action)) {
            DownloadBugUtils.downloadBug(context);
            NotificationUtils.clearAllNotifications(context);
        } else if (ACTION_DISMISS.equals(action)) {
            NotificationUtils.clearAllNotifications(context);
        }
    }

    // clear all notifications
    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }



}
