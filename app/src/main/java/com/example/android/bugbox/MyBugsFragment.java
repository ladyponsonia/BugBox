package com.example.android.bugbox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.bugbox.adapters.BugAdapter;
import com.example.android.bugbox.contentProvider.BugsContract.BugEntry;


public class MyBugsFragment extends Fragment implements BugAdapter.BugOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = this.getClass().getSimpleName();

    private View mRootview;
    private RecyclerView mBugsRV;
    public static BugAdapter mBugAdapter;
    private LayoutManager mLayoutManager;
    private LinearLayout mNoDataLayout;

    private static final int DB_QUERY_LOADER_ID = 888;
    public static final String BUG_ID = "bug-id";
    public static final String BUG_NUMBER_KEY = "number-of-bugs";



    public MyBugsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootview = inflater.inflate(R.layout.fragment_my_bugs, container, false);
        mNoDataLayout = mRootview.findViewById(R.id.no_bugs_lyt);
        setBugAdapter();
        //db query to get data for mybugs recycler view
        getActivity().getSupportLoaderManager().initLoader(DB_QUERY_LOADER_ID, null, this);

        // Add a touch helper to swipe left to delete an item.
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                Log.d(TAG, "item swipped");

                int id = (int) viewHolder.itemView.getTag();
                // Build uri with id appended
                Uri uri = BugEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(Integer.toString(id)).build();
                //Delete row from db
                getActivity().getContentResolver().delete(uri, null, null);
                // Restart the loader for rv
                refreshData();

            }
        }).attachToRecyclerView(mBugsRV);
        return mRootview;
    }


    @Override
    public void onClick(int id) {
        Intent ARIntent = new Intent(getActivity(), ARActivity.class);
        ARIntent.putExtra(BUG_ID, id );
        startActivity(ARIntent);
    }


    @Override
    public Loader onCreateLoader(int id, Bundle bundle) {

        String[] mProjection = {BugEntry._ID, BugEntry.COLUMN_NAME, BugEntry.COLUMN_THUMBNAIL};

        return new CursorLoader(getContext(),
                BugEntry.CONTENT_URI,
                mProjection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
        Log.d (TAG, "onLoadFinished ") ;
        mBugAdapter.swapCursor(newCursor);
        saveNumberOfBugs();//saves number to display in app widget
        //if there's no data show no bugs msg
        if (mBugAdapter.getItemCount()==0) {
            mNoDataLayout.setVisibility(View.VISIBLE);
            mBugsRV.setVisibility(View.INVISIBLE);
        } else {
            mNoDataLayout.setVisibility(View.INVISIBLE);
            mBugsRV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mBugAdapter.swapCursor(null);
        Log.d (TAG, "onLoaderReset");
    }


    private void setBugAdapter() {
        mBugsRV = mRootview.findViewById(R.id.bugs_rv);
        mBugAdapter = new BugAdapter(getActivity(), this );
        //2 column for portrait, 3 column for landscape
        int columnsNum = 2;
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            columnsNum = 3;
        }
        mLayoutManager = new GridLayoutManager(getActivity(),columnsNum );
        mBugsRV.setLayoutManager(mLayoutManager);
        mBugsRV.setAdapter(mBugAdapter);
    }


    //db re-query for mybugs recycler view
    // to be called from activity when new bug is added to db
    public void refreshData(){
        Log.d (TAG, "refreshing data") ;
        getActivity().getSupportLoaderManager().restartLoader(DB_QUERY_LOADER_ID, null, this );
    }

    //saves number of bugs in rv to shared preferences
    private void saveNumberOfBugs(){
        int numberOfBugs;
        if (mBugAdapter != null) {
            numberOfBugs = mBugAdapter.getItemCount();
        }else{
            numberOfBugs = 0;
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(BUG_NUMBER_KEY, numberOfBugs);
        editor.commit();

    }

    public void scrollToNewBug(){
        mBugsRV.scrollToPosition(mBugAdapter.getItemCount() - 1);
    }
}
