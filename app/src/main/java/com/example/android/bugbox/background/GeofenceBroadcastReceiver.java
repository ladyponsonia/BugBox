package com.example.android.bugbox.background;

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

//with help from https://stackoverflow.com/questions/33074048/how-to-retrieve-the-latitude-and-longitude-of-a-triggered-geofence-from-public-l
public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getSimpleName();
    private static final float ERROR_MARGIN =  0.0001f;;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "OnReceive called");

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            Location location = geofencingEvent.getTriggeringLocation();
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.d(TAG, "Lat/Lon: " + latitude + ", " +longitude);


            //find the bug that triggered the geofence transition
            Bug bugFound = null;
            for (Bug bug:MainActivity.bugsList){
                float lat = bug.getLat();
                float lng = bug.getLon();

                //compare values with a margin of error
                Log.d(TAG, "Lat: " + (Math.abs(lat - latitude) < ERROR_MARGIN));
                Log.d(TAG, "Lon: " + (Math.abs(lng - longitude) < ERROR_MARGIN));
                if (Math.abs(lat - latitude) < ERROR_MARGIN && Math.abs(lng - longitude) < ERROR_MARGIN){
                    bugFound = bug;
                    Log.d (TAG, "bugFound:"+ bugFound);
                }
            }
            //send notification
            if(bugFound != null){
                Log.d (TAG, "sendNotification called");
                NotificationUtils.sendNotification(context, bugFound);
            }

        }
    }
}
