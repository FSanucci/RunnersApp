package com.app.runners.adapter;

import android.animation.Animator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.app.runners.R;
import com.app.runners.fragment.PersonalInfoFragment;
import com.app.runners.interfaces.Item;
import com.app.runners.model.Comment;
import com.app.runners.rest.RestConstants;
import com.app.runners.utils.Storage;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;


public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder>{

    private final static String WALLCHANGES = "WALL_CHANGES";
    private final static String PLANCHANGES = "PLAN_CHANGES";

    private final Context mContext;
    private final List<? extends Item> mValues;
    private final String RUNNER = "Runner";
    private SimpleDraweeView mPreview;
    private boolean zoom = false;
    private static final int REQUEST_IMAGE = 1;
    private Animation animation,animation2;

    private OnImageClickListener mImageClickListener;
    private OnImageLongClickListener mImageLongClickListener;
    private OnNotificationClickListener mNotificationClickListener;

    public ItemRecyclerViewAdapter(List<? extends Item> items, Context newContext) {
        mValues = items;
        mContext = newContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        if(mValues.get(position).getTitle() == null){
            holder.mTitleTextView.setVisibility(View.GONE);
        }else {
            holder.mTitleTextView.setVisibility(View.VISIBLE);
            holder.mTitleTextView.setText(mValues.get(position).getTitle());
        }

        if(mValues.get(position).getSubtitle() == null){
            holder.mSubtitleTextView.setVisibility(View.GONE);
        }else {
            holder.mSubtitleTextView.setVisibility(View.VISIBLE);
            holder.mSubtitleTextView.setText(mValues.get(position).getSubtitle());
        }

        if(mValues.get(position).getDate() == null){
            if(mValues.get(position).getTitle() != null) {
                holder.mExtraMargin.setVisibility(View.VISIBLE);
            }else{
                holder.mExtraMargin.setVisibility(View.GONE);
            }
            holder.mDateTextView.setVisibility(View.GONE);
        }else {
            holder.mExtraMargin.setVisibility(View.GONE);
            holder.mDateTextView.setVisibility(View.VISIBLE);
            holder.mDateTextView.setText(mValues.get(position).getDate());
        }

        if (mValues.get(position).isComunity()){
            fillComunityMessage(holder, position);
        } else {
            fillMessage(holder, position);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mItem.isNotification()){
                    mNotificationClickListener.onNotificationClickListener(holder.mItem.getContentType());
                }
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final View mExtraMargin;
        public final TextView mTitleTextView;
        public final TextView mSubtitleTextView;
        public final TextView mDateTextView;
        public final SimpleDraweeView mImageView;
        public Item mItem;
        public final RelativeLayout container;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            container = (RelativeLayout) view.findViewById(R.id.main_container);

            mTitleTextView = (TextView) view.findViewById(R.id.title);
            mSubtitleTextView = (TextView) view.findViewById(R.id.subtitle);
            mDateTextView = (TextView) view.findViewById(R.id.date);
            mExtraMargin = view.findViewById(R.id.extra_margin);
            mImageView = view.findViewById(R.id.image);
            Drawable image = view.getContext().getResources().getDrawable(R.drawable.background_image);
            mImageView.setBackground(image);
        }
    }

    public void setImageClickListener(OnImageClickListener listener){
        mImageClickListener = listener;
    }

    public void setImageLongClickListener(OnImageLongClickListener listener){
        mImageLongClickListener = listener;
    }

    public void setNotificationClickListener(OnNotificationClickListener listener){
        mNotificationClickListener = listener;
    }

    public interface OnImageClickListener{
        void onImageClickListener(Comment data);
    }

    public interface OnImageLongClickListener{
        void onImageLongClickListener(Comment data);
    }

    public interface OnNotificationClickListener{
        void onNotificationClickListener(String contentType);
    }

    public void fillMessage(ViewHolder holder, int position){
/*
        if(mValues.get(position).getTitle() == null){
            holder.mTitleTextView.setVisibility(View.GONE);
        }else {
            holder.mTitleTextView.setVisibility(View.VISIBLE);
            holder.mTitleTextView.setText(mValues.get(position).getTitle());
        }

        if(mValues.get(position).getSubtitle() == null){
            holder.mSubtitleTextView.setVisibility(View.GONE);
        }else {
            holder.mSubtitleTextView.setVisibility(View.VISIBLE);
            holder.mSubtitleTextView.setText(mValues.get(position).getSubtitle());
        }

        if(mValues.get(position).getDate() == null){
            if(mValues.get(position).getTitle() != null) {
                holder.mExtraMargin.setVisibility(View.VISIBLE);
            }else{
                holder.mExtraMargin.setVisibility(View.GONE);
            }
            holder.mDateTextView.setVisibility(View.GONE);
        }else {
            holder.mExtraMargin.setVisibility(View.GONE);
            holder.mDateTextView.setVisibility(View.VISIBLE);
            holder.mDateTextView.setText(mValues.get(position).getDate());
        }
*/
        if (mValues.get(position).getResource() == null){
            holder.mImageView.setVisibility(View.GONE);
        } else {
            holder.mImageView.setVisibility(View.VISIBLE);
            String path = RestConstants.IMAGE_HOST + mValues.get(position).getResource();

            Uri uri1 = Uri.parse(path);
            if (uri1 != null){
                holder.mImageView.setImageURI(uri1);
            }

            holder.mImageView.setTag(mValues.get(position));

            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Comment data = (Comment) v.getTag();
                    if (mImageClickListener != null){
                        mImageClickListener.onImageClickListener(data);
                    }
                }
            });

            holder.mImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    Comment data = (Comment) v.getTag();
                    if (mImageLongClickListener != null){
                        mImageLongClickListener.onImageLongClickListener(data);
                    }
                    return true;
                }
            });
        }

        try {
            if (((Comment) holder.mItem).getAutor().equals(RUNNER)){

                holder.container.setBackgroundResource(R.drawable.chat_runner_rev);

                RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                containerParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                containerParams.setMargins(50, 0, 0, 0);
                holder.container.setLayoutParams(containerParams);

                RelativeLayout.LayoutParams dateParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                dateParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                dateParams.setMargins(0, 40, 70, 30);
                holder.mDateTextView.setLayoutParams(dateParams);

                RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                titleParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                titleParams.setMargins(0, 70, 70, 30);
                holder.mTitleTextView.setLayoutParams(titleParams);

                RelativeLayout.LayoutParams subtitleParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                subtitleParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                subtitleParams.setMargins(60, 120, 70, 30);
                holder.mSubtitleTextView.setLayoutParams(subtitleParams);


            } else {

                holder.container.setBackgroundResource(R.drawable.chat_coach_rev);

                RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                containerParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                containerParams.setMargins(0, 0, 50, 0);
                holder.container.setLayoutParams(containerParams);

                RelativeLayout.LayoutParams dateParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                dateParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                dateParams.setMargins(70, 40, 0, 30);
                holder.mDateTextView.setLayoutParams(dateParams);

                RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                titleParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                titleParams.setMargins(70, 70, 0, 30);
                holder.mTitleTextView.setLayoutParams(titleParams);

                RelativeLayout.LayoutParams subtitleParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                subtitleParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                subtitleParams.setMargins(70, 120, 60, 30);
                holder.mSubtitleTextView.setLayoutParams(subtitleParams);

            }
        } catch (Exception e){
            Log.e("ERROR", e.toString());
        }
    }

    public void fillComunityMessage(ViewHolder holder, int position){
/*
        if(mValues.get(position).getTitle() == null){
            holder.mTitleTextView.setVisibility(View.GONE);
        }else {
            holder.mTitleTextView.setVisibility(View.VISIBLE);
            holder.mTitleTextView.setText(mValues.get(position).getTitle());
        }

        if(mValues.get(position).getSubtitle() == null){
            holder.mSubtitleTextView.setVisibility(View.GONE);
        }else {
            holder.mSubtitleTextView.setVisibility(View.VISIBLE);
            holder.mSubtitleTextView.setText(mValues.get(position).getSubtitle());
        }

        if(mValues.get(position).getDate() == null){
            if(mValues.get(position).getTitle() != null) {
                holder.mExtraMargin.setVisibility(View.VISIBLE);
            }else{
                holder.mExtraMargin.setVisibility(View.GONE);
            }
            holder.mDateTextView.setVisibility(View.GONE);
        }else {
            holder.mExtraMargin.setVisibility(View.GONE);
            holder.mDateTextView.setVisibility(View.VISIBLE);
            holder.mDateTextView.setText(mValues.get(position).getDate());
        }
*/
        if (mValues.get(position).getResource() == null){
            holder.mImageView.setVisibility(View.GONE);
        }

        try {
            if (((Comment)holder.mItem).isOwner()){
                holder.container.setBackgroundResource(R.drawable.chat_runner_rev);

                RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                containerParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                containerParams.setMargins(50, 0, 0, 0);
                holder.container.setLayoutParams(containerParams);

                RelativeLayout.LayoutParams dateParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                dateParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                dateParams.setMargins(0, 40, 70, 30);
                holder.mDateTextView.setLayoutParams(dateParams);

                RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                titleParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                titleParams.setMargins(0, 70, 70, 30);
                holder.mTitleTextView.setLayoutParams(titleParams);

                RelativeLayout.LayoutParams subtitleParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                subtitleParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                subtitleParams.setMargins(60, 120, 70, 30);
                holder.mSubtitleTextView.setLayoutParams(subtitleParams);

            } else {
                if (((Comment)holder.mItem).isCoach()){
                    holder.container.setBackgroundResource(R.drawable.chat_coach_rev);
                }
                else {
                    holder.container.setBackgroundResource(R.drawable.chat_runner);
                }

                RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                containerParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                containerParams.setMargins(0, 0, 50, 0);
                holder.container.setLayoutParams(containerParams);

                RelativeLayout.LayoutParams dateParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                dateParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                dateParams.setMargins(70, 40, 0, 30);
                holder.mDateTextView.setLayoutParams(dateParams);

                RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                titleParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                titleParams.setMargins(70, 70, 0, 30);
                holder.mTitleTextView.setLayoutParams(titleParams);

                RelativeLayout.LayoutParams subtitleParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                subtitleParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                subtitleParams.setMargins(70, 120, 60, 30);
                holder.mSubtitleTextView.setLayoutParams(subtitleParams);
            }



/*
            if (((Comment) holder.mItem).getAutor().equals(RUNNER)){

                holder.container.setBackgroundResource(R.drawable.chat_runner_rev);

                RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                containerParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                containerParams.setMargins(50, 0, 0, 0);
                holder.container.setLayoutParams(containerParams);

                RelativeLayout.LayoutParams dateParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                dateParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                dateParams.setMargins(0, 40, 70, 30);
                holder.mDateTextView.setLayoutParams(dateParams);

                RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                titleParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                titleParams.setMargins(0, 70, 70, 30);
                holder.mTitleTextView.setLayoutParams(titleParams);

                RelativeLayout.LayoutParams subtitleParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                subtitleParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                subtitleParams.setMargins(60, 120, 70, 30);
                holder.mSubtitleTextView.setLayoutParams(subtitleParams);


            } else {

                holder.container.setBackgroundResource(R.drawable.chat_coach_rev);

                RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                containerParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                containerParams.setMargins(0, 0, 50, 0);
                holder.container.setLayoutParams(containerParams);

                RelativeLayout.LayoutParams dateParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                dateParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                dateParams.setMargins(70, 40, 0, 30);
                holder.mDateTextView.setLayoutParams(dateParams);

                RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                titleParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                titleParams.setMargins(70, 70, 0, 30);
                holder.mTitleTextView.setLayoutParams(titleParams);

                RelativeLayout.LayoutParams subtitleParams = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.WRAP_CONTENT),(RelativeLayout.LayoutParams.WRAP_CONTENT));
                subtitleParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                subtitleParams.setMargins(70, 120, 60, 30);
                holder.mSubtitleTextView.setLayoutParams(subtitleParams);

            }
*/
        } catch (Exception e){
            Log.e("ERROR", e.toString());
        }
    }

}
