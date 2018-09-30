package com.example.android.bugbox;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.android.bugbox.model.Bug;
import com.example.android.bugbox.services.Geofencing;
import com.example.android.bugbox.utilities.NotificationUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    //dummy data for bug locations
    public static final ArrayList<Bug> bugsList = new ArrayList<Bug>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //initialize bug data
        bugsList.add( new Bug("Beetle","4yufxgZ1QQ2", 33.930277f , -118.434982f , 50.0f));
        bugsList.add( new Bug("Grasshopper","e3HTaUWGeWs", 33.920791f , -118.413011f , 50.0f));
        bugsList.add( new Bug("Ladybug","4K7V5f9ntfu", 33.969689f , -118.434464f , 50.0f));

        NotificationUtils.createNotificationChannel(this);
    }

    //start button function
    public void startBugsActivity(View view){
        Intent bugsIntent = new Intent(MainActivity.this, BugsActivity.class);
        startActivity(bugsIntent);
    }
}
