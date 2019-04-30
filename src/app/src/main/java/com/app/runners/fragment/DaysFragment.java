package com.app.runners.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.runners.R;
import com.app.runners.model.DayPlan;
import com.app.runners.utils.Constants;
import com.app.runners.utils.IntentHelper;


public class DaysFragment extends Fragment {

    private static final int DAY1 = 1;
    private static final int DAY2 = 2;
    private static final String ARG_DAY1 = "day_plan1";
    private static final String ARG_DAY2 = "day_plan2";
    private static final String ARG_WEEK = "week";
    private static final String ARG_DAY1_NUMBER = "day1";
    private static final String ARG_DAY2_NUMBER = "day2";
    private DayPlan mDayPlan1;
    private DayPlan mDayPlan2;

    private View mContainer1View;
    private TextView mTitle1TextView;
    private TextView mBody1TextView;
    private TextView mKms1TextView;
    private TextView mComment1TextView;
    private ImageView mComment1ButtonTextView;

    private View mContainer2View;
    private TextView mTitle2TextView;
    private TextView mBody2TextView;
    private TextView mKms2TextView;
    private TextView mComment2TextView;
    private ImageView mComment2ButtonTextView;
    private int mWeek;
    private int mDay1;
    private int mDay2;

    public DaysFragment() {
        // Required empty public constructor
    }

    public static DaysFragment newInstance(DayPlan dayPlan1,DayPlan dayPlan2, int week, int day1, int day2) {
        DaysFragment fragment = new DaysFragment();
        Bundle param = new Bundle();
        param.putSerializable(ARG_DAY1, dayPlan1);
        param.putSerializable(ARG_DAY2, dayPlan2);
        param.putSerializable(ARG_WEEK, week);
        param.putSerializable(ARG_DAY1_NUMBER, day1);
        param.putSerializable(ARG_DAY2_NUMBER, day2);
        fragment.setArguments(param);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDayPlan1 = (DayPlan) getArguments().getSerializable(ARG_DAY1);
            mDayPlan2 = (DayPlan) getArguments().getSerializable(ARG_DAY2);
            mWeek = (int) getArguments().getSerializable(ARG_WEEK);
            mDay1 = (int) getArguments().getSerializable(ARG_DAY1_NUMBER);
            mDay2 = (int) getArguments().getSerializable(ARG_DAY2_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_days, container, false);

        mContainer1View = v.findViewById(R.id.container1);
        mTitle1TextView = (TextView) v.findViewById(R.id.title1);
        mBody1TextView = (TextView) v.findViewById(R.id.body1);
        mKms1TextView = (TextView) v.findViewById(R.id.km1);
        mComment1TextView = (TextView) v.findViewById(R.id.comment1);
        mComment1ButtonTextView = (ImageView) v.findViewById(R.id.comment_buttom1);

        if(mDayPlan1!=null) {
           // mTitle1TextView.setText(getActivity().getString(R.string.training_plan_day) + " " + mDayPlan1.dayNumber + " " + mDayPlan1.day);
            mTitle1TextView.setText(getActivity().getString(R.string.training_plan_day) + " " + mDayPlan1.day);
            mBody1TextView.setText(mDayPlan1.description);
            mKms1TextView.setText(mDayPlan1.kms);
            if(mDayPlan1.comments != null)
                mComment1TextView.setText(mDayPlan1.comments);
        }else{
            mContainer1View.setVisibility(View.INVISIBLE);
        }

        mContainer2View = v.findViewById(R.id.container2);
        mTitle2TextView = (TextView) v.findViewById(R.id.title2);
        mBody2TextView = (TextView) v.findViewById(R.id.body2);
        mKms2TextView = (TextView) v.findViewById(R.id.km2);
        mComment2TextView = (TextView) v.findViewById(R.id.comment2);
        mComment2ButtonTextView = (ImageView) v.findViewById(R.id.comment_buttom2);

        if(mDayPlan2!=null) {
           // mTitle2TextView.setText(getActivity().getString(R.string.training_plan_day) + " " + mDayPlan2.dayNumber + " " + mDayPlan2.day);
            mTitle2TextView.setText(getActivity().getString(R.string.training_plan_day) + " " + mDayPlan2.day);
            mBody2TextView.setText(mDayPlan2.description);
            mKms2TextView.setText(mDayPlan2.kms);
            if(mDayPlan2.comments != null)
                mComment2TextView.setText(mDayPlan2.comments);
        }else{
            mContainer2View.setVisibility(View.INVISIBLE);
        }

        setupListeners();
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setupListeners(){
        mComment1ButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.DAY,mDay1);
                bundle.putInt(Constants.WEEK,mWeek);

                if(mDayPlan1.comments!=null)
                    bundle.putString(Constants.COMMENT, mDayPlan1.comments);
                IntentHelper.gotoAddCommentActivity(DaysFragment.this,bundle,DAY1);
            }
        });

        mComment2ButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.DAY,mDay2);
                bundle.putInt(Constants.WEEK,mWeek);

                if(mDayPlan2.comments!=null)
                    bundle.putString(Constants.COMMENT, mDayPlan2.comments);
                IntentHelper.gotoAddCommentActivity(DaysFragment.this,bundle,DAY2);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == DAY1 && data.getStringExtra(Constants.COMMENT) != null) {
                mDayPlan1.comments = data.getStringExtra(Constants.COMMENT);
                mComment1TextView.setText(mDayPlan1.comments);
            } else if (requestCode == DAY2 && data.getStringExtra(Constants.COMMENT) != null) {
                mDayPlan2.comments = data.getStringExtra(Constants.COMMENT);
                mComment2TextView.setText(mDayPlan2.comments);
            }
        }
    }
}
