package com.app.runners.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.app.runners.R;
import com.app.runners.adapter.ItemRecyclerViewAdapter;
import com.app.runners.manager.UserController;
import com.app.runners.model.CoachComment;


import java.util.ArrayList;

/**
 * Created by sergiocirasa on 7/9/17.
 */

public class CoachFragment extends Fragment {
    private ItemRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<CoachComment> mComments = new ArrayList<>();
    private View mMainContent;
    private View mNoContentView;

    public CoachFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coach_comment, container, false);
        Context context = view.getContext();
        mMainContent = view.findViewById(R.id.main_content);
        mNoContentView = view.findViewById(R.id.no_content);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        setupListeners();
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
        if(UserController.getInstance().getSignedUser().plan!=null) {
            mComments = UserController.getInstance().getSignedUser().plan.coachComments;
            if (mComments != null && mComments.size() > 0) {
                showNoContentView(false);
                mAdapter = new ItemRecyclerViewAdapter(mComments, getContext());
                mRecyclerView.setAdapter(mAdapter);
            } else {
                showNoContentView(true);
            }
        } else {
            showNoContentView(true);
        }
    }

    private void setupListeners(){

    }

    private void showNoContentView(boolean show){
        mMainContent.setVisibility(show?View.GONE:View.VISIBLE);
        mNoContentView.setVisibility(show?View.VISIBLE:View.GONE);
    }

}
