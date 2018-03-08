package com.example.gyatsina.firstapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.gyatsina.firstapp.logger.DebugLogger;
import com.example.gyatsina.firstapp.network.StampObj;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by gyatsina on 3/2/2018.
 */

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {
    private Context mContext;
    private List<StampObj> mDataset;
    private OnGridClickListener mOnGridClickListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mImageView;

        public ViewHolder(ImageView v) {
            super(v);
            mImageView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public GridAdapter(Context c, List<StampObj> myDataset, OnGridClickListener onGridClickListener) {
        mContext = c;
        mDataset = myDataset;
        mOnGridClickListener = onGridClickListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        ImageButton v = (ImageButton) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stamp_button, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Picasso.with(mContext).load(mDataset.get(position).getImage()).into(holder.mImageView);
        holder.mImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DebugLogger.e("==========", "CLICK");
                mOnGridClickListener.onItemClick(mDataset.get(position));
            }
        });
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}