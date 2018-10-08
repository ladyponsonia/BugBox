package com.example.android.bugbox.model;


//bug location info
//contains all bug info used by the map fragment
public class Bug {

    private String mName;
    private String mInfo;
    private String mPolyAssetID;
    private float mGfLat;
    private float mGfLon;
    private float mGfRadius;

    public Bug (String name, String info, String polyAssetId, float lat, float lon, float radius){
        this.mName = name;
        this.mInfo = info;
        this.mPolyAssetID = polyAssetId;
        this.mGfLat = lat;
        this.mGfLon = lon;
        this.mGfRadius = radius;
    }

    public String getName(){
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getInfo(){
        return mInfo;
    }

    public void setInfo(String info) {
        mInfo = info;
    }

    public String getPolyAssetID(){
        return mPolyAssetID;
    }

    public void setPolyAssetID(String polyAssetID) {
        mPolyAssetID = polyAssetID;
    }

    public float getLat(){
        return mGfLat;
    }

    public void setLat(float lat) {
        mGfLat = lat;
    }

    public float getLon(){
        return mGfLon;
    }

    public void setLon(float lon) {
        mGfLon = lon;
    }

    public float getRadius(){
        return mGfRadius;
    }

    public void setRadius(float radius) {
        mGfRadius = radius;
    }
}
