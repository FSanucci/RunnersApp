package com.app.runners.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.runners.R;
import com.app.runners.activity.AddPersonalInfoActivity;
import com.app.runners.activity.MainActivity;
import com.app.runners.adapter.ItemRecyclerViewAdapter;
import com.app.runners.manager.NotificationController;
import com.app.runners.manager.UserController;
import com.app.runners.model.CoachComment;
import com.app.runners.model.Documentation;
import com.app.runners.model.Notification;
import com.app.runners.model.RunnerComment;
import com.app.runners.model.Comment;
import com.app.runners.model.User;
import com.app.runners.rest.RestConstants;
import com.app.runners.rest.core.ParserUtils;
import com.app.runners.utils.AppController;
import com.app.runners.utils.DialogHelper;
import com.app.runners.utils.ImageProvider;
import com.app.runners.utils.IntentHelper;
import com.app.runners.utils.Storage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;


public class PersonalInfoFragment extends Fragment {
    private ItemRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<RunnerComment> mPersonalComments = new ArrayList<>();
    private ArrayList<CoachComment> mCoachComments = new ArrayList<>();
    private ArrayList<Comment> mComments = new ArrayList<>();
    private SwipeRefreshLayout mSwipeContainer;
    private PersonalInfoFragment callback;
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

    public PersonalInfoFragment() {
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
        contentTitle.setText(getResources().getString(R.string.personal_info_title));
        noContentTitle = (TextView) view.findViewById(R.id.content_empty_title);
        noContentTitle.setText(getResources().getString(R.string.personal_info_title));

        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeContainer.setRefreshing(true);
                updateData();
            }
        });

        mAddButton = view.findViewById(R.id.add_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.gotoAddPersonalNote(getActivity());
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(callback).attach(callback).commit();

                //updateData();
            }
        });

        mAttachButton = view.findViewById(R.id.add_image_button);
        mAttachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPickingDJ = false;
                showCameraPicker();
            }
        });

        showProgress(false);
        mSwipeContainer.setRefreshing(false);

        if(UserController.getInstance().getSignedUser().plan!=null) {
            mPersonalComments = UserController.getInstance().getSignedUser().plan.runnerComments;
            mCoachComments = UserController.getInstance().getSignedUser().plan.coachComments;

            mComments = fillList(mPersonalComments, mCoachComments);

        } else {
            showNoContentView(true);
        }

        //showProgress(true);
        //updateData();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mImageProvider = new ImageProvider(getActivity());
        mImageProvider.setImageProviderListener(new ImageProvider.ImageProviderListener() {
            @Override
            public void didSelectImage(String path) {
                setPicture(path);
            }
        });
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
        if(UserController.getInstance().getSignedUser().plan!=null) {
            mPersonalComments = UserController.getInstance().getSignedUser().plan.runnerComments;
            mCoachComments = UserController.getInstance().getSignedUser().plan.coachComments;

            mComments = fillList(mPersonalComments, mCoachComments);

        } else {
            showNoContentView(true);
        }
