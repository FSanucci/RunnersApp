package com.app.runners.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.runners.R;
import com.app.runners.interfaces.Item;
import com.app.runners.model.Link;

import java.util.List;

/**
 * Created by sergiocirasa on 14/9/17.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private final List<Link> mValues;

    public VideoAdapter(List<Link> items) {
        mValues = items;
    }

    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_link, parent, false);
        return new VideoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VideoAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        if(mValues.get(position).title == null){
            holder.mTitleTextView.setVisibility(View.GONE);
        }else {
            holder.mTitleTextView.setVisibility(View.VISIBLE);
            if(mValues.get(position).text!=null && mValues.get(position).text.length()>0)
                holder.mTitleTextView.setText(mValues.get(position).text);
            else holder.mTitleTextView.setText(mValues.get(position).title);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleTextView;
        public Link mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleTextView = (TextView) view.findViewById(R.id.title);
        }
    }
}
