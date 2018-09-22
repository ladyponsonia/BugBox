package com.example.android.bugbox.contentProvider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.android.bugbox.model.Bug;
import com.example.android.bugbox.model.Bug3D;
import com.example.android.bugbox.model.BugFormats;
import com.example.android.bugbox.model.BugResources;
import com.example.android.bugbox.model.BugRoot;
import com.example.android.bugbox.model.BugThumbnail;

import com.example.android.bugbox.contentProvider.BugsContract.BugEntry;


import java.util.ArrayList;
import java.util.List;

public class ContentProviderUtils {

    //put Cursor data into ArrayList<Bug3D> for BugAdapter
    public static  ArrayList<Bug3D> cursorToBugList(Cursor bugsCursor){

        ArrayList<Bug3D> bugsList = new ArrayList<Bug3D>();
        if (bugsCursor.moveToFirst()){
            int i = 0;
            while(!bugsCursor.isAfterLast()){
                String bugId = bugsCursor.getString(bugsCursor.getColumnIndex(BugEntry.COLUMN_POLY_ASSET_ID));
                String bugName = bugsCursor.getString(bugsCursor.getColumnIndex(BugEntry.COLUMN_NAME));
                String bugAuthor = bugsCursor.getString(bugsCursor.getColumnIndex(BugEntry.COLUMN_AUTHOR_NAME));
                String bugObjFileLoc = bugsCursor.getString(bugsCursor.getColumnIndex(BugEntry.COLUMN_OBJ_FILE));
                String bugMtlFileLoc = bugsCursor.getString(bugsCursor.getColumnIndex(BugEntry.COLUMN_MTL_FILE));
                String bugPngFileLoc = bugsCursor.getString(bugsCursor.getColumnIndex(BugEntry.COLUMN_PNG_FILE));

                //build bug thumbnail
                String bugThumbUrl = bugsCursor.getString(bugsCursor.getColumnIndex(BugEntry.COLUMN_THUMBNAIL));
                BugThumbnail bugThumbnail = new BugThumbnail(bugThumbUrl);

                //build bug formats
                String bugObjUrl = bugsCursor.getString(bugsCursor.getColumnIndex(BugEntry.COLUMN_OBJ_URL));
                String bugMtlUrl = bugsCursor.getString(bugsCursor.getColumnIndex(BugEntry.COLUMN_MTL_URL));
                String bugPngUrl = bugsCursor.getString(bugsCursor.getColumnIndex(BugEntry.COLUMN_TEXTURE_URL));

                String bugObjFileName = bugsCursor.getString(bugsCursor.getColumnIndex(BugEntry.COLUMN_OBJ_FILENAME));
                String bugMtlFileName = bugsCursor.getString(bugsCursor.getColumnIndex(BugEntry.COLUMN_MTL_FILENAME));
                String bugPngFileName = bugsCursor.getString(bugsCursor.getColumnIndex(BugEntry.COLUMN_TEXTURE_FILENAME));

                //create bug root and resources
                BugRoot bugObj = new BugRoot(bugObjFileName, bugObjUrl);
                BugResources bugMtl = new BugResources(bugMtlFileName, bugMtlUrl);
                BugResources bugPng = new BugResources(bugPngFileName, bugPngUrl);

                //make resource list for bugformats
                List<BugResources> resources =new ArrayList<BugResources>();
                resources.add(bugMtl);
                resources.add(bugPng);
                BugFormats bugFormats = new BugFormats(bugObj, resources);

                // make formats list for Bug3D constructor
                List<BugFormats> bugFormatsList = new ArrayList<BugFormats>();
                bugFormatsList.add(bugFormats);

                bugsCursor.moveToNext();
                //create new bug with data from cursor row
                Bug3D bug = new Bug3D( bugId, bugName, bugAuthor, bugFormatsList, bugThumbnail,
                        bugObjFileLoc, bugMtlFileLoc, bugPngFileLoc);
                bugsList.add(bug);
                i++;
            }
            bugsCursor.close();
        }
        return bugsList;
    }

    public static void insertBug (Bug3D bug, Context context){

        //add bug from json response to db
        ContentValues contentValues = new ContentValues();
        contentValues.put(BugEntry.COLUMN_POLY_ASSET_ID, bug.getPolyAssetId());
        contentValues.put(BugEntry.COLUMN_NAME, bug.getName());
        contentValues.put(BugEntry.COLUMN_AUTHOR_NAME, bug.getAuthorName());
        contentValues.put(BugEntry.COLUMN_THUMBNAIL, bug.getThumbnail().getUrl());
        contentValues.put(BugEntry.COLUMN_OBJ_FILENAME, bug.getFormats().get(0).getRoot().getRelativePath());
        contentValues.put(BugEntry.COLUMN_OBJ_URL, bug.getFormats().get(0).getRoot().getUrl());
        contentValues.put(BugEntry.COLUMN_MTL_FILENAME, bug.getFormats().get(0).getResources().get(0).getRelativePath());
        contentValues.put(BugEntry.COLUMN_MTL_URL, bug.getFormats().get(0).getResources().get(0).getUrl());
        contentValues.put(BugEntry.COLUMN_TEXTURE_FILENAME, bug.getFormats().get(0).getResources().get(1).getRelativePath());
        contentValues.put(BugEntry.COLUMN_TEXTURE_URL, bug.getFormats().get(0).getResources().get(1).getUrl());

        Uri uri = context.getContentResolver().insert(BugEntry.CONTENT_URI, contentValues );

        // Display the URI that's returned with a Toast
        if(uri != null) {
            Toast.makeText(context, uri.toString(), Toast.LENGTH_LONG).show();
            Log.d("INSERT BUG", uri.toString());
        }
    }
}
