package com.app.runners.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.runners.R;
import com.app.runners.manager.UserController;
import com.app.runners.model.User;
import com.app.runners.utils.AppController;
import com.app.runners.utils.ColorHelper;
import com.app.runners.utils.Constants;
import com.app.runners.utils.IntentHelper;
import com.app.runners.utils.ResourcesHelper;
import com.app.runners.utils.Storage;
import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.app.runners.utils.Constants.KEY_RESOURCE;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SPLASH_ACTV";

    private Handler mHandler = new Handler();
    private @ColorInt int mColorPrimary;

    private Handler mHandlerBack = new Handler();
    private ImageView logo;
    private ImageView imageView;
    private VideoView videoView;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(AppController.getInstance().getDefaultTheme(false), true);
        setContentView(R.layout.activity_splash);

        logo = (ImageView) findViewById(R.id.imageView);
        imageView = (ImageView) findViewById(R.id.image_back);
        videoView = (VideoView) findViewById(R.id.video_view);
        ctx = this;

        /*mColorPrimary = ColorHelper.getPrimaryColorFromTheme(getTheme());
        ImageView img = (ImageView) findViewById(R.id.imageView);
        img.setColorFilter(mColorPrimary, android.graphics.PorterDuff.Mode.MULTIPLY);*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        logo.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);

        String[] filesInStorage = ctx.fileList();
        List<String> fileList = new ArrayList<String>();
        for (String filename : filesInStorage){
            Log.e(TAG, "FileInStorage: " + filename);

            if ((filename.contains(".mp4") || filename.contains(".jpg")) && !filename.contains("temp")){
                fileList.add(filename);
            }
        }

        final long d1s = (new Date(System.currentTimeMillis())).getTime() / 1000;

        int position = -1;
        //long delay = 2000L;
        long delay = 0L;
        if (fileList.size() > 0){

            long resourceValue = Storage.getInstance().getLongPreferences(KEY_RESOURCE);
            if (resourceValue == 0){

                position = 0;
                Storage.getInstance().putLongPreferences(KEY_RESOURCE, 1);
            } else {

                resourceValue++;
                position = (int) (resourceValue - 1);

                if (resourceValue == fileList.size()){
                    Storage.getInstance().remove(KEY_RESOURCE);
                } else {
                    Storage.getInstance().putLongPreferences(KEY_RESOURCE, resourceValue);
                }
            }

            Log.e(TAG, "position: " + position);
            Log.e(TAG, "fileList.size(): " + fileList.size());

            if(position >= fileList.size())
                position = fileList.size() - 1;

            if(position < 0) position = 0;

            String resourceName = fileList.get(position);
            String filePath = ctx.getFilesDir() + "/" + resourceName;

            if (resourceName.contains(".mp4")){
                delay = 7L;
                logo.setVisibility(View.INVISIBLE);
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoPath(filePath);
                videoView.start();

            } else if (resourceName.contains(".jpg")){
                delay = 4L;
                File file = new File(filePath);
                Bitmap bitmapFile = BitmapFactory.decodeFile(file.getAbsolutePath());

                logo.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bitmapFile);
            }
        }

        final long delayFinal = delay;
        if (UserController.getInstance().getSignedUser() == null) {

            final long d2s = (new Date(System.currentTimeMillis())).getTime() / 1000;
            final long diff = (d2s - d1s);
            Log.e(TAG, "INIT_PARAMS (NO-USER): " + diff + " seconds!!!");

            long delayDiff = delayFinal - diff;
            if (delayDiff <= 0){

                Log.e(TAG, "DELAYDIFF: 0 seconds!!!");
                IntentHelper.goToLoginActivity(SplashActivity.this);

            } else {

                mHandlerBack.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        final long d3s = (new Date(System.currentTimeMillis())).getTime() / 1000;
                        Log.e(TAG, "DELAYDIFF: " + (d3s - d2s) + " seconds!!!");

                        IntentHelper.goToLoginActivity(SplashActivity.this);
                    }
                }, delayDiff);
            }
        }else{

            UserController.getInstance().doAutoLogin(new Response.Listener<User>() {
                @Override
                public void onResponse(final User response) {

                    final long d2s = (new Date(System.currentTimeMillis())).getTime() / 1000;
                    final long diff = (d2s - d1s);
                    Log.e(TAG, "INIT_PARAMS (AUTO-LOGIN): " + diff + " seconds!!! - InitDefaultDelay: " + delayFinal + "seconds!!!");

                    long delayDiff = delayFinal - diff;
                    Log.e(TAG, "delayDiff var = " + delayDiff);
                    if (delayDiff <= 0){

                        Log.e(TAG, "DELAYDIFF: 0 seconds!!!");
                        goToMain(response);

                    } else {

                        mHandlerBack.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                final long d3s = (new Date(System.currentTimeMillis())).getTime() / 1000;
                                Log.e(TAG, "DELAYDIFF: " + (d3s - d2s) + " seconds!!!");

                                goToMain(response);
                            }
                        }, delayDiff * 1000);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    IntentHelper.goToLoginActivity(SplashActivity.this);
                }
            });

        }
    }

    private void goToMain(final User response){
        Log.e(TAG, "goToMain() ----------------");

        ResourcesHelper.getInstance().downloadResData(response.resources, ctx);

        if (getIntent().getExtras() != null) {
            IntentHelper.goToMainActivity(SplashActivity.this,getIntent().getExtras());
            return;
        }
        IntentHelper.goToMainActivity(SplashActivity.this,null);

    }

}
