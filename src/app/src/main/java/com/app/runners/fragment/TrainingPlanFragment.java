package com.app.runners.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.runners.R;
import com.app.runners.activity.VideoActivity;
import com.app.runners.adapter.DaysPageAdapter;
import com.app.runners.adapter.VideoAdapter;
import com.app.runners.interfaces.ClickListener;
import com.app.runners.manager.UserController;
import com.app.runners.model.DayPlan;
import com.app.runners.model.Link;
import com.app.runners.model.User;
import com.app.runners.model.WeekPlan;
import com.app.runners.utils.ColorHelper;
import com.app.runners.utils.RecyclerTouchListener;
import com.app.runners.view.StateButton;
import com.app.runners.view.WrapContentViewPager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;


public class TrainingPlanFragment extends Fragment {

    private static final String TAG = "TRAIN_PLAN_FRG";

    private LinearLayout mButtonContainer;
    private StateButton mWeek1Button;
    private StateButton mWeek2Button;
    private StateButton mWeek3Button;
    private StateButton mWeek4Button;
    private StateButton mWeek5Button;
    private StateButton mWeek6Button;

    private WrapContentViewPager mViewPager;
    private DaysPageAdapter mAdapter;
    private ImageView mBackImageView;
    private ImageView mNextImageView;

    private TextView mPlanNameTextView;
    private TextView mFromDateTextView;
    private TextView mToDateTextView;
    private TextView mNumberOfWeeksTextView;
    private TextView mStimuliTextView;

    private TextView mMacrocicloTextView;
    private TextView mMesocicloTextView;
    private TextView mMicrocicloTextView;
    private TextView mWeek_nameTextView;
    private TextView mWeekInfoTextView;
    private TextView mKmsTotalTextView;
    private TextView mReferencesTitleTextView;
    private TextView mReferencesTextView;
    private VideoAdapter mVideoAdapter;

    private TextView mPeriod_descrTextView;
    private TextView mGoals_descrTextView;

    private TextView mVideoTitleTextView;
    private RecyclerView mRecyclerView;

    private View mNoContentView;
    private View mContentView;
    private int mWeekSelected = -1;
    private boolean mViewInitialized = false;
    private boolean mFirstTime = true;
    private @ColorInt int mColorPrimary;
    private ArrayList<WeekPlan> mWeekplans;
    private int counterWeeksOffset = 0;

    private String fromDateBase = "";
    private static final String DATE_COMPLETE_FORMAT = "dd-MM-yyyy";
    private static final String DATE_REDUCE_FORMAT = "dd/MM";


    public TrainingPlanFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_training_plan, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (WrapContentViewPager) view.findViewById(R.id.pager);

        mButtonContainer = (LinearLayout) view.findViewById(R.id.button_container);
        mWeek1Button = (StateButton) view.findViewById(R.id.week1);
        mWeek2Button = (StateButton) view.findViewById(R.id.week2);
        mWeek3Button = (StateButton) view.findViewById(R.id.week3);
        mWeek4Button = (StateButton) view.findViewById(R.id.week4);
        mWeek5Button = (StateButton) view.findViewById(R.id.week5);
        mWeek6Button = (StateButton) view.findViewById(R.id.week6);

        mBackImageView = (ImageView) view.findViewById(R.id.back);
        mNextImageView = (ImageView) view.findViewById(R.id.next);

        mPlanNameTextView = (TextView) view.findViewById(R.id.plan_name);
        mFromDateTextView = (TextView) view.findViewById(R.id.from);
        mToDateTextView = (TextView) view.findViewById(R.id.to);
        mNumberOfWeeksTextView = (TextView) view.findViewById(R.id.number_weeks);
        mStimuliTextView = (TextView) view.findViewById(R.id.stimuli);

        mMacrocicloTextView = (TextView) view.findViewById(R.id.macrociclo);
        mMesocicloTextView = (TextView) view.findViewById(R.id.mesociclo);
        mMicrocicloTextView = (TextView) view.findViewById(R.id.microciclo);
        mWeek_nameTextView = (TextView) view.findViewById(R.id.week_name);
        mWeekInfoTextView = (TextView) view.findViewById(R.id.week_info);
        mKmsTotalTextView = (TextView) view.findViewById(R.id.kms_total);
        mReferencesTextView = (TextView) view.findViewById(R.id.references);
        mReferencesTitleTextView = (TextView) view.findViewById(R.id.references_title);
        mVideoTitleTextView = (TextView) view.findViewById(R.id.videos_title);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mPeriod_descrTextView = (TextView) view.findViewById(R.id.period_description);
        mGoals_descrTextView = (TextView) view.findViewById(R.id.goals_description);

