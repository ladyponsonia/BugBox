package com.example.android.bugbox.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.Format;
import java.util.List;

/*bug 3D asset info
contains all bug info downloaded from Poly
used in mybugs fragment and AR activity
All Bug3D model classes built with http://www.jsonschema2pojo.org/*/
public class Bug3D {

    @SerializedName("name")
    @Expose
    private String mName;
    @SerializedName("displayName")
    @Expose
    private String mDisplayName;
    @SerializedName("authorName")
    @Expose
    private String mAuthorName;
    @SerializedName("formats")
    @Expose
    private List<BugFormats> mFormats = null;
    @SerializedName("thumbnail")
    @Expose
    private BugThumbnail mThumbnail;

    public Bug3D (String name, String displayName, String authorName,
                  List<BugFormats> formats, BugThumbnail thumbnail){
        this.mName = name;
        this.mDisplayName = displayName;
        this.mAuthorName = authorName;
        this.mFormats = formats;
        this.mThumbnail = thumbnail;
    }


    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        this.mDisplayName = displayName;
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