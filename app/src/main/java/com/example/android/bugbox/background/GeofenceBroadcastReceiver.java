package com.example.android.bugbox.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.example.android.bugbox.BugsActivity;
import com.example.android.bugbox.model.Bug;
import com.example.android.bugbox.utilities.NotificationUtils;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

//with help from https://stackoverflow.com/questions/33074048/how-to-retrieve-the-latitude-and-longitude-of-a-triggered-geofence-from-public-l
public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getSimpleName();
    private static final float ERROR_MARGIN = 50;//meters

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "OnReceive called");

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        // Get the transition type and location that triggered the event
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Location enteredLocation = geofencingEvent.getTriggeringLocation();

            //find the bug that triggered the geofence transition
            //with help from https://stackoverflow.com/questions/17983865/making-a-location-object-in-android-with-latitude-and-longitude-values
            Bug bugFound = null;
            for (Bug bug: BugsActivity.bugsList){
                //make new location object
                Location bugLocation = new Location("");
                bugLocation.setLatitude(bug.getLat());
                bugLocation.setLongitude(bug.getLon());

                float distanceInMeters =  bugLocation.distanceTo(enteredLocation);
                Log.d(TAG, "Distance in meters to " + bug.getName() +": " + distanceInMeters);
                if (distanceInMeters<ERROR_MARGIN){
                    bugFound = bug;
                    Log.d (TAG, "bugFound:"+ bugFound);
                    break;
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
