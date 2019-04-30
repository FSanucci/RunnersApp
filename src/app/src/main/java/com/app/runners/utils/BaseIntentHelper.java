package com.app.runners.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.app.runners.model.Race;
import com.app.runners.utils.AppController;

import java.io.Serializable;

/**
 * Created by sergiocirasa on 14/8/17.
 */

public class BaseIntentHelper {
    protected static AppController appInstance = AppController.getInstance();

    public static void shareURL(Activity activity, String url) {
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    protected static <T> Intent launchIntent(Activity activity, Class<T> className) {
        return launchIntent(activity, className, false, null);
    }

    protected static <T> Intent launchIntentAndFinish(Activity activity, Class<T> className) {
        return launchIntent(activity, className, true, null);
    }

    protected static <T> Intent launchIntentAndFinish(Activity activity, Class<T> className, Bundle params) {
        return launchIntent(activity, className, true, params);
    }

    protected static <T> Intent launchIntent(Activity activity, Class<T> className, Race race, String path){
        return launchIntent(activity, className, false, null, race, path);
    }

    protected static <T> Intent launchIntent(Fragment fragment, Class<T> className, Context context, Bundle params, int requestCode) {
        Intent intent = new Intent(context, className);
        if (params != null) {
            intent.putExtras(params);
        }
        fragment.startActivityForResult(intent, requestCode);
        return intent;
    }

    protected static <T> Intent launchIntent(Activity activity, Class<T> className, boolean finish, Bundle params, int requestCode) {
        Intent intent = new Intent(appInstance, className);
        if (params != null) {
            intent.putExtras(params);
        }
        activity.startActivityForResult(intent, requestCode);

        if (finish) {
            activity.finish();
        }
        return intent;
    }

    protected static <T> Intent launchIntent(Fragment fragment, Class<T> className, boolean finish, Bundle params, int requestCode) {
        Intent intent = new Intent(appInstance, className);
        if (params != null) {
            intent.putExtras(params);
        }
        fragment.startActivityForResult(intent, requestCode);

        if (finish && fragment.getActivity()!=null) {
            fragment.getActivity().finish();
        }
        return intent;
    }

    protected static <T> Intent launchIntent(Activity activity, Class<T> className, Bundle params, int requestCode) {
        Intent intent = new Intent(appInstance, className);
        if (params != null) {
            intent.putExtras(params);
        }
        activity.startActivityForResult(intent, requestCode);

        return intent;
    }

    protected static <T> Intent launchIntent(Activity activity, Class<T> className, boolean finish, Bundle params, Race race, String path) {
        Intent intent = new Intent(appInstance, className);
        if (params != null) {
            intent.putExtras(params);
        }
        intent.putExtra("ID",race.getId());
        intent.putExtra("TITLE",race.getTitle());
        intent.putExtra("SUBTITLE", race.getSubtitle());
        intent.putExtra("PATH", path);

        activity.startActivity(intent);

        if (finish) {
            activity.finish();
        }
        return intent;
    }

    protected static <T> Intent launchIntent(Activity activity, Class<T> className, boolean finish, Bundle params) {
        Intent intent = new Intent(appInstance, className);
        if (params != null) {
            intent.putExtras(params);
        }
        activity.startActivity(intent);

        if (finish) {
            activity.finish();
        }
        return intent;
    }

    protected static <T> Intent launchIntent(Activity activity, Class<T> className, boolean finish, String key, Serializable serializable) {
        Intent intent = new Intent(appInstance, className);
        if (key != null) {
            intent.putExtra(key, serializable);
        }
        activity.startActivity(intent);

        if (finish) {
            activity.finish();
        }
        return intent;
    }
}