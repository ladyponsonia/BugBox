package com.example.android.bugbox.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.example.android.bugbox.MainActivity;
import com.example.android.bugbox.model.Bug;
import com.example.android.bugbox.utilities.NotificationUtils;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

//with help from https://stackoverflow.com/questions/33074048/how-to-retrieve-the-latitude-and-longitude-of-a-triggered-geofence-from-public-l
public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BROADCAST RECEIVER", "OnReceive called");

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            Location location = geofencingEvent.getTriggeringLocation();
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.d("BROADCAST RECEIVER", "Lat/Lon: " + latitude + ", " +longitude);


            //find the bug that triggered the geofence transition
            Bug bugFound = null;
            for (Bug bug:MainActivity.bugsList){
                float lat = bug.getLat();
                float lng = bug.getLon();

                //compare values with a margin of error
                float error = 0.00001f;
                Log.d("BROADCAST RECEIVER", "Lat: " + (Math.abs(lat - latitude) < error));
                Log.d("BROADCAST RECEIVER", "Lon: " + (Math.abs(lng - longitude) < error));
                if (Math.abs(lat - latitude) < error && Math.abs(lng - longitude) < error){
                    bugFound = bug;
                    Log.d ("BROADCAST RECEIVER", "bugFound:"+ bugFound);
                }
            }
            //send notification
            if(bugFound != null){
                Log.d ("BROADCAST RECEIVER", "sendNotification called");
                NotificationUtils.sendNotification(context, bugFound);
            }

        }
    }
}