        mNoContentView = view.findViewById(R.id.no_content);
        mContentView = view.findViewById(R.id.scroll);

        selectWeek(mWeek1Button);
        mColorPrimary = ColorHelper.getPrimaryColorFromTheme(getActivity().getTheme());
        setupListeners();
        refreshContent();
        mViewInitialized = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mViewInitialized && !mFirstTime){
            refreshContent();
        }
        mFirstTime = false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private Hashtable mapWeekOrder;
    private Hashtable loadMapWeekOrder(User user){
        Hashtable mapWeekOrder = new Hashtable();
        int counter = 1;
        if (user.plan.weeks.size()>0 && user.plan.weeks.get(0).isVisible){
            mapWeekOrder.put(1,counter);
            counter++;
        }
        if (user.plan.weeks.size()>1 && user.plan.weeks.get(1).isVisible){
            mapWeekOrder.put(2,counter);
            counter++;
        }

        if (user.plan.weeks.size()>2 && user.plan.weeks.get(2).isVisible){
            mapWeekOrder.put(3,counter);
            counter++;
        }

        if (user.plan.weeks.size()>3 && user.plan.weeks.get(3).isVisible){
            mapWeekOrder.put(4,counter);
            counter++;
        }

        if (user.plan.weeks.size()>4 && user.plan.weeks.get(4).isVisible){
            mapWeekOrder.put(5,counter);
            counter++;
        }

        if (user.plan.weeks.size()>5 && user.plan.weeks.get(5).isVisible){
            mapWeekOrder.put(6,counter);
            counter++;
        }
        return mapWeekOrder;
    }

    public void refreshContent() {
        final User user = UserController.getInstance().getSignedUser();

        if(user.plan == null){
            mNoContentView.setVisibility(View.VISIBLE);
            mContentView.setVisibility(View.GONE);
            return;
        }else{
            mNoContentView.setVisibility(View.GONE);
            mContentView.setVisibility(View.VISIBLE);
        }

        if(user.plan.name!=null){
            mPlanNameTextView.setText(user.plan.name);
        }else mPlanNameTextView.setText("");

        if(user.plan.fromDate!=null) {
            mFromDateTextView.setText(user.plan.fromDate);
            fromDateBase = user.plan.fromDate;
        } else mFromDateTextView.setText("");

        if(user.plan.toDate!=null)
            mToDateTextView.setText(user.plan.toDate);
        else mToDateTextView.setText("");

        if(user.plan.weekNumberDescription!=null)
            mNumberOfWeeksTextView.setText(user.plan.weekNumberDescription);
        else mNumberOfWeeksTextView.setText("");

        if(user.plan.stimuli!=null)
            mStimuliTextView.setText(user.plan.stimuli);
        else mStimuliTextView.setText("");

        if(user.plan.macroCycle!=null)
            mMacrocicloTextView.setText(user.plan.macroCycle);
        else mMacrocicloTextView.setText("");

        if(user.plan.mesoCycle!=null)
            mMesocicloTextView.setText(user.plan.mesoCycle);
        else mMesocicloTextView.setText("");

        if(user.plan.microCycle!=null)
            mMicrocicloTextView.setText(user.plan.microCycle);
        else mMicrocicloTextView.setText("");

        if (user.plan.period != null)
            mPeriod_descrTextView.setText(user.plan.period);
        else mPeriod_descrTextView.setText("");

        if (user.plan.goals != null)
            mGoals_descrTextView.setText(user.plan.goals);
        else mGoals_descrTextView.setText("");

        if(user.plan.references!=null) {
            mReferencesTextView.setVisibility(View.VISIBLE);
            mReferencesTitleTextView.setVisibility(View.VISIBLE);
            mReferencesTextView.setText(user.plan.references);
        }else {
            mReferencesTextView.setVisibility(View.GONE);
            mReferencesTitleTextView.setVisibility(View.GONE);
            mReferencesTextView.setText("");
        }
        mWeek1Button.setVisibility(View.VISIBLE);
        mWeek2Button.setVisibility(View.VISIBLE);
        mWeek3Button.setVisibility(View.VISIBLE);
        mWeek4Button.setVisibility(View.VISIBLE);
        mWeek5Button.setVisibility(View.VISIBLE);
        mWeek6Button.setVisibility(View.VISIBLE);

        mButtonContainer.setWeightSum(6);

        if(user.plan.weeks!=null)
            mWeekplans = new ArrayList<>(user.plan.weeks);
        else mWeekplans = new ArrayList<>();

        if(user.plan.weeks.size()==5){
            mWeek6Button.setVisibility(View.GONE);
            mButtonContainer.setWeightSum(5);
        }else if(user.plan.weeks.size()==4){
            mWeek5Button.setVisibility(View.GONE);
            mWeek6Button.setVisibility(View.GONE);
            mButtonContainer.setWeightSum(4);
        }else if(user.plan.weeks.size()==3){
            mWeek4Button.setVisibility(View.GONE);
            mWeek5Button.setVisibility(View.GONE);
            mWeek6Button.setVisibility(View.GONE);
            mButtonContainer.setWeightSum(3);
        }else if(user.plan.weeks.size()==2){
            mWeek3Button.setVisibility(View.GONE);
            mWeek4Button.setVisibility(View.GONE);
            mWeek5Button.setVisibility(View.GONE);
            mWeek6Button.setVisibility(View.GONE);
            mButtonContainer.setWeightSum(2);
        }else if(user.plan.weeks.size()==1){
            mWeek2Button.setVisibility(View.GONE);
            mWeek3Button.setVisibility(View.GONE);
            mWeek4Button.setVisibility(View.GONE);
            mWeek5Button.setVisibility(View.GONE);
            mWeek6Button.setVisibility(View.GONE);
            mButtonContainer.setWeightSum(1);
        }else if(user.plan.weeks.size()!=6){
            mWeek1Button.setVisibility(View.GONE);
            mWeek2Button.setVisibility(View.GONE);
            mWeek3Button.setVisibility(View.GONE);
            mWeek4Button.setVisibility(View.GONE);
            mWeek5Button.setVisibility(View.GONE);
            mWeek6Button.setVisibility(View.GONE);
        }

        this.mapWeekOrder = loadMapWeekOrder(user);

        selectWeek(mWeek1Button);

        mWeekInfoTextView.setVisibility(View.GONE);
        loadWeek(0);
        if (this.mapWeekOrder.containsKey(1)){
            int mapWeek = ((Integer)this.mapWeekOrder.get(1));
            mWeek_nameTextView.setText(getString(R.string.training_plan_week2) + " " + mapWeek +"");

            if (!fromDateBase.isEmpty()){

                SimpleDateFormat sdfComplete = new SimpleDateFormat(DATE_COMPLETE_FORMAT);
                SimpleDateFormat sdfReduce = new SimpleDateFormat(DATE_REDUCE_FORMAT);
                try {
                    Date dateBase = sdfComplete.parse(fromDateBase);
                    int daysToAssign = mapWeek * 6;

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateBase);

                    calendar.add(Calendar.DATE, daysToAssign);

                    String weekFrom = sdfReduce.format(dateBase);
                    String weekTo = sdfReduce.format(calendar.getTime());

                    String weekInfoStr = "(" + weekFrom + " - " + weekTo + ")";
                    mWeekInfoTextView.setVisibility(View.VISIBLE);
                    mWeekInfoTextView.setText(weekInfoStr);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        if(user.plan.weeks.size()>0 && !user.plan.weeks.get(0).isVisible) {
            mWeek1Button.setVisibility(View.GONE);
            counterWeeksOffset++;
            mButtonContainer.setWeightSum(mButtonContainer.getWeightSum()-1);

            selectWeek(mWeek2Button);
            loadWeek(1);
        }

        if(user.plan.weeks.size()>1 && !user.plan.weeks.get(1).isVisible) {
            mWeek2Button.setVisibility(View.GONE);
            counterWeeksOffset++;
            mButtonContainer.setWeightSum(mButtonContainer.getWeightSum()-1);

            selectWeek(mWeek3Button);
            loadWeek(2);
        }

        if(user.plan.weeks.size()>2 && !user.plan.weeks.get(2).isVisible) {
            mWeek3Button.setVisibility(View.GONE);
            counterWeeksOffset++;
            mButtonContainer.setWeightSum(mButtonContainer.getWeightSum()-1);

            selectWeek(mWeek4Button);
            loadWeek(3);
        }


        if(user.plan.weeks.size()>3 && !user.plan.weeks.get(3).isVisible) {
            mWeek4Button.setVisibility(View.GONE);
            counterWeeksOffset++;
            mButtonContainer.setWeightSum(mButtonContainer.getWeightSum()-1);

            selectWeek(mWeek5Button);
            loadWeek(4);
        }


        if(user.plan.weeks.size()>4 && !user.plan.weeks.get(4).isVisible) {
            mWeek5Button.setVisibility(View.GONE);
            counterWeeksOffset++;
            mButtonContainer.setWeightSum(mButtonContainer.getWeightSum()-1);

            selectWeek(mWeek6Button);
            loadWeek(5);
        }

        if(user.plan.weeks.size()>5 && !user.plan.weeks.get(5).isVisible) {
            mWeek6Button.setVisibility(View.GONE);
            counterWeeksOffset++;
            mButtonContainer.setWeightSum(mButtonContainer.getWeightSum()-1);
        }

        if (mapWeekOrder.containsKey(6)){
            mWeek6Button.setSubtitleText(((Integer)mapWeekOrder.get(6))+"");
            selectWeek(mWeek6Button);
            loadWeek(5);
        }
        if (mapWeekOrder.containsKey(5)){
            mWeek5Button.setSubtitleText(((Integer)mapWeekOrder.get(5))+"");
            selectWeek(mWeek5Button);
            loadWeek(4);
        }

        if (mapWeekOrder.containsKey(4)){
            mWeek4Button.setSubtitleText(((Integer)mapWeekOrder.get(4))+"");
            selectWeek(mWeek4Button);
            loadWeek(3);
        }
        if (mapWeekOrder.containsKey(3)){
            mWeek3Button.setSubtitleText(((Integer)mapWeekOrder.get(3))+"");
            selectWeek(mWeek3Button);
            loadWeek(2);
        }
        if (mapWeekOrder.containsKey(2)){
            mWeek2Button.setSubtitleText(((Integer)mapWeekOrder.get(2))+"");
            selectWeek(mWeek2Button);
            loadWeek(1);
        }
        if (mapWeekOrder.containsKey(1)){
            mWeek1Button.setSubtitleText(((Integer)mapWeekOrder.get(1))+"");
            selectWeek(mWeek1Button);
            loadWeek(0);
        }

        if(user.links!=null && user.links.size()>0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mVideoTitleTextView.setVisibility(View.VISIBLE);
            mVideoAdapter = new VideoAdapter(user.links);
            mRecyclerView.setAdapter(mVideoAdapter);

            mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),
                    mRecyclerView, new ClickListener() {
                @Override
                public void onClick(View view, final int position) {
                    try {
                        Link link = user.links.get(position);
                        String url = link.url;
                        if (url.endsWith(".mp4")){
                            Intent i = new Intent(getActivity(), VideoActivity.class);
                            i.putExtra("VIDEO_PATH", url);
                            startActivity(i);
                        }else {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }
                    }catch(Exception e){}
                }

                @Override
                public void onLongClick(View view, int position, float xx, float yy) {

                }
            }));

        }else{
            mRecyclerView.setVisibility(View.GONE);
            mVideoTitleTextView.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = mViewPager.getCurrentItem();
                if (index - 1 >= 0) {
                    mViewPager.setCurrentItem(index - 1);
                    updateArrowButtons(index - 1);
                }
            }
        });

        mNextImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = mViewPager.getCurrentItem();
                if (index + 1 < mAdapter.getCount()) {
                    mViewPager.setCurrentItem(index + 1);
                    updateArrowButtons(index + 1);
                }
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                updateArrowButtons(position);
            }
        });

        mWeek1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectWeek(mWeek1Button);
                loadWeek(0);
            }
        });

        mWeek2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectWeek(mWeek2Button);
                loadWeek(1);
            }
        });

        mWeek3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectWeek(mWeek3Button);
                loadWeek(2);
            }
        });

        mWeek4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectWeek(mWeek4Button);
                loadWeek(3);
            }
        });
        mWeek5Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectWeek(mWeek5Button);
                loadWeek(4);
            }
        });
        mWeek6Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectWeek(mWeek6Button);
                loadWeek(5);
            }
        });
    }

    private void updateArrowButtons(int page) {

        if (page == mAdapter.getCount() - 1) {
            mNextImageView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorDisabled), android.graphics.PorterDuff.Mode.MULTIPLY);
        } else
            mNextImageView.setColorFilter(mColorPrimary, android.graphics.PorterDuff.Mode.MULTIPLY);

        if (page == 0) {
            mBackImageView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorDisabled), android.graphics.PorterDuff.Mode.MULTIPLY);
        } else
            mBackImageView.setColorFilter(mColorPrimary, android.graphics.PorterDuff.Mode.MULTIPLY);

    }

    private void selectWeek(StateButton btn) {
        mWeek1Button.select(false);
        mWeek2Button.select(false);
        mWeek3Button.select(false);
        mWeek4Button.select(false);
        mWeek5Button.select(false);
        mWeek6Button.select(false);
        btn.select(true);
    }

    private void restartViewPager() {
        mViewPager.resetMaxHeight();
        mViewPager.setCurrentItem(0);

        mBackImageView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorDisabled), android.graphics.PorterDuff.Mode.MULTIPLY);
        mNextImageView.setColorFilter(mColorPrimary, android.graphics.PorterDuff.Mode.MULTIPLY);

    }

    private void loadWeek(int index) {
        if(mWeekSelected == index)
            return;

        if (mWeekplans.size()<=index)
            return;

        mWeekSelected = index;

        WeekPlan plan = mWeekplans.get(index);
        mKmsTotalTextView.setText(getActivity().getString(R.string.training_plan_total_vol) + " " +plan.totalKms+"km");
        mWeek_nameTextView.setText(getString(R.string.training_plan_week2) + " " );

        int mapWeek = -1;

        if (this.mapWeekOrder.containsKey(index+1)){
            mapWeek = ((Integer)this.mapWeekOrder.get(index+1));
            mWeek_nameTextView.setText(getString(R.string.training_plan_week2) + " " + mapWeek +"");
        }


        mWeekInfoTextView.setVisibility(View.GONE);
        if (!fromDateBase.isEmpty() && mapWeek > 0){

            SimpleDateFormat sdfComplete = new SimpleDateFormat(DATE_COMPLETE_FORMAT);
            SimpleDateFormat sdfReduce = new SimpleDateFormat(DATE_REDUCE_FORMAT);
            try {
                Date dateBase = sdfComplete.parse(fromDateBase);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateBase);

                if (mapWeek > 1){

                    int daysToAssign = ((mapWeek - 1) * 7);
                    calendar.add(Calendar.DATE, daysToAssign);
                    dateBase = calendar.getTime();
                    calendar.add(Calendar.DATE, 6);
                } else {

                    int daysToAssign = mapWeek * 6;
                    calendar.add(Calendar.DATE, daysToAssign);
                }


                String weekFrom = sdfReduce.format(dateBase);
                String weekTo = sdfReduce.format(calendar.getTime());

                String weekInfoStr = "(" + weekFrom + " - " + weekTo + ")";
                mWeekInfoTextView.setVisibility(View.VISIBLE);
                mWeekInfoTextView.setText(weekInfoStr);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        // Create an adapter with the fragments we show on the ViewPager
        mAdapter = new DaysPageAdapter(getChildFragmentManager());
        for (int i = 0; i < plan.days.size(); i += 2) {
            DayPlan dp1 = i < plan.days.size() ? plan.days.get(i) : null;
            DayPlan dp2 = i+1 < plan.days.size() ? plan.days.get(i + 1) : null;
            mAdapter.addFragment(DaysFragment.newInstance(dp1,dp2,index+1,i+1,i+2));
        }

        this.mViewPager.setAdapter(mAdapter);
        restartViewPager();
    }

}
