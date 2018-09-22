package com.example.android.bugbox.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BugThumbnail {


    @SerializedName("relativePath")
    @Expose
    private String mRelativePath;
    @SerializedName("url")
    @Expose
    private String mUrl;

    public BugThumbnail( String path, String url){
        this.mRelativePath = path;
        this.mUrl = url;
    }

    public String getRelativePath() {
        return mRelativePath;
    }

    public void setRelativePath(String relativePath) {
        this.mRelativePath = relativePath;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }
}
