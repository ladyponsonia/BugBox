package com.example.android.bugbox.model;

import android.content.res.Resources;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BugFormats {

    @SerializedName("root")
    @Expose
    private BugRoot mRoot;
    @SerializedName("resources")
    @Expose
    private List<BugResources> mResources = null;

    public BugFormats( BugRoot root, List<BugResources> resources){
        this.mRoot = root;
        this.mResources = resources;
    }


    public BugRoot getRoot() {
        return mRoot;
    }

    public void setRoot(BugRoot root) {
        this.mRoot = root;
    }

    public List<BugResources> getResources() {
        return mResources;
    }

    public void setResources(List<BugResources> resources) {
        this.mResources = resources;
    }
}
