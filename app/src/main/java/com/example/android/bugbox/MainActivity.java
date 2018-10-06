package com.example.android.bugbox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.android.bugbox.model.Bug;
import com.example.android.bugbox.utilities.NotificationUtils;

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
        bugsList.add( new Bug("Caterpillar","64T47O5VHAB",33.930527f, -118.423598f, 100.0f));
        NotificationUtils.createNotificationChannel(this);
    }

    //start button function
    public void startBugsActivity(View view){
        Intent bugsIntent = new Intent(MainActivity.this, BugsActivity.class);
        startActivity(bugsIntent);
    }
}
