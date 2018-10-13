package com.example.android.bugbox.contentProvider;

import android.net.Uri;
import android.provider.BaseColumns;

public class BugsContract {
    public static final String AUTHORITY = "com.example.android.bugbox";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_BUGS = "bugs";

    /* inner class that defines the contents of the  table */
    public static final class BugEntry implements BaseColumns {

        // BugEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BUGS).build(
                );

        // Movie table and column names
        public static final String TABLE_NAME = "bugs";

        // table has an automatically produced "_ID" column in addition to the ones below
        public static final String COLUMN_POLY_ASSET_ID = "polyAssetID";
        public static final String COLUMN_SCALE = "scale";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_AUTHOR_NAME = "authorName";
        public static final String COLUMN_OBJ_URL = "objUrl";
        public static final String COLUMN_MTL_URL = "mtlUrl";
        public static final String COLUMN_TEXTURE_URL = "pngUrl";
        public static final String COLUMN_THUMBNAIL = "thumbUrl";
        public static final String COLUMN_OBJ_FILE = "objFileLocation";
        public static final String COLUMN_MTL_FILE = "mtlFileLocation";
        public static final String COLUMN_PNG_FILE = "pngFileLocation";
        public static final String COLUMN_OBJ_FILENAME = "objFilename";
        public static final String COLUMN_MTL_FILENAME = "mtlFilename";
        public static final String COLUMN_TEXTURE_FILENAME = "pngFilename";

    }
}
