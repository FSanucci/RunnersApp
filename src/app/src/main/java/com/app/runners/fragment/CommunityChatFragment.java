package com.app.runners.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.runners.R;
import com.app.runners.adapter.ItemRecyclerViewAdapter;
import com.app.runners.manager.UserController;
import com.app.runners.model.CoachComment;
import com.app.runners.model.Comment;
import com.app.runners.model.RunnerComment;
import com.app.runners.model.User;
import com.app.runners.model.WallComment;
import com.app.runners.utils.DialogHelper;
import com.app.runners.utils.ImageProvider;
import com.app.runners.utils.IntentHelper;

import java.util.ArrayList;

/**
 * Created by devcreative on 1/17/18.
 */

public class CommunityChatFragment extends Fragment {
    private String ERROR = "304 | Request failed";

    private ItemRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private CommunityChatFragment callback;
    private ArrayList<RunnerComment> mPersonalComments = new ArrayList<>();
    private ArrayList<CoachComment> mCoachComments = new ArrayList<>();
    private ArrayList<WallComment> mResponse = new ArrayList<>();
    private ArrayList<Comment> mComments = new ArrayList<>();
    private SwipeRefreshLayout mSwipeContainer;
    private View mMainContent;
    private View mNoContentView;
    private View mAddButton;
    private View mAttachButton;
    private View mProgressView;
    private TextView contentTitle;
    private TextView noContentTitle;

    private int origin = 0;
    private boolean mPickingDJ;
    private ImageProvider mImageProvider;
    protected ProgressDialog pDialog;


    public CommunityChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Context context = view.getContext();
        callback = this;
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

        contentTitle = (TextView) view.findViewById(R.id.content_title);
        contentTitle.setText(getResources().getString(R.string.message_info_title));
        noContentTitle = (TextView) view.findViewById(R.id.content_empty_title);
        noContentTitle.setText(getResources().getString(R.string.message_info_title));

        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeContainer.setRefreshing(true);
                //mComments.clear();
                updateData();
            }
        });

        mAddButton = view.findViewById(R.id.add_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //IntentHelper.gotoAddPersonalNote(getActivity());
                IntentHelper.gotoAddComunityComment(getActivity());
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(callback).attach(callback).commit();

                //mComments.clear();
                //updateData();
            }
        });

        mAttachButton = view.findViewById(R.id.add_image_button);
        mAttachButton.setVisibility(View.GONE);
        mAttachButton.setEnabled(false);

        showProgress(false);
        mSwipeContainer.setRefreshing(false);

        fillList(false);
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

        updateData();
/*
        UserController.getInstance().getWallComments(new Response.Listener<ArrayList<WallComment>>() {
            @Override
            public void onResponse(ArrayList<WallComment> response) {
                Log.i("WALLCOMMENT", "PrimaryClass");

                mResponse = response;
                for (WallComment comment : mResponse){
                    Comment newComment = new Comment(comment._id, comment.owner, comment.coach, comment.author,
                                                      comment.text, null, comment.crationDate);
                    mComments.add(newComment);
                }

                if (!mComments.isEmpty()){
                    mAdapter = new ItemRecyclerViewAdapter(mComments, getContext());
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    showNoContentView(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //dismissProgressDialog();
                DialogHelper.showMessage(getActivity(),R.string.uploading_error);
            }
        });
*/
    }

    private void fillList(final boolean refresh){
        mComments = new ArrayList<Comment>();
        UserController.getInstance().getWallComments(new Response.Listener<ArrayList<WallComment>>() {
            @Override
            public void onResponse(ArrayList<WallComment> response) {
                Log.i("WALLCOMMENT", "PrimaryClass");

                mResponse = response;
                for (WallComment comment : mResponse){
                    Comment newComment = new Comment(comment._id, comment.owner, comment.coach, comment.author,
                            comment.text, null, comment.crationDate);
                    mComments.add(newComment);
                }

                for (int i=1; i<mComments.size(); i++){
                    for (int j=0; j<mComments.size()-i; j++){
                        if (mComments.get(j).getDateInt().getTime() < mComments.get(j+1).getDateInt().getTime()){
                            Comment aux = mComments.get(j);
                            mComments.set(j,mComments.get(j+1));
                            mComments.set(j+1,aux);
                        }
                    }
                }
/*
                if (refresh){
                    showProgress(false);
                    mSwipeContainer.setRefreshing(false);

                    if (mComments.isEmpty()){
                        mNoContentView.setVisibility(View.VISIBLE);
                        mMainContent.setVisibility(View.GONE);
                    } else {
                        mNoContentView.setVisibility(View.GONE);
                        mMainContent.setVisibility(View.VISIBLE);
                        //mAdapter = new ItemRecyclerViewAdapter(mComments, getContext());
                        mRecyclerView.setAdapter(mAdapter);
                    }
                } else {
                    if (!mComments.isEmpty()){
                        mAdapter = new ItemRecyclerViewAdapter(mComments, getContext());
                        mRecyclerView.setAdapter(mAdapter);
                    } else {
                        showNoContentView(true);
                    }
                }

                */


                if (refresh) {
                    showProgress(false);
                    mSwipeContainer.setRefreshing(false);
                }

                if (!mComments.isEmpty()){
                    mAdapter = new ItemRecyclerViewAdapter(mComments, getContext());
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    showNoContentView(true);
                }



                }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //dismissProgressDialog();
                if (!error.getMessage().equals(ERROR)){
                    DialogHelper.showMessage(getActivity(),R.string.error_comunitychat);
                }
            }
        });
    }

    private void updateData(){
        fillList(true);

/*
        UserController.getInstance().updateUser(new Response.Listener<User>() {
            @Override
            public void onResponse(User response) {

                //fillList();

                showProgress(false);
                mSwipeContainer.setRefreshing(false);

                if (mComments.isEmpty()){
                    mNoContentView.setVisibility(View.VISIBLE);
                    mMainContent.setVisibility(View.GONE);
                } else {
                    mNoContentView.setVisibility(View.GONE);
                    mMainContent.setVisibility(View.VISIBLE);
                    //mAdapter = new ItemRecyclerViewAdapter(mComments, getContext());
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
*/
    }

    private void showNoContentView(boolean show){
        mMainContent.setVisibility(show?View.GONE:View.VISIBLE);

        if(UserController.getInstance().getSignedUser().plan != null){
            mAddButton.setVisibility(View.VISIBLE);
        }else {
            mAddButton.setVisibility(View.GONE);
        }
        mNoContentView.setVisibility(show?View.VISIBLE:View.GONE);
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
