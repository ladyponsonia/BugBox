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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
    }

    //start button function
    public void startBugsActivity(View view){
        Intent bugsIntent = new Intent(MainActivity.this, BugsActivity.class);
        startActivity(bugsIntent);
    }
}
