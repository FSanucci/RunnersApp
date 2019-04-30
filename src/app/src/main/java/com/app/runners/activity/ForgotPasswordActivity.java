package com.app.runners.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.runners.BuildConfig;
import com.app.runners.R;
import com.app.runners.manager.UserController;
import com.app.runners.utils.AppController;
import com.app.runners.utils.ColorHelper;
import com.app.runners.utils.DialogHelper;

public class ForgotPasswordActivity extends BaseActivity {

    private TextInputLayout mEmailTextInputLayout;
    private EditText mEmailEditText;
    private View mProgressView;
    private View mContentView;
    private @ColorInt int mColorPrimary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        this.mContentView = findViewById(R.id.scroll);
        this.mProgressView = findViewById(R.id.login_progress);
        this.mEmailEditText = (EditText) findViewById(R.id.email_input);
        this.mEmailTextInputLayout = (TextInputLayout) findViewById(R.id.email_wrapper);

        mColorPrimary = ColorHelper.getPrimaryColorFromTheme(getTheme());
        ImageView img = (ImageView) findViewById(R.id.imageView);
        img.setColorFilter(mColorPrimary, android.graphics.PorterDuff.Mode.MULTIPLY);

        TextView mVersionNumber = (TextView) findViewById(R.id.version);
        mVersionNumber.setText(getString(R.string.version_number, BuildConfig.VERSION_NAME));

        View loginBtn = findViewById(R.id.button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                onForgotPassword(mEmailEditText.getText().toString());
            }
        });

    }


    public void onForgotPassword(String email){
        mEmailTextInputLayout.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailTextInputLayout.setError(getString(R.string.error_field_required));
            focusView = mEmailEditText;
            cancel = true;
        }else if (!isEmailValid(email)) {
            mEmailTextInputLayout.setError(getString(R.string.error_invalid_email));
            focusView = mEmailEditText;
            cancel = true;
        }


        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mContentView.setVisibility(View.INVISIBLE);

            if(AppController.getInstance().isNetworkAvailable()) {
                UserController.getInstance().passRecovery(email, new Response.Listener<Void>() {
                    @Override
                    public void onResponse(Void response) {
                        showProgress(false);
                        DialogHelper.showMessage(ForgotPasswordActivity.this,null,ForgotPasswordActivity.this.getString(R.string.success_pass_recovery),new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which){
                                ForgotPasswordActivity.this.finish();
                            }
                        });
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mContentView.setVisibility(View.VISIBLE);
                        showProgress(false);
                        DialogHelper.showMessage(ForgotPasswordActivity.this,R.string.error,R.string.error_pass_recovery);
                    }
                });
            }else{
                mContentView.setVisibility(View.VISIBLE);
                showProgress(false);
                DialogHelper.showNoInternetErrorMessage(ForgotPasswordActivity.this,null);
            }
        }
    }
    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
