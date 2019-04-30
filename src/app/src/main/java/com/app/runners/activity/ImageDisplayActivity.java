package com.app.runners.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.app.runners.R;
import com.app.runners.rest.RestConstants;
import com.app.runners.utils.Storage;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by devcreative on 1/22/18.
 */

public class ImageDisplayActivity extends BaseActivity {

    private static final String TAG = "ZOOMIMAGE";

    private SimpleDraweeView mPreview;
    private String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        mPreview = (SimpleDraweeView) findViewById(R.id.image);
        image = Storage.getInstance().getImage();

        String path = RestConstants.IMAGE_HOST + image;
        Uri uri1 = Uri.parse(path);
        if (uri1 != null){
            mPreview.setImageURI(uri1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume-------------");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
/*
    @Override
    protected void setupToolbar(){
        super.setupToolbar();
    }
*/
}
