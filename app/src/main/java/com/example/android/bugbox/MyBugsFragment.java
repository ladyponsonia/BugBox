package com.example.android.bugbox;

import android.content.Intent;
import android.net.Uri;
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

import com.example.android.bugbox.adapters.BugAdapter;
import com.example.android.bugbox.contentProvider.BugsContract.BugEntry;


public class MyBugsFragment extends Fragment implements BugAdapter.BugOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    private View mRootview;
    private RecyclerView mBugsRV;
    private BugAdapter mBugAdapter;
    private LayoutManager mLayoutManager;


    private static final int DB_QUERY_LOADER_ID = 888;
    public static final String BUG_ID = "bug-id";
    private final String TAG = this.getClass().getSimpleName();


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
        setBugAdapter();
        //db query for mybugs recycler view
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
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setBugAdapter() {
        mBugsRV = mRootview.findViewById(R.id.bugs_rv);
        mBugAdapter = new BugAdapter(getActivity(), this );
        //1 column for phone, 3 column for tablet
        int columnsNum = 2;
        /*if (getResources().getConfiguration().screenWidthDp >= 700){
            columnsNum = 3;
        }*/
        mLayoutManager = new GridLayoutManager(getActivity(),columnsNum );
        mBugsRV.setLayoutManager(mLayoutManager);
        mBugsRV.setAdapter(mBugAdapter);
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
        /*if there's no data show error msg
        if (mBugsList == null) {
            errorTV.setText(R.string.no_data);
            showErrorMsg(true);
        } else {
            showErrorMsg(false);
        }*/
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mBugAdapter.swapCursor(null);
        Log.d (TAG, "onLoaderReset");
    }

    //db re-query for mybugs recycler view
    // to be called from activity when new bug is added to db
    public void refreshData(){
        Log.d (TAG, "refreshing data") ;
        getActivity().getSupportLoaderManager().restartLoader(DB_QUERY_LOADER_ID, null, this );
    }


}
