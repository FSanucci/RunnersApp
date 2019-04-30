package com.app.runners.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.os.Build;
import android.support.design.widget.TextInputLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.runners.BuildConfig;
import com.app.runners.R;
import com.app.runners.fragment.DatePickerFragment;
import com.app.runners.manager.UserController;
import com.app.runners.model.Race;
import com.app.runners.rest.core.ParserUtils;
import com.app.runners.utils.AppController;
import com.app.runners.utils.DialogHelper;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.app.runners.rest.core.ParserUtils.DATE_FORMAT;
import static com.app.runners.rest.core.ParserUtils.DATE_TIME_FORMAT;

public class AddRacingWishActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    private View mMainContainer;
    private View mSendButton;
    private EditText mRacingWishEditText;
    private ProgressBar mProgressView;
    private TextInputLayout mRacingWishTextInputLayout;

    private EditText mRacingWishEditTextKm;
    private TextInputLayout mRacingWishTextInputLayoutKm;
    private EditText mRacingWishEditTextDate;
    private TextInputLayout mRacingWishTextInputLayoutDate;

    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private LinearLayout mRacingWishTextInputLayoutDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(AppController.getInstance().getDefaultTheme(true), true);
        setContentView(R.layout.activity_add_racing);

        mMainContainer = findViewById(R.id.scroll);
        mSendButton = findViewById(R.id.send_button);
        mRacingWishEditText = (EditText) findViewById(R.id.racing_input);
        mRacingWishTextInputLayout = (TextInputLayout) findViewById(R.id.racing_wrapper);
        mRacingWishEditTextKm = (EditText) findViewById(R.id.racingkm_input);
        mRacingWishTextInputLayoutKm = (TextInputLayout) findViewById(R.id.racingkm_wrapper);
        mRacingWishEditTextDate = (EditText) findViewById(R.id.racingdate_input);
        mRacingWishTextInputLayoutDate = (TextInputLayout) findViewById(R.id.racingdate_wrapper);
        mProgressView = (ProgressBar) findViewById(R.id.progress);

        LinearLayout timePicker = (LinearLayout) findViewById(R.id.picker_container);
        timePicker.setVisibility(View.GONE);

        TextView mVersionNumber = (TextView) findViewById(R.id.version);
        mVersionNumber.setText(getString(R.string.version_number, BuildConfig.VERSION_NAME));

        mRacingWishTextInputLayoutDuration = (LinearLayout) findViewById(R.id.desc_container_duration);
        mRacingWishTextInputLayoutDuration.setVisibility(View.GONE);

        mRacingWishEditTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.show(getSupportFragmentManager(), "date");
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                if(validateField()){
                    sendRacing();
                }else{
                    mRacingWishEditText.setError(getString(R.string.error_field_required));
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar cal = new GregorianCalendar(year, month, day);
        setDate(cal);
    }

    private void setDate(final Calendar calendar){

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        String dateFormatter = formatter.format(calendar.getTime());
        mRacingWishEditTextDate.setText(dateFormatter);
    }

    private boolean validateField(){
        if(mRacingWishEditText.getText().length()==0){
            return false;
        }
        if(mRacingWishEditTextKm.getText().length()==0){
            return false;
        }
        if(mRacingWishEditTextDate.getText().length()==0){
            return false;
        }
        return true;
    }

    private void sendRacing(){

        if(!AppController.getInstance().isNetworkAvailable()) {
            DialogHelper.showNoInternetErrorMessage(AddRacingWishActivity.this,null);
            return;
        }

        showProgress(true);
        mMainContainer.setVisibility(View.INVISIBLE);

        Race race = new Race(mRacingWishEditText.getText().toString(), Integer.parseInt(mRacingWishEditTextKm.getText().toString()), ParserUtils.parseDate(mRacingWishEditTextDate.getText().toString(),"dd/MM/yyyy"));
        Log.e("RACE", race.km + " " + race.runningDate);
        UserController.getInstance().postRaceWish(race, new Response.Listener<Void>() {
            @Override
            public void onResponse(Void response) {
                showProgress(false);
                Toast.makeText(getApplicationContext(), AddRacingWishActivity.this.getString(R.string.racing_add_success), Toast.LENGTH_SHORT).show();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mMainContainer.setVisibility(View.VISIBLE);
                showProgress(false);
                DialogHelper.showMessage(AddRacingWishActivity.this,R.string.error,R.string.racing_add_error2);
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
