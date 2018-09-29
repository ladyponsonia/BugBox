package com.example.android.bugbox.utilities;

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
        if (bug.getFormats().get(0).getResources().size()>1) {
            contentValues.put(BugEntry.COLUMN_TEXTURE_FILENAME, bug.getFormats().get(0).getResources().get(1).getRelativePath());
            contentValues.put(BugEntry.COLUMN_TEXTURE_URL, bug.getFormats().get(0).getResources().get(1).getUrl());
        }

        Uri uri = context.getContentResolver().insert(BugEntry.CONTENT_URI, contentValues );

        // Display the URI that's returned with a Toast
        if(uri != null) {
            Toast.makeText(context, uri.toString(), Toast.LENGTH_LONG).show();
            Log.d("INSERT BUG", uri.toString());
        }
    }
}
