package com.app.runners.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.runners.BuildConfig;
import com.app.runners.R;
import com.app.runners.manager.NotificationController;
import com.app.runners.manager.UserController;
import com.app.runners.model.User;
import com.app.runners.utils.AppController;
import com.app.runners.utils.DialogHelper;
import com.app.runners.utils.IntentHelper;
import com.app.runners.utils.ResourcesHelper;

public class LoginActivity extends BaseActivity {

    private TextInputLayout mEmailTextInputLayout;
    private TextInputLayout mPasswordTextInputLayout;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private View mProgressView;
    private View mContentView;
    private
    @ColorInt
    int mColorPrimary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        NotificationController.getInstance().clearData();

        this.mContentView = findViewById(R.id.scroll);
        this.mProgressView = findViewById(R.id.login_progress);
        this.mEmailEditText = (EditText) findViewById(R.id.email_input);
        this.mPasswordEditText = (EditText) findViewById(R.id.password_input);
        this.mEmailTextInputLayout = (TextInputLayout) findViewById(R.id.email_wrapper);
        this.mPasswordTextInputLayout = (TextInputLayout) findViewById(R.id.password_wrapper);

        TextView mVersionNumber = (TextView) findViewById(R.id.version);
        mVersionNumber.setText(getString(R.string.version_number, BuildConfig.VERSION_NAME));

      /*  mColorPrimary = ColorHelper.getPrimaryColorFromTheme(getTheme());
        ImageView img = (ImageView) findViewById(R.id.imageView);
        img.setColorFilter(mColorPrimary, PorterDuff.Mode.MULTIPLY);*/


        mEmailEditText.setText(UserController.getInstance().lastEmailLogued());
        this.mEmailEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    hideSoftKeyboard(textView);
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        View loginBtn = findViewById(R.id.login_button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                attemptLogin();
            }
        });


        View forgotPasswordBtn = findViewById(R.id.forgot_password);
        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                IntentHelper.goToForgotPasswordActivity(LoginActivity.this);
            }
        });

        View firstLoginBtn = findViewById(R.id.first_login);
        firstLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                IntentHelper.gotoMyFirstLoginActivity(LoginActivity.this);
            }
        });
/*
        mEmailEditText.setText("diego@montal.com.ar");
        mPasswordEditText.setText("1234567A");


        mEmailEditText.setText("guillermopantaleo@hotmail.com");
        mPasswordEditText.setText("aaaa1234");
*/
    }

    private void attemptLogin() {
        // Reset errors.
        mEmailTextInputLayout.setError(null);
        mPasswordTextInputLayout.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordTextInputLayout.setError(getString(R.string.error_field_required));
            focusView = mPasswordEditText;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordTextInputLayout.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordEditText;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailTextInputLayout.setError(getString(R.string.error_field_required));
            focusView = mEmailEditText;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailTextInputLayout.setError(getString(R.string.error_invalid_email));
            focusView = mEmailEditText;
            cancel = true;
        }


        if (cancel) {
            focusView.requestFocus();
        } else {

            if (AppController.getInstance().isNetworkAvailable()) {
                showProgress(true);
                mContentView.setVisibility(View.INVISIBLE);

                UserController.getInstance().login(email, password, new Response.Listener<User>() {
                    @Override
                    public void onResponse(User response) {
                        ResourcesHelper.getInstance().downloadResData(response.resources, ctx);

                        showProgress(false);
                        IntentHelper.goToMainActivity(LoginActivity.this, null);
                        AppController.getInstance().saveDefaultTheme(response.theme);
                        LoginActivity.this.finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        mContentView.setVisibility(View.VISIBLE);
                        DialogHelper.showMessage(LoginActivity.this, R.string.error, R.string.error_login);
                    }
                });
            } else {
                DialogHelper.showNoInternetErrorMessage(LoginActivity.this, null);
            }
        }
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 4;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        try {
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
        } catch (Exception e) {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

}
