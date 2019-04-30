package com.app.runners.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.app.runners.activity.AddCommentActivity;
import com.app.runners.activity.AddComunityCommentActivity;

import com.app.runners.activity.AddPersonalInfoActivity;
import com.app.runners.activity.AddRacingActivity;
import com.app.runners.activity.AddRacingWishActivity;
import com.app.runners.activity.FirstLoginActivity;
import com.app.runners.activity.ForgotPasswordActivity;
import com.app.runners.activity.LoginActivity;
import com.app.runners.activity.MainActivity;

/**
 * Created by sergiocirasa on 14/8/17.
 */

public class IntentHelper extends BaseIntentHelper {

    public static void goToLoginActivity(Activity activity) {
        launchIntent(activity, LoginActivity.class,true,null);
    }

    public static void goToForgotPasswordActivity(Activity activity) {
        launchIntent(activity, ForgotPasswordActivity.class);
    }

    public static void goToMainActivity(Activity activity, Bundle params) {
        launchIntent(activity, MainActivity.class,true,params);
    }

    public static void gotoAddPersonalNote(Activity activity){
        launchIntent(activity, AddPersonalInfoActivity.class);
    }

    public static void gotoAddComunityComment(Activity activity){
        launchIntent(activity, AddComunityCommentActivity.class);
    }

    public static void gotoAddRacingActivity(Activity activity){
        launchIntent(activity, AddRacingActivity.class);
    }

    public static void gotoAddRacingWishActivity(Activity activity){
        launchIntent(activity, AddRacingWishActivity.class);
    }

    public static void gotoMyFirstLoginActivity(Activity activity){
        launchIntent(activity, FirstLoginActivity.class);
    }


    public static void gotoAddCommentActivity(Fragment fragment, Bundle params, int requestCode){
        launchIntent(fragment, AddCommentActivity.class, fragment.getActivity(), params,requestCode);
    }


    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

}
