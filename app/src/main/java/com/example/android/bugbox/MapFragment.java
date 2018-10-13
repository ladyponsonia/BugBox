package com.example.android.bugbox;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.android.bugbox.adapters.MapInfoWindow;
import com.example.android.bugbox.model.Bug;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


/**
 *Maps with help from https://developers.google.com/maps/documentation/android-sdk/intro
 * https://www.youtube.com/watch?v=M0bYvXlhgSI&index=3&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt
 * https://stackoverflow.com/questions/34817412/how-to-use-googlemap-in-fragment-android
 * https://www.journaldev.com/10380/android-google-maps-example-tutorial
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private ProgressBar mProgressBar;
    private FloatingActionButton mMyLocation;
    private FloatingActionButton mDirections;
    private Marker mMarkerSelected = null;
    private boolean mHasConnection;
    private boolean mIsMapReady;
    private Location mUserLocation;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_map, container, false);

        //get views
        mMyLocation = rootview.findViewById(R.id.my_location_fab);
        mMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                centerOnUserLocation();
            }
        });
        mMyLocation.setVisibility(View.INVISIBLE);
        mDirections = rootview.findViewById(R.id.directions_fab);
        mDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMarkerSelected == null) {
                    Toast.makeText(getContext(), R.string.directions_toast, Toast.LENGTH_LONG).show();
                } else {
                    navigateWithGMaps(mMarkerSelected);
                }

            }
        });
        mDirections.setVisibility(View.INVISIBLE);
        mProgressBar = rootview.findViewById(R.id.map_progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.setUserVisibleHint(false);

        //start map
        mMapFragment.getMapAsync(this);
        return rootview;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        //get bugs data
        ArrayList<Bug> bugsList = BugsActivity.bugsList;

        //add marker on map for each bug on the list
        for (Bug bug : bugsList) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(bug.getLat(), bug.getLon()))
                    .title(bug.getName())
                    .snippet(bug.getInfo()))
                    .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bug_map_marker));

        }
        //set custom info window
        MapInfoWindow mapInfoWindow = new MapInfoWindow(getContext());
        mMap.setInfoWindowAdapter(mapInfoWindow);

        mProgressBar.setVisibility(View.INVISIBLE);
        mMyLocation.setVisibility(View.VISIBLE);
        mDirections.setVisibility(View.VISIBLE);
        mMapFragment.setUserVisibleHint(true);

            centerOnUserLocation();




        mMap.setOnMarkerClickListener(this);

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        mMarkerSelected = marker;
        marker.showInfoWindow();
        return true;
    }


    //set camera to current location
    public void centerOnUserLocation() {

        try {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            //check location permission
            if (getContext().checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED ) {
                mUserLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            }
        }catch(Exception e){
            e.printStackTrace();
            Log.d(TAG, "Exception caught");
            Toast.makeText(getContext(),getString(R.string.map_error), Toast.LENGTH_LONG ).show();
        }
        if (mUserLocation != null) {
            Log.d(TAG, mUserLocation.toString());
            //center camera on user's location
            mMap.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(mUserLocation.getLatitude(), mUserLocation.getLongitude()), 12));
        }
    }

    //open google maps to provide navigation
    //with help from https://developers.google.com/maps/documentation/urls/android-intents
    private void navigateWithGMaps(Marker marker){

        LatLng markerPosition = marker.getPosition();
        Uri intentUri = Uri.parse("google.navigation:q="+ markerPosition.latitude +","+ markerPosition.longitude);
        String title = marker.getTitle();
        Intent navigationIntent = new Intent(Intent.ACTION_VIEW, intentUri);
        navigationIntent.setPackage("com.google.android.apps.maps");
        startActivity(navigationIntent);

    }
}