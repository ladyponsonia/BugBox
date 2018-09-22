package com.example.android.bugbox.contentProvider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.bugbox.contentProvider.BugsContract.BugEntry;

public class BugDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BugsDb.db";
    private static final int VERSION = 1;

    // Constructor
    BugDbHelper(Context context) {

        super(context, DATABASE_NAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create bugs table
        final String CREATE_TABLE = "CREATE TABLE "  + BugEntry.TABLE_NAME + " (" +
                BugEntry._ID                + " INTEGER PRIMARY KEY, " +
                BugEntry.COLUMN_POLY_ASSET_ID + " TEXT NOT NULL, " +
                BugEntry.COLUMN_NAME  + " TEXT NOT NULL, " +
                BugEntry.COLUMN_AUTHOR_NAME   + " TEXT, " +
                BugEntry.COLUMN_OBJ_URL + " TEXT NOT NULL, " +
                BugEntry.COLUMN_MTL_URL + " TEXT, " +
                BugEntry.COLUMN_TEXTURE_URL + " TEXT, " +
                BugEntry.COLUMN_THUMBNAIL + " TEXT NOT NULL, " +
                BugEntry.COLUMN_OBJ_FILE + " TEXT, " +
                BugEntry.COLUMN_MTL_FILE + " TEXT, " +
                BugEntry.COLUMN_PNG_FILE + " TEXT, " +
                BugEntry.COLUMN_OBJ_FILENAME + " TEXT, " +
                BugEntry.COLUMN_MTL_FILENAME + " TEXT, " +
                BugEntry.COLUMN_TEXTURE_FILENAME    + " TEXT );";

        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BugEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
