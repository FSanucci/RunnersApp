package com.app.runners.fragment.racingsFragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.app.AlertDialog;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.runners.R;
import com.app.runners.activity.MainActivity;
import com.app.runners.adapter.ItemRecyclerViewAdapter;
import com.app.runners.interfaces.ClickListener;
import com.app.runners.manager.UserController;
import com.app.runners.model.Race;
import com.app.runners.model.User;
import com.app.runners.rest.RestConstants;
import com.app.runners.utils.AppController;
import com.app.runners.utils.IntentHelper;
import com.app.runners.utils.RecyclerTouchListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


public class RacingsToDoFragment extends Fragment {
    private ItemRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<Race> mRaces = new ArrayList<>();
    private View mMainContent;
    private View mNoContentView;
    private View mAddButton;
    private Race mRaceToDelete;
    private RacingsToDoFragment callback;
    private int positionToDelete;
    private int origin = 0;
    private View mMainContainer;
    private ProgressBar mProgressView;

    public RacingsToDoFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_racings_to_do, container, false);
        Context context = view.getContext();
        mAddButton = view.findViewById(R.id.add_button);
        mMainContent = view.findViewById(R.id.main_content);
        mNoContentView = view.findViewById(R.id.no_content);

        mMainContainer = view.findViewById(R.id.view_list);
        mProgressView = (ProgressBar) view.findViewById(R.id.progress);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        callback = this;

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
        mRaces = UserController.getInstance().getSignedUser().racesWish;

        if (origin == 1){
            mRaces.remove(positionToDelete);
            origin=0;
        }

        if(mRaces!=null && mRaces.size()>0){
            showNoContentView(false);
            mAdapter = new ItemRecyclerViewAdapter(mRaces, getContext());
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this.getContext(), mRecyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) { }

                @Override
                public void onLongClick(View view, int position, float xx, float yy) {
                    Log.d("CLICKLISTENER", "Long click on Races to run");
                    mRaceToDelete = UserController.getInstance().getSignedUser().racesWish.get(position);
                    positionToDelete = position;

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    alertDialogBuilder.setTitle(getResources().getString(R.string.racing_title_button));
                    alertDialogBuilder
                            .setMessage(getResources().getString(R.string.racing_message_button))
                            .setCancelable(false)
                            .setPositiveButton(getResources().getString(R.string.racing_confirm_button),new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    deleteWishRace();
                                    //IntentHelper.gotoDelRaceActivity(getActivity(), mRaceToDelete, RestConstants.HOST+RestConstants.DELETE_RACEWISH_SERVICE);
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
            }));
        }else{
            showNoContentView(true);
        }
    }
/*
    private Date tryParse(String dateString, List<String> formatStrings){

        for (String formatString : formatStrings){
            try {
                return new SimpleDateFormat(formatString).parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
*/
    private void setupListeners(){

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.gotoAddRacingWishActivity(getActivity());
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(callback).attach(callback).commit();
            }
        });
    }

    private void showNoContentView(boolean show){
        mMainContent.setVisibility(show?View.GONE:View.VISIBLE);

        mNoContentView.setVisibility(show?View.VISIBLE:View.GONE);
    }

    private void deleteWishRace(){
        origin = 1;
        if(!AppController.getInstance().isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_msg),
                    Toast.LENGTH_LONG).show();
            origin = 0;
        } else {
            showProgress(true);
            mMainContainer.setVisibility(View.INVISIBLE);

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
        }
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

