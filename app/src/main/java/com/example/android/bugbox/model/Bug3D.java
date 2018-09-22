package com.example.android.bugbox.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/*bug 3D asset info
contains all bug info downloaded from Poly
used in mybugs fragment and AR activity
All Bug3D model classes built with http://www.jsonschema2pojo.org/*/
public class Bug3D {

    @SerializedName("name")
    @Expose
    private String mPolyAssetId;
    @SerializedName("displayName")
    @Expose
    private String mName;
    @SerializedName("authorName")
    @Expose
    private String mAuthorName;
    @SerializedName("formats")
    @Expose
    private List<BugFormats> mFormats = null;
    @SerializedName("thumbnail")
    @Expose
    private BugThumbnail mThumbnail;

    @Nullable
    private String mObjFileLocation;

    @Nullable
    private String mMtlFileLocation;

    @Nullable
    private String mPngFileLocation;


    public Bug3D (String polyAssetID, String name, String authorName,
                  List<BugFormats> formats, BugThumbnail thumbnail){
        this.mPolyAssetId = polyAssetID;
        this.mName = name;
        this.mAuthorName = authorName;
        this.mFormats = formats;
        this.mThumbnail = thumbnail;
    }

    public Bug3D (String polyAssetID, String name, String authorName,
                  List<BugFormats> formats, BugThumbnail thumbnail,
                  String objFileLocation, String mtlFileLocation, String pngFileLocation){
        this.mPolyAssetId = polyAssetID;
        this.mName = name;
        this.mAuthorName = authorName;
        this.mFormats = formats;
        this.mThumbnail = thumbnail;
        this.mObjFileLocation = objFileLocation;
        this.mMtlFileLocation = mtlFileLocation;
        this.mPngFileLocation = pngFileLocation;
    }


    public String getPolyAssetId() {
        return mPolyAssetId;
    }

    public void setPolyAssetId(String polyAssetId) {
        this.mPolyAssetId = polyAssetId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getAuthorName() {
        return mAuthorName;
    }

    public void setAuthorName(String authorName) {
        this.mAuthorName = authorName;
    }

    public List<BugFormats> getFormats() {
        return mFormats;
    }

    public void setFormats(List<BugFormats> formats) {
        this.mFormats = formats;
    }

    public BugThumbnail getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(BugThumbnail thumbnail) {
        this.mThumbnail = thumbnail;
    }

}