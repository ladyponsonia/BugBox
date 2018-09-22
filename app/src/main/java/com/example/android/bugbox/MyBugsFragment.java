package com.example.android.bugbox;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bugbox.adapters.BugAdapter;
import com.example.android.bugbox.model.Bug;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * interface
 * to handle interaction events.
 * Use the {@link MyBugsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyBugsFragment extends Fragment implements BugAdapter.BugOnClickHandler {

    private View mRootview;
    private RecyclerView mBugsRV;
    private BugAdapter mBugAdapter;
    private ArrayList<Bug> mBugsList;
    private LayoutManager mLayoutManager;

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

        mBugsList = MainActivity.bugsList;
        setBugAdapter(mBugsList);
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

    private void setBugAdapter(ArrayList<Bug> bugsList
            //, Bundle savedInstanceState
    ) {
        mBugsRV = mRootview.findViewById(R.id.bugs_rv);
        mBugAdapter = new BugAdapter(getActivity(),bugsList, this);
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
    public void onClick(Bug bug) {

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
}
