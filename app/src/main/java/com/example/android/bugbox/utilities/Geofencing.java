package com.example.android.bugbox.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.android.bugbox.MainActivity;
import com.example.android.bugbox.background.GeofenceBroadcastReceiver;
import com.example.android.bugbox.model.Bug;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;


//with help from https://developer.android.com/training/location/geofencing and
//https://android-developers.googleblog.com/2017/06/reduce-friction-with-new-location-apis.html
public class Geofencing {

    public static final String TAG = Geofencing.class.getSimpleName();
    private static final long GEOFENCE_TIMEOUT = 60480000; // 1 week
    private static final int ACCESS_FINE_LOCATON_CODE = 444;

    private List<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private GeofencingClient mGeofencingClient;
    private Context mContext;

    public Geofencing(Context context, GeofencingClient client) {
        mContext = context;
        mGeofencingClient = client;
        mGeofencePendingIntent = null;
        mGeofenceList = new ArrayList<>();
    }

    //create list of geofences using data from buglist in main activity
    public void createGeofenceList() {
        ArrayList<Bug> bugList = MainActivity.bugsList;
        mGeofenceList = new ArrayList<>();
        for (int i = 0; i < bugList.size(); i++) {
            String requestId = String.valueOf(i);
            float lat = bugList.get(i).getLat();
            float lng = bugList.get(i).getLon();
            float radius = bugList.get(i).getRadius();
            // Build a Geofence object
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(requestId)
                    .setExpirationDuration(GEOFENCE_TIMEOUT)
                    .setCircularRegion(lat, lng, radius)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build();
            // Add it to the list
            mGeofenceList.add(geofence);
        }
    }

    //create geofence request
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    //create pending intent
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(mContext, GeofenceBroadcastReceiver.class);
        mGeofencePendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    public void registerAllGeofences( Activity activity) {

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATON_CODE);
            return;
        }
        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("GEOFENCING", "geofences added");
                    }
                })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("GEOFENCING", "failed to add geofences");
                    }
                });
    }

}
