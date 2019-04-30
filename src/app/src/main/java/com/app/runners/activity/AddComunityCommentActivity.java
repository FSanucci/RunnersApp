package com.app.runners.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.runners.BuildConfig;
import com.app.runners.R;
import com.app.runners.manager.UserController;
import com.app.runners.model.RunnerComment;
import com.app.runners.utils.AppController;
import com.app.runners.utils.DialogHelper;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by devcreative on 1/23/18.
 */

public class AddComunityCommentActivity extends BaseActivity {

    private View mMainContainer;
    private View mSendButton;
    private EditText mNoteEditText;
    private ProgressBar mProgressView;
    private TextInputLayout mNoteTextInputLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(AppController.getInstance().getDefaultTheme(true), true);
        setContentView(R.layout.activity_add_personal_info);

        mMainContainer = findViewById(R.id.scroll);
        mSendButton = findViewById(R.id.send_button);
        mNoteEditText = (EditText) findViewById(R.id.note_input);
        mNoteTextInputLayout = (TextInputLayout) findViewById(R.id.note_wrapper);
        mProgressView = (ProgressBar) findViewById(R.id.progress);

        TextView mVersionNumber = (TextView) findViewById(R.id.version);
        mVersionNumber.setText(getString(R.string.version_number, BuildConfig.VERSION_NAME));

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                if(validateField()){
                    sendNote();
                }else{
                    mNoteEditText.setError(getString(R.string.error_field_required));
                }
            }
        });
    }

    private boolean validateField(){
        if(mNoteEditText.getText().length()==0){
            return false;
        }
        return true;
    }

    private void sendNote(){

        if(!AppController.getInstance().isNetworkAvailable()) {
            DialogHelper.showNoInternetErrorMessage(AddComunityCommentActivity.this,null);
            return;
        }

        showProgress(true);
        mMainContainer.setVisibility(View.INVISIBLE);


        RunnerComment note = new RunnerComment(mNoteEditText.getText().toString());
        note.date = new Date();

        UserController.getInstance().postWallComment(note, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showProgress(false);
                Toast.makeText(getApplicationContext(), AddComunityCommentActivity.this.getString(R.string.personal_info_add_success), Toast.LENGTH_SHORT).show();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mMainContainer.setVisibility(View.VISIBLE);
                showProgress(false);
                DialogHelper.showMessage(AddComunityCommentActivity.this,R.string.error,R.string.personal_info_add_error2);
            }
        });

/*
        RunnerComment note = new RunnerComment(mNoteEditText.getText().toString());
        note.date = new Date();

        UserController.getInstance().postComment(note, new Response.Listener<ArrayList<RunnerComment>>() {
            @Override
            public void onResponse(ArrayList<RunnerComment> response) {
                showProgress(false);
                Toast.makeText(getApplicationContext(), AddComunityCommentActivity.this.getString(R.string.personal_info_add_success), Toast.LENGTH_SHORT).show();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mMainContainer.setVisibility(View.VISIBLE);
                showProgress(false);
                DialogHelper.showMessage(AddComunityCommentActivity.this,R.string.error,R.string.personal_info_add_error2);
            }
        });
*/
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
