package com.example.android.bugbox;

import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.example.android.bugbox.contentProvider.BugsContract;
import com.example.android.bugbox.contentProvider.BugsContract.BugEntry;

public class ARActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String TAG = this.getClass().getSimpleName();
    private static final int DB_QUERY_LOADER_ID = 555;

    private int mId;
    private TextView mName;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        Intent intent = getIntent();
        mId = intent.getExtras().getInt(MyBugsFragment.BUG_ID);
        Log.d("AR ACTIVITY", String.valueOf(mId));

        mName = findViewById(R.id.ar_bug_tv);
        Log.d("AR ACTIVITY", mName.toString());

        getSupportLoaderManager().initLoader(DB_QUERY_LOADER_ID, null, this);


    }

    @Override
    public Loader onCreateLoader(int id, Bundle bundle) {

        String[] mProjection = {BugsContract.BugEntry.COLUMN_NAME};
        String mSelection = "_id=?";
        String[] mSelectionArgs = new String[]{String.valueOf(mId)};

        return new CursorLoader(this,
                BugEntry.CONTENT_URI,
                mProjection,
                mSelection,
                mSelectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d (TAG, "onLoadFinished, data: " + data.toString()) ;
        Log.d (TAG, "onLoadFinished, bool: " + data.moveToFirst()) ;
        if (mCursor != null) mCursor.close();
        mCursor = data;
        if (data != null && data.moveToFirst()) {
            String bugName = data.getString(data.getColumnIndex(BugEntry.COLUMN_NAME));
            mName.setText(bugName);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d (TAG, "onLoaderReset");
        if (mCursor != null) mCursor.close();
        mCursor = null;

    }
}
