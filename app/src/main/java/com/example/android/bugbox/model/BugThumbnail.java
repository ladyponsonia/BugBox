package com.example.android.bugbox.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BugThumbnail {


    @SerializedName("url")
    @Expose
    private String mUrl;

    public BugThumbnail( String url){
        this.mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }
}

