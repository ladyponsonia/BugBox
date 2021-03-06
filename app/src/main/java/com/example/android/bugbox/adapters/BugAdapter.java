package com.example.android.bugbox.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bugbox.R;
import com.example.android.bugbox.model.Bug;
import com.example.android.bugbox.model.Bug3D;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.example.android.bugbox.contentProvider.BugsContract.BugEntry;

public class BugAdapter extends RecyclerView.Adapter<BugAdapter.BugViewHolder> {

    private final String TAG = this.getClass().getSimpleName();

    private Cursor mCursor;
    private Context mContext;
    private final BugOnClickHandler mClickHandler;

    public BugAdapter(Context context, BugOnClickHandler clickHandler){
        mContext = context;
        mClickHandler = clickHandler;
    }

    public interface BugOnClickHandler {
        void onClick(int adapterPosition); //on click method will be implemented in BugsActivity
    }

    @NonNull
    @Override
    public BugAdapter.BugViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate bug_grid_cell layout
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.bug_grid_cell, parent, false);
        //return BugViewHolder instance
        BugViewHolder viewholder = new BugViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull BugAdapter.BugViewHolder holder, int position) {
        // Move the mCursor to the position of the item to be displayed
        if (!mCursor.isClosed()) {
            if (!mCursor.moveToPosition(position)) {
                return;// bail if returned null
            }

            Log.d(TAG, "OnBindViewHolder, cursor position: " + position);
            //display data from cursor
            String bugName = mCursor.getString(mCursor.getColumnIndex(BugEntry.COLUMN_NAME));
            String bugThumbUrl = mCursor.getString(mCursor.getColumnIndex(BugEntry.COLUMN_THUMBNAIL));
            int bugId = mCursor.getInt(mCursor.getColumnIndex(BugEntry._ID));

            Log.d(TAG, bugName + " " + bugThumbUrl + " " + bugId);

            //set id if not empty
            if (bugId != 0) {
                //set tag. will be used to delete item
                holder.itemView.setTag(bugId);
            }

            //set name if not empty
            if (!bugName.isEmpty()) {
                holder.mName.setText(bugName);
            }

            //set image
            int size = (int)mContext.getResources().getDimension(R.dimen.bug_image_size);
            Picasso.with(mContext).setLoggingEnabled(true);//to see logs
            if (bugThumbUrl.isEmpty()) {
                Log.d(TAG, "using default image");
                Picasso.with(mContext)
                        .load(R.drawable.placeholder_image)
                        .resize(size, size)
                        .into(holder.mImage);
            } else {
                Picasso.with(mContext)
                        .load(bugThumbUrl)
                        .resize(size, size)
                        .placeholder(R.drawable.placeholder_image)
                        .into(holder.mImage);
            }
        }

    }

    @Override
    public int getItemCount () {
        if (null == mCursor){
            Log.d(TAG, "getItemCount: null cursor" );
            return 0;
        }
        Log.d(TAG, "getItemCount: " + mCursor.getCount() );
        return mCursor.getCount();

    }

    class BugViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView mImage;
        public TextView mName;

        public BugViewHolder(View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.bug_iv);
            mName = itemView.findViewById(R.id.bug_tv);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = (int) view.getTag();
            Log.d(TAG, String.valueOf(id));
            mClickHandler.onClick(id);
        }
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }


}