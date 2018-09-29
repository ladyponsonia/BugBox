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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * interface
 * to handle interaction events.
 * Use the {@link MyBugsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyBugsFragment extends Fragment implements BugAdapter.BugOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    private View mRootview;
    private RecyclerView mBugsRV;
    private BugAdapter mBugAdapter;
    private LayoutManager mLayoutManager;


    private static final int DB_QUERY_LOADER_ID = 888;
    public static final String BUG_ID = "bug-id";
    private final String TAG = this.getClass().getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

   // private OnFragmentInteractionListener mListener;

    public MyBugsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyBugsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyBugsFragment newInstance(String param1, String param2) {
        MyBugsFragment fragment = new MyBugsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

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
                // Restart the loader
                getActivity().getSupportLoaderManager().restartLoader(DB_QUERY_LOADER_ID,
                        null, MyBugsFragment.this);

            }
        }).attachToRecyclerView(mBugsRV);
        return mRootview;
    }

    // TODO: Rename method, update argument and hook method into UI event
    /*
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/

    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    private void setBugAdapter(
            //, Bundle savedInstanceState
    ) {
        mBugsRV = mRootview.findViewById(R.id.bugs_rv);
        mBugAdapter = new BugAdapter(getActivity(), this );
        //1 column for phone, 3 column for tablet
        int columnsNum = 2;
        /*if (getResources().getConfiguration().screenWidthDp >= 700){
            columnsNum = 3;
        }*/
        mLayoutManager = new GridLayoutManager(getActivity(),columnsNum );
        mBugsRV.setLayoutManager(mLayoutManager);
        /*get mListState
        //with help from https://stackoverflow.com/questions/47110168/layoutmanager-onsaveinstancestate-not-working
        if (savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable(LIST_STATE_KEY);
            mainRV.getLayoutManager().onRestoreInstanceState(mListState);
        }*/
        mBugsRV.setAdapter(mBugAdapter);
    }

    @Override
    public void onClick(int adapterPosition) {
        Intent ARIntent = new Intent(getActivity(), ARActivity.class);
        ARIntent.putExtra(BUG_ID, adapterPosition );
        startActivity(ARIntent);
    }







    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/

    @Override
    public Loader onCreateLoader(int id, Bundle bundle) {

        String[] mProjection = {BugEntry.COLUMN_NAME, BugEntry.COLUMN_THUMBNAIL};

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
