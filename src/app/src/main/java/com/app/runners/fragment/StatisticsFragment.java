package com.app.runners.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.runners.R;
import com.app.runners.adapter.ItemStatisticViewAdapter;
import com.app.runners.manager.UserController;
import com.app.runners.model.Race;
import com.app.runners.model.StatisticItem;
import com.app.runners.model.StatisticListItem;
import com.app.runners.model.User;
import com.app.runners.rest.RestConstants;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private static final String TAG = "STATISTIC_FRG";

    private ItemStatisticViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView mUserName;
    private SimpleDraweeView mProfilePreview;
    private ProgressBar mProgressView;
    private View mMainContent;
    private View mNoContentView;

    private List<StatisticItem> mStatistics = new ArrayList<>();
    private List<StatisticListItem> mSatisticAll = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        Context context = view.getContext();

        mUserName = (TextView) view.findViewById(R.id.headder_title);
        mProfilePreview = (SimpleDraweeView) view.findViewById(R.id.profile);

        User user = UserController.getInstance().getSignedUser();
        String message = context.getString(R.string.statistics_wellcome) + " " + user.profile.firstName + " " + user.profile.lastName + "!";
        mUserName.setText(message);

        if (user.profile.profilePictureImage != null){
            String uriPath = RestConstants.IMAGE_HOST + user.profile.profilePictureImage;
            Uri uri3 = Uri.parse(uriPath);
            if (uri3 != null){
                mProfilePreview.setImageURI(uri3);
            }
        } else {
            //mProfilePreview.setVisibility(View.GONE);
            mProfilePreview.setImageDrawable(context.getDrawable(R.drawable.ic_profile_person));
        }

        mMainContent = view.findViewById(R.id.body_container);
        mNoContentView = view.findViewById(R.id.no_content);

        mProgressView = (ProgressBar) view.findViewById(R.id.progress);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();

        fillStatisticList();

        mStatistics = new ArrayList<>();
        for (StatisticListItem item : mSatisticAll){

            int lastRecord = (item.races.size()) - 1;
            boolean firstItem = true;
            for (Race race : item.races){
                StatisticItem newItem = new StatisticItem();
                newItem.id = item.id;
                newItem.race = race;
                newItem.duration = ((race.durationHour * 60) + race.durationMinute);
/*
                if (lastRecord == 0){
                    newItem.startList = true;
                } else {
                    lastRecord--;
                    newItem.startList = false;
                }
*/
                newItem.startList = firstItem;
                firstItem = false;

                mStatistics.add(newItem);
            }
        }

        if(mStatistics!=null && mStatistics.size()>0){
            Log.e(TAG, "FillRecyclerView");

            showNoContentView(false);
            mAdapter = new ItemStatisticViewAdapter(mStatistics, getContext());
            mRecyclerView.setAdapter(mAdapter);
        }else{
            showNoContentView(true);
        }
    }

    private void fillStatisticList(){

        List<Race> races = UserController.getInstance().getSignedUser().races;
        mSatisticAll = new ArrayList<>();

        for (Race race : races){
            if (race.km != null && race.km >= 0){
                StatisticListItem itemList = null;
                int index = 0;
                for (StatisticListItem currentListItem : mSatisticAll){
                    if (currentListItem.id == race.km){
                        itemList = mSatisticAll.remove(index);
                        break;
                    }
                    index++;
                }

                if (itemList == null){
                    itemList = new StatisticListItem();
                    itemList.id = race.km;
                    itemList.races.add(race);
                } else {
                    itemList.races.add(race);
                }
                mSatisticAll.add(itemList);
            }
        }

        if (mSatisticAll.size() > 1){
            Collections.sort(mSatisticAll, new Comparator<StatisticListItem>() {
                @Override
                public int compare(StatisticListItem o1, StatisticListItem o2) {
                    StatisticListItem s1 = (StatisticListItem) o1;
                    StatisticListItem s2 = (StatisticListItem) o2;
                    return Integer.compare(s1.id, s2.id);
                }
            });
        }
    }

    private void showNoContentView(boolean show){
        mMainContent.setVisibility(show?View.GONE:View.VISIBLE);
        //  mAddButton.setVisibility(show?View.GONE:View.VISIBLE);
        mNoContentView.setVisibility(show?View.VISIBLE:View.GONE);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

}