package com.app.runners.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.app.runners.R;
import com.app.runners.model.ICallback;
import com.app.runners.utils.AppController;
import com.app.runners.utils.PermissionsHelper;

/**
 * Created by sergiocirasa on 21/8/17.
 */

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BASEACT";

    protected Context ctx;
    protected Handler handler = new Handler();
    protected PermissionsHelper permHelper;
    protected ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(AppController.getInstance().getDefaultTheme(false), true);
        this.ctx = this;
        permHelper = new PermissionsHelper(this);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy------------------------");
        permHelper = null;
        Runtime.getRuntime().gc();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause: BaseActivity ------------------------");
        super.onPause();

        //Start: BackgroundTimer
       // AppController.getInstance().saveDate();
        //End: BackgroundTimer
    }

    @Override
    public void onUserInteraction(){
        AppController.getInstance().saveDate();
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume: BaseActivity ------------------------");
        super.onResume();

        //Start: BackgroundTimer
        AppController.getInstance().appOnBackground(new ICallback() {
            @Override
            public void onRequest() {
                Log.e(TAG, "onRequest() --------------------");
                Intent intent = new Intent(BaseActivity.this, SplashActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //End: BackgroundTimer
    }


    public void finishActivityWithCode(int code){
        Intent resultIntent = new Intent();
        setResult(code, resultIntent);
        finish();
    }

    protected void setupToolbar(Toolbar toolbar){
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    protected void setupToolbar(Toolbar toolbar, String title){
        setupToolbar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    protected void setupToolbar(Toolbar toolbar, int titleId){
        setupToolbar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(titleId));
        }
    }

    protected void showProgressDialog(int message) {
        try {
            if(pDialog!=null)
                pDialog.dismiss();

            pDialog = new ProgressDialog(this);
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

    protected void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    protected void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }
}
