package com.app.runners.activity;

import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.app.runners.R;


public class VideoActivity  extends AppCompatActivity {

    private static final String TAG = "VIDEO_ACT";

    private VideoView mVideoView;
    private String videoPath;
    private ProgressBar loading;

    private MediaPlayer mediaPlayer;
    private int videoPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        mVideoView = (VideoView) findViewById(R.id.video_view);
        loading = (ProgressBar) findViewById(R.id.loading);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            videoPath = bundle.getString("VIDEO_PATH");

            if (videoPath!=null && !videoPath.isEmpty()) {
                setupVideo();
                playMainContent();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume::-----------------------------------");
        try{
            if (mediaPlayer != null) {
                mVideoView.seekTo(videoPosition);
                mVideoView.start();
            }
        }catch (Exception e) {
        }
    }

    @Override
    public void onPause() {
        if (mediaPlayer != null) {
            loading.setVisibility(View.VISIBLE);
            try {
                videoPosition = mVideoView.getCurrentPosition();
                mediaPlayer.pause();
            }catch(Exception e){
                loading.setVisibility(View.GONE);

            }
        }

        super.onPause();
        Log.e(TAG, "onPause::-----------------------------------");
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed::-----------------------------------");
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer = null;
            }catch(Exception e){}
        }
        super.onBackPressed();
    }

    private void setupVideo() {
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                loading.setVisibility(View.GONE);
                Log.e(TAG, "onCompletion!!!!!!!!:");
            }
        });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                loading.setVisibility(View.GONE);
                Log.e(TAG, "setOnPreparedListener-----------------------------!!!!!!!!:");
                mediaPlayer = mp;
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e(TAG, "Error MediaPlayer:onErrorListener ------------------------------------");
                return false;
            }
        });
    }

    private void playMainContent() {
        loading.setVisibility(View.VISIBLE);

        mVideoView.setMediaController(new MediaController(this));
        mVideoView.setVideoURI(Uri.parse(videoPath));
        mVideoView.requestFocus();

        //Set the surface holder height to the screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mVideoView.getHolder().setFixedSize(size.x, size.y);
        mVideoView.start();
    }


}
