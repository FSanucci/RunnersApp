package com.app.runners.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.runners.R;

/**
 * Created by sergiocirasa on 21/8/17.
 */


public class PermissionsHelper {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 3;

    public static final int REQUEST_READ_CALENDAR_PERMISSION = 4;
    public static final int REQUEST_WRITE_CALENDAR_PERMISSION = 5;

    private Activity activity;

    public PermissionsHelper(Activity ctx) {
        this.activity = ctx;
    }


    public static boolean checkPermissionForLocation(){
        int result = ContextCompat.checkSelfPermission(AppController.getInstance().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkPermissionForExternalStorage(){
        int result = ContextCompat.checkSelfPermission(AppController.getInstance().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkPermissionForCamera(){
        int result = ContextCompat.checkSelfPermission(AppController.getInstance().getApplicationContext(), Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    public void requestPermissionForCamera(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)){
            showPermissionError(activity, R.string.permision_error_no_camara);
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionForLocation(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)){
            showPermissionError(activity, R.string.permision_error_no_write_data);
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionForExternalStorage(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            showPermissionError(activity, R.string.permision_error_no_write_data);
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    public void showPermissionError(final Activity activity, int error){
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        SnackBarHelper.makeError(viewGroup, error)
                .setActionTextColor(activity.getResources().getColor(R.color.colorGreen))
                .setAction("Ir", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        IntentHelper.startInstalledAppDetailsActivity(activity);
                    }
                }).show();
    }
}
