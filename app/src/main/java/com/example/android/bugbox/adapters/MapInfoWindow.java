package com.example.android.bugbox.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bugbox.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

//with help from http://www.zoftino.com/google-maps-android-custom-info-window-example

public class MapInfoWindow implements GoogleMap.InfoWindowAdapter {

    private Context mContext;

    public MapInfoWindow(Context context){
        mContext = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)mContext).getLayoutInflater()
                .inflate(R.layout.map_info, null);

        TextView nameTV = view.findViewById(R.id.map_bug_name);
        TextView infoTV = view.findViewById(R.id.map_bug_info);

        nameTV.setText(marker.getTitle());
        infoTV.setText(marker.getSnippet());

        return view;
    }
}
