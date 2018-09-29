package com.example.android.bugbox.contentProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.UserDictionary;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.example.android.bugbox.contentProvider.BugsContract.BugEntry;


public class BugsContentProvider extends ContentProvider {

    private static final int BUGS = 100;
    private static final int BUG_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(BugsContract.AUTHORITY, BugsContract.PATH_BUGS, BUGS);
        uriMatcher.addURI(BugsContract.AUTHORITY, BugsContract.PATH_BUGS + "/#", BUG_WITH_ID);

        return uriMatcher;
    }

    // Member variable for a DbHelper that's initialized in the onCreate() method
    private BugDbHelper mBugDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mBugDbHelper = new BugDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    //query all bugs to display in mybugs recycler view
    //or query selected bug to get info for AR activity
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mBugDbHelper.getReadableDatabase();

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case BUGS:
                retCursor =  db.query(BugEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case BUG_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};
                retCursor =  db.query(BugEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Nullable
    @Override
    //insert new bug
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // Get access to the movie database
        final SQLiteDatabase db = mBugDbHelper.getWritableDatabase();

        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case BUGS:
                long id = db.insert(BugEntry.TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(BugEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // default case for unknown URI's
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the content resolver
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    //delete single bug from db
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mBugDbHelper.getWritableDatabase();

        int bugDeleted;
        String id = uri.getPathSegments().get(1);
        String mSelection = "_id=?";
        String[] mSelectionArgs = new String[]{id};

        switch (sUriMatcher.match(uri)) {
            case BUG_WITH_ID:
                bugDeleted = db.delete(BugEntry.TABLE_NAME, mSelection, mSelectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (bugDeleted != 0) {
            // A bug was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return bugDeleted;
    }

    @Override
    //to add saved files location to db
    //with help from https://developer.android.com/guide/topics/providers/content-provider-basics
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mBugDbHelper.getWritableDatabase();

        String id = uri.getPathSegments().get(1);
        String mSelection = "_id=?";
        String[] mSelectionArgs = new String[]{id};
        int rowUpdated;

        switch (sUriMatcher.match(uri)) {
            case BUG_WITH_ID:
                rowUpdated = db.update( BugEntry.TABLE_NAME, values, mSelection, mSelectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the content resolver
        getContext().getContentResolver().notifyChange(uri, null);

        return rowUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
