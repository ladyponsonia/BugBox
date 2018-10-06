package com.example.android.bugbox;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.android.bugbox.model.Bug;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


/**
 *Maps with help from https://developers.google.com/maps/documentation/android-sdk/intro
 * https://www.youtube.com/watch?v=M0bYvXlhgSI&index=3&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt
 * https://stackoverflow.com/questions/34817412/how-to-use-googlemap-in-fragment-android
 * https://www.journaldev.com/10380/android-google-maps-example-tutorial
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int ACCESS_FINE_LOCATON_CODE = 444;

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private ProgressBar mProgressBar;


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
        mProgressBar = rootview.findViewById(R.id.map_progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.setUserVisibleHint(false);
        Log.d("MAPfragment", "onCreateView" + mMapFragment.toString());

        //start map
        mMapFragment.getMapAsync(this);
        return rootview;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        //check/request location permissions
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATON_CODE);
            return;
        }
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.setMyLocationEnabled(true);

        //get bugs data
        ArrayList<Bug> bugsList = MainActivity.bugsList;

        //add marker on map for each bug on the list
        for (Bug bug : bugsList){
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(bug.getLat(), bug.getLon())).title(bug.getName()));
        }
        //center camera on 1st bug
        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(bugsList.get(0).getLat(), bugsList.get(0).getLon()), 12));
        mProgressBar.setVisibility(View.INVISIBLE);
        mMapFragment.setUserVisibleHint(true);

        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null){
            Log.d("MAP", location.toString());
        }
    }

}