*/

        /*
        if(UserController.getInstance().getSignedUser().plan!=null) {
            mPersonalComments = UserController.getInstance().getSignedUser().plan.runnerComments;
            mCoachComments = UserController.getInstance().getSignedUser().plan.coachComments;

            mComments = fillList(mPersonalComments, mCoachComments);

        } else {
            showNoContentView(true);
        }
        */
    }

    public ArrayList<Comment> fillList(ArrayList<RunnerComment> mPersonalComments, ArrayList<CoachComment> mCoachComments ){
        if (mPersonalComments != null && mCoachComments != null &&
                (mPersonalComments.size() > 0 || mCoachComments.size() > 0)){
            showNoContentView(false);

            mComments.clear();

            for (int i=0; i<mPersonalComments.size(); i++){
                RunnerComment rComment = mPersonalComments.get(i);
                Comment comment = new Comment(rComment.getId(), rComment.getTitle(), rComment.getSubtitle(), rComment.getResource(), rComment.getDateInt());
                comment.setAutor(getContext().getString(R.string.personal_info_name));
                mComments.add(comment);
            }

            for (int i=0; i<mCoachComments.size(); i++){
                CoachComment cComment = mCoachComments.get(i);
                Comment comment = new Comment(cComment.getId(), cComment.getTitle(), cComment.getSubtitle(), cComment.getResource(), cComment.getDateInt());
                comment.setTitle(getContext().getString(R.string.coach_name));
                comment.setAutor(getContext().getString(R.string.coach_name));
                mComments.add(comment);
            }
/*
                mComments.sort(new Comparator<Comment>() {
                    @Override
                    public int compare(Comment c1, Comment c2) {
                        return c1.getDateInt().getTime() > c2.getDateInt().getTime()? -1 : 1;
                    }
                });
*/
            for (int i=1; i<mComments.size(); i++){
                for (int j=0; j<mComments.size()-i; j++){
                    if (mComments.get(j).getDateInt().getTime() < mComments.get(j+1).getDateInt().getTime()){
                        Comment aux = mComments.get(j);
                        mComments.set(j,mComments.get(j+1));
                        mComments.set(j+1,aux);
                    }
                }
            }

            mAdapter = new ItemRecyclerViewAdapter(mComments, getContext());

            mAdapter.setImageClickListener(new ItemRecyclerViewAdapter.OnImageClickListener() {
                @Override
                public void onImageClickListener(Comment data) {
                    Log.i("CLICKLISTENER", "Image Click Listener");

                    if (((MainActivity)getActivity()) != null) {
                        Storage.getInstance().setImage(data.getResource());
                        ((MainActivity)getActivity()).gotoZoomImage();
                    }
                }
            });

            mAdapter.setImageLongClickListener(new ItemRecyclerViewAdapter.OnImageLongClickListener() {
                @Override
                public void onImageLongClickListener(final Comment data) {
                    Log.i("CLICKLISTENER", "Image Long Click Listener");

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    alertDialogBuilder.setTitle(getResources().getString(R.string.message_title_button));
                    alertDialogBuilder
                            .setMessage(getResources().getString(R.string.message_info_button))
                            .setCancelable(false)
                            .setPositiveButton(getResources().getString(R.string.racing_confirm_button),new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    deleteImage(data);
                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.racing_cancel_button),new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });

            mRecyclerView.setAdapter(mAdapter);

        } else {
            showNoContentView(true);
        }

        return mComments;
    }

    private void updateData(){
        UserController.getInstance().updateUser(new Response.Listener<User>() {
            @Override
            public void onResponse(User response) {

                if (UserController.getInstance().getSignedUser().plan != null){
                    mPersonalComments = UserController.getInstance().getSignedUser().plan.runnerComments;
                    mCoachComments = UserController.getInstance().getSignedUser().plan.coachComments;
                }

                mComments = fillList(mPersonalComments, mCoachComments);

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
    }

    private void setPicture(String photoPath) {
        Log.i("PATH", "Image pick");

        showProgressDialog(R.string.uploading);
        UserController.getInstance().uploadImageForChat(photoPath, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dismissProgressDialog();
                updateUserContent(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                DialogHelper.showMessage(getActivity(),R.string.uploading_error);
            }
        });
    }

    protected void showProgressDialog(int message) {
        try {
            if(pDialog!=null)
                pDialog.dismiss();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getResources().getString(message));
            pDialog.setCancelable(true);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
        } catch (Throwable e) {
        }
    }

    protected void dismissProgressDialog() {
        try {
            if (pDialog != null) {
                pDialog.dismiss();
            }
        } catch (Throwable e) {
        }
    }

    private void updateUserContent(String uriPath){
        RunnerComment note = new RunnerComment(null, uriPath);
        note.date = new Date();

        showProgress(true);
        UserController.getInstance().postComment(note, new Response.Listener<ArrayList<RunnerComment>>() {
            @Override
            public void onResponse(ArrayList<RunnerComment> response) {
                showProgress(false);
                DialogHelper.showMessage(getActivity(),R.string.success,R.string.ip_picker_add_success);
                updateData();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                DialogHelper.showMessage(getActivity(),R.string.error,R.string.ip_picker_add_error);
                updateData();
            }
        });
    }

    public void showCameraPicker(){
        mImageProvider.showImagePicker();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mImageProvider.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mImageProvider.onRequestPermissionsResult(requestCode,permissions,grantResults);
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

    public void deleteImage(Comment comment){
        origin = 1;
        if(!AppController.getInstance().isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_msg),
                    Toast.LENGTH_LONG).show();
            origin = 0;
        } else {
            showProgress(true);

            String path = RestConstants.HOST + RestConstants.DELETE_COMMENT_SERVICE;
            UserController.getInstance().deleteComment(comment, path, new Response.Listener<Void>() {
                @Override
                public void onResponse(Void response) {
                    showProgress(false);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.message_del_success), Toast.LENGTH_SHORT).show();
                    updateData();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showProgress(false);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.message_del_error), Toast.LENGTH_SHORT).show();
                }
            });

/*
            UserController.getInstance().deleteRace(mRaceToDelete, RestConstants.HOST + RestConstants.DELETE_RACEWISH_SERVICE, new Response.Listener<Void>() {
                @Override
                public void onResponse(Void response) {
                    showProgress(false);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.racing_del_success), Toast.LENGTH_SHORT).show();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(callback).attach(callback).commit();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mMainContainer.setVisibility(View.VISIBLE);
                    showProgress(false);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.racing_del_error), Toast.LENGTH_SHORT).show();
                }
            });
*/
        }
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
