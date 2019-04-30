package com.app.runners.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.runners.R;
import com.app.runners.adapter.ItemRecyclerViewAdapter;
import com.app.runners.manager.NotificationController;
import com.app.runners.manager.PaymentController;
import com.app.runners.model.Notification;
import com.app.runners.model.Payment;
import com.app.runners.utils.DialogHelper;

import java.util.ArrayList;


public class PaymentsFragment extends Fragment {

    private ItemRecyclerViewAdapter mAdapter;
    private SwipeRefreshLayout mSwipeContainer;
    private RecyclerView mRecyclerView;
    private ArrayList<Payment> mPayments = new ArrayList<>();
    private View mMainContent;
    private View mNoContentView;
    private View mProgressView;

    public PaymentsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationController.getInstance().readNotifications();
    }

    @Override
    public void onStop() {
        super.onStop();
        NotificationController.getInstance().readNotifications();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payments, container, false);
        Context context = view.getContext();
        mMainContent = view.findViewById(R.id.main_content);
        mNoContentView = view.findViewById(R.id.no_content);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mProgressView = view.findViewById(R.id.progress);
        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeContainer.setRefreshing(true);
                updateData(true);
            }
        });
        showProgress(true);
        updateData(true);
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


    private void updateData(boolean force){

        PaymentController.getInstance().getItems(force, new Response.Listener<ArrayList<Payment>>() {
            @Override
            public void onResponse(ArrayList<Payment> list) {
                showProgress(false);
                mSwipeContainer.setRefreshing(false);
                if(list==null || list.size()==0){
                    mNoContentView.setVisibility(View.VISIBLE);
                    mMainContent.setVisibility(View.GONE);
                }else{
                    mPayments = list;
                    mNoContentView.setVisibility(View.GONE);
                    mMainContent.setVisibility(View.VISIBLE);
                    mAdapter = new ItemRecyclerViewAdapter(mPayments, getContext());
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                mSwipeContainer.setRefreshing(false);
                mNoContentView.setVisibility(View.VISIBLE);
                mMainContent.setVisibility(View.GONE);
                DialogHelper.showStandardErrorMessage(getActivity(),null);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2 && getActivity()!=null) {
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
        }catch(Exception e){
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

}
