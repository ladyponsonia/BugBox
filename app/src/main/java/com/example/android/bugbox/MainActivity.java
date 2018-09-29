package com.example.android.bugbox;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.android.bugbox.model.Bug;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //dummy data for bug locations
    public static ArrayList<Bug> bugsList = new ArrayList<Bug>();

    public static final String CHANNEL_ID = "BugBox_Notifications_channel_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize bug data
        bugsList.add(new Bug("Beetle","4yufxgZ1QQ2", 33.930277f , -118.434982f , 200.0f));
        bugsList.add( new Bug("Grasshopper","e3HTaUWGeWs", 33.920791f , -118.413011f , 200.0f));
        bugsList.add( new Bug("Ladybug","4K7V5f9ntfu", 33.969689f , -118.434464f , 200.0f));

        createNotificationChannel();
    }

    //start button function
    public void startBugsActivity(View view){
        Intent bugsIntent = new Intent(MainActivity.this, BugsActivity.class);
        startActivity(bugsIntent);
    }

    //create notification channel
    //with help from https://developer.android.com/training/notify-user/build-notification
    private void createNotificationChannel() {

        CharSequence name = getString(R.string.notification_channel_name);
        String description = getString(R.string.notification_channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

}
