package com.app.runners.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
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
import com.app.runners.utils.Constants;
import com.app.runners.utils.DialogHelper;

import java.util.ArrayList;
import java.util.Date;

public class AddCommentActivity extends BaseActivity {

    private View mMainContainer;
    private View mSendButton;
    private View mCancelButton;
    private EditText mNoteEditText;
    private ProgressBar mProgressView;
    private TextInputLayout mNoteTextInputLayout;
    private int mWeek;
    private int mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(AppController.getInstance().getDefaultTheme(true), true);
        setContentView(R.layout.activity_add_comment);

        mMainContainer = findViewById(R.id.scroll);
        mSendButton = findViewById(R.id.send_button);
        mCancelButton = findViewById(R.id.cancel_button);
        mNoteEditText = (EditText) findViewById(R.id.note_input);
        mNoteTextInputLayout = (TextInputLayout) findViewById(R.id.note_wrapper);
        mProgressView = (ProgressBar) findViewById(R.id.progress);

        TextView mVersionNumber = (TextView) findViewById(R.id.version);
        mVersionNumber.setText(getString(R.string.version_number, BuildConfig.VERSION_NAME));

        if(getIntent().getExtras() != null){
            String comment = getIntent().getExtras().getString(Constants.COMMENT);
            if(comment!=null)
                mNoteEditText.setText(comment);

            mWeek = getIntent().getExtras().getInt(Constants.WEEK);
            mDay = getIntent().getExtras().getInt(Constants.DAY);
        }

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                if (validateField()) {
                    sendNote();
                } else {
                    mNoteEditText.setError(getString(R.string.error_field_required));
                }
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                finish();
            }
        });
    }

    private boolean validateField() {
        if (mNoteEditText.getText().length() == 0) {
            return false;
        }
        return true;
    }

    private void sendNote() {

        if(mNoteEditText.getText() == null || mNoteEditText.getText().toString().length() == 0){
            Toast.makeText(getApplicationContext(), AddCommentActivity.this.getString(R.string.addcomment_add_error), Toast.LENGTH_SHORT).show();
            return;
        }


        if (!AppController.getInstance().isNetworkAvailable()) {
            DialogHelper.showNoInternetErrorMessage(AddCommentActivity.this, null);
            return;
        }

        showProgress(true);
        mMainContainer.setVisibility(View.INVISIBLE);


        UserController.getInstance().postDayComment(mWeek,mDay,mNoteEditText.getText().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showProgress(false);
                Toast.makeText(getApplicationContext(), AddCommentActivity.this.getString(R.string.personal_info_add_success), Toast.LENGTH_SHORT).show();
                Intent result = new Intent();
                result.putExtra(Constants.COMMENT,mNoteEditText.getText().toString());
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mMainContainer.setVisibility(View.VISIBLE);
                showProgress(false);
                DialogHelper.showMessage(AddCommentActivity.this, R.string.error, R.string.personal_info_add_error2);
            }
        });

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
