package com.app.runners.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.runners.R;
import com.app.runners.utils.AppController;
import com.app.runners.utils.ColorHelper;

/**
 * Created by sergiocirasa on 16/8/17.
 */

public class StateButton extends LinearLayout {

    private TextView mTitleTextView;
    private TextView mSubtitleTextView;
    private View mMainView;
    private @ColorInt int mColorPrimary;

    public StateButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        mMainView = inflater.inflate(R.layout.state_button, this);
        mTitleTextView = (TextView) mMainView.findViewById(R.id.title);
        mSubtitleTextView = (TextView) mMainView.findViewById(R.id.subtitle);
        mColorPrimary = ColorHelper.getPrimaryColorFromTheme(context.getTheme());

        //get the attributes specified in attrs.xml using the name we included
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.StateButton, 0, 0);

        if(a.getString(R.styleable.StateButton_title)!=null)
            mTitleTextView.setText(a.getString(R.styleable.StateButton_title));

        if(a.getString(R.styleable.StateButton_title)!=null)
            mSubtitleTextView.setText(a.getString(R.styleable.StateButton_subtitle));
    }

    public void setSubtitleText(String subtitle){
        mSubtitleTextView.setText(subtitle);
    }

    public void select(boolean selected){
/*
        if(selected){
            mMainView.setBackground(AppController.getInstance().getResources().getDrawable(R.drawable.state_button_selected));
            GradientDrawable drawable = (GradientDrawable)mMainView.getBackground();
            if(drawable!=null)
                drawable.setStroke(3, mColorPrimary);

            mTitleTextView.setTextColor(mColorPrimary);
            mSubtitleTextView.setTextColor(mColorPrimary);
        }else{
            mMainView.setBackgroundColor(mColorPrimary);
            mTitleTextView.setTextColor(AppController.getInstance().getResources().getColor(R.color.colorWhite));
            mSubtitleTextView.setTextColor(AppController.getInstance().getResources().getColor(R.color.colorWhite));
        }
*/

        if(selected){
            mMainView.setBackground(AppController.getInstance().getResources().getDrawable(R.drawable.state_button_selected));
            //mMainView.setBackgroundColor(mColorPrimary);
            GradientDrawable drawable = (GradientDrawable)mMainView.getBackground();
            if(drawable!=null) {
                drawable.setStroke(3, mColorPrimary);
                drawable.setColor(mColorPrimary);
            }

            mTitleTextView.setTextColor(getResources().getColor(R.color.colorWhite));
            mSubtitleTextView.setTextColor(getResources().getColor(R.color.colorWhite));
        }else{
            mMainView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            mTitleTextView.setTextColor(mColorPrimary);
            mSubtitleTextView.setTextColor(mColorPrimary);
        }
    }
}
