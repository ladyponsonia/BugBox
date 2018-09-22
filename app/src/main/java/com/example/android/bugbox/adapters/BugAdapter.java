package com.example.android.bugbox.adapters;

import android.content.Context;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BugAdapter extends RecyclerView.Adapter<BugAdapter.BugViewHolder> {

    private final String TAG = this.getClass().getSimpleName();

    private ArrayList<Bug> mBugsData;
    private Context mContext;
    private final BugOnClickHandler mClickHandler;

    public BugAdapter(Context context, ArrayList<Bug> bugsData, BugOnClickHandler clickHandler){
        mContext = context;
        mBugsData = bugsData;
        mClickHandler = clickHandler;
    }

    public interface BugOnClickHandler {
        void onClick(Bug bug); //on click method will be implemented in BugsActivity
    }

    @NonNull
    @Override
    public BugAdapter.BugViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate bug_grid_cell layout
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.bug_grid_cell, parent, false);
        //return BugViewHolder instance
        BugViewHolder viewholder = new BugViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull BugAdapter.BugViewHolder holder, int position) {
        Bug bug = mBugsData.get(position);

        //set name if not empty
        if(!bug.getName().isEmpty()) {
            holder.mName.setText(bug.getName());
        }

        /*set image
        Picasso.with(mContext).setLoggingEnabled(true);//to see logs
        String imagePath = bug.getImage();
        if (imagePath.isEmpty()){
            Log.d(TAG, "using default image");*/
            Picasso.with(mContext)
                    .load(R.drawable.ic_launcher_foreground)
                    .into(holder.mImage);
        /*}else {
            Picasso.with(mContext)
                    .load(imagePath)
                    .resize(555,831)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.mImage);
        }*/
    }

    @Override
    public int getItemCount() {
        if (mBugsData == null) return 0;
        return mBugsData.size();
    }

    public class BugViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

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
            int adapterPosition = getAdapterPosition();
            Bug bug = mBugsData.get(adapterPosition);
            mClickHandler.onClick(bug);
        }
    }

    public void setRecipeData(ArrayList<Bug> bugData) {
        mBugsData = bugData;
        notifyDataSetChanged();
    }


}
