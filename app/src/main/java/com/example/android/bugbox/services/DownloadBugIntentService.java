package com.example.android.bugbox.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.android.bugbox.utilities.NotificationUtils;

public class DownloadBugIntentService extends IntentService {

    //constructor
    public DownloadBugIntentService(){super(DownloadBugIntentService.class.getSimpleName());}

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String action = intent.getAction();
        NotificationUtils.executeAction(getApplicationContext(), action);

    }
}
