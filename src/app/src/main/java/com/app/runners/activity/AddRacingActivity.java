package com.app.runners.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.support.design.widget.TextInputLayout;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddRacingActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    private final static String TAG = "ADDRACING";

    private View mMainContainer;
    private View mSendButton;
    private EditText mRacingEditText;
    private ProgressBar mProgressView;
    private TextInputLayout mRacingTextInputLayout;

    private LinearLayout mRacingContainerDate;
    private LinearLayout mRacingContainerKm;

    TimePickerDialog picker;
    private EditText mRacingEditTextKm;
    private TextInputLayout mRacingTextInputLayoutKm;
    private EditText mRacingEditTextDate;
    private TextInputLayout mRacingTextInputLayoutDate;

    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private LinearLayout mRacingContainerDuration;
    private EditText mRacingEditTextDuration;
    private int durationHour = 0;
    private int durationMinute = 0;
    private int durationSecond = 0;

    //TimePicker
    private LinearLayout picker_container;
    private Button btn_time_picker;
    private NumberPicker pickerHours;
    private NumberPicker pickerMinutes;
    private NumberPicker pickerSeconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(AppController.getInstance().getDefaultTheme(true), true);
        setContentView(R.layout.activity_add_racing);

        mMainContainer = findViewById(R.id.scroll);
        mSendButton = findViewById(R.id.send_button);
        mRacingEditText = (EditText) findViewById(R.id.racing_input);
        mRacingTextInputLayout = (TextInputLayout) findViewById(R.id.racing_wrapper);

        mRacingContainerDate = (LinearLayout) findViewById(R.id.desc_container_date);
        mRacingContainerKm = (LinearLayout) findViewById(R.id.desc_container_km);
        mProgressView = (ProgressBar) findViewById(R.id.progress);

        TextView mVersionNumber = (TextView) findViewById(R.id.version);
        mVersionNumber.setText(getString(R.string.version_number, BuildConfig.VERSION_NAME));

        //mRacingContainerDate.setVisibility(View.GONE);
        //mRacingContainerKm.setVisibility(View.GONE);

        mRacingEditTextKm = (EditText) findViewById(R.id.racingkm_input);
        mRacingTextInputLayoutKm = (TextInputLayout) findViewById(R.id.racingkm_wrapper);
        mRacingEditTextDate = (EditText) findViewById(R.id.racingdate_input);
        mRacingTextInputLayoutDate = (TextInputLayout) findViewById(R.id.racingdate_wrapper);
        mRacingContainerDuration = (LinearLayout) findViewById(R.id.desc_container_duration);
        mRacingEditTextDuration = (EditText) findViewById(R.id.racingduration_input);
        mRacingEditTextDuration.setInputType(InputType.TYPE_NULL);
        mRacingEditTextDuration.setText("00:00:00");
        mRacingEditTextDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                final Calendar cldr = Calendar.getInstance();
                //int hour = cldr.get(Calendar.HOUR_OF_DAY);
                //int minutes = cldr.get(Calendar.MINUTE);

                int hour = 0;
                int minutes = 0;

                picker = new MyTimePickerDialog(AddRacingActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                durationHour = sHour;
                                durationMinute = sMinute;
                                mRacingEditTextDuration.setText(sHour + ":" + sMinute);
                            }
                        }, hour, minutes, true);
                picker.show();
*/
                hideSoftKeyboard(v);
                picker_container.setVisibility(View.VISIBLE);
            }
        });

        mRacingEditTextDate.setOnClickListener(new View.OnClickListener() {
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
/*
                hideSoftKeyboard(v);
                if(validateField()){
                    sendRacing();
                }else{
                    mRacingEditText.setError(getString(R.string.error_field_required));
                }
*/
                hideSoftKeyboard(v);
                if(validateField()){
                    sendRacing();
                }

            }
        });


        //TimePicker
        picker_container = (LinearLayout) findViewById(R.id.picker_container);
        picker_container.setVisibility(View.GONE);

        pickerHours = (NumberPicker) findViewById(R.id.picker_hours);
        pickerHours.setMaxValue(23);
        pickerHours.setMinValue(0);
        pickerMinutes = (NumberPicker) findViewById(R.id.picker_minutes);
        pickerMinutes.setMaxValue(59);
        pickerMinutes.setMinValue(0);
        pickerSeconds = (NumberPicker) findViewById(R.id.picker_seconds);
        pickerSeconds.setMaxValue(59);
        pickerSeconds.setMinValue(0);

        btn_time_picker = (Button) findViewById(R.id.picker_btn);
        btn_time_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                durationHour = pickerHours.getValue();
                durationMinute = pickerMinutes.getValue();
                durationSecond = pickerSeconds.getValue();

                String rHours = String.format("%02d", durationHour);
                String rMinutes = String.format("%02d", durationMinute);
                String rSeconds = String.format("%02d", durationSecond);

                String raceDuration =  rHours + ":" + rMinutes + ":" + rSeconds;
                Log.e(TAG, "RaceDuration: " + raceDuration);
                mRacingEditTextDuration.setText(raceDuration);
                picker_container.setVisibility(View.GONE);
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
        mRacingEditTextDate.setText(dateFormatter);
    }

    private boolean validateField(){
/*
        if(mRacingEditText.getText().length()==0){
            return false;
        }
        return true;
*/

        if (mRacingEditText.getText().length() == 0){
            mRacingEditText.setError(getString(R.string.error_field_required));
            return false;
        } else {
            mRacingEditText.setError(null);
        }
        if (mRacingEditTextKm.getText().length() == 0){
            mRacingEditTextKm.setError(getString(R.string.error_field_required));
            return false;
        } else {
            mRacingEditTextKm.setError(null);
        }
        if (mRacingEditTextDate.getText().length() == 0){
            mRacingEditTextDate.setError(getString(R.string.error_field_required));
            return false;
        } else {
            mRacingEditTextDate.setError(null);
        }
        if (mRacingEditTextDuration.getText().length() == 0){
            mRacingEditTextDuration.setError(getString(R.string.error_field_required));
            return false;
        } else {
            mRacingEditTextDuration.setError(null);
        }

        return true;
    }

    private void sendRacing(){

        if(!AppController.getInstance().isNetworkAvailable()) {
            DialogHelper.showNoInternetErrorMessage(AddRacingActivity.this,null);
            return;
        }

        showProgress(true);
        mMainContainer.setVisibility(View.INVISIBLE);

        //Race race = new Race(mRacingEditText.getText().toString());

        Race race = new Race(mRacingEditText.getText().toString(), Integer.parseInt(mRacingEditTextKm.getText().toString()),
                ParserUtils.parseDate(mRacingEditTextDate.getText().toString(),"dd/MM/yyyy"), durationHour, durationMinute, durationSecond);

        UserController.getInstance().postRace(race, new Response.Listener<Void>() {
            @Override
            public void onResponse(Void response) {
                showProgress(false);
                Toast.makeText(getApplicationContext(), AddRacingActivity.this.getString(R.string.racing_add_success), Toast.LENGTH_SHORT).show();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mMainContainer.setVisibility(View.VISIBLE);
                showProgress(false);
                DialogHelper.showMessage(AddRacingActivity.this,R.string.error,R.string.racing_add_error2);
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

    private class MyTimePickerDialog extends TimePickerDialog {
        MyTimePickerDialog(Context context, TimePickerDialog.OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
            super(context, callBack, hourOfDay, minute, is24HourView);
        }

        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            // to prevent the dialog from changing the title which by default contains AM/PM
        }
    }
}
