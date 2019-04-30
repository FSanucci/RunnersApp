package com.app.runners.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.app.runners.R;
import com.app.runners.model.ICallback;
import com.app.runners.rest.core.HRestEngine;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.app.runners.utils.Constants.BACKGROUND_LIMIT;


/**
 * Created by sergiocirasa on 14/8/17.
 */

public class AppController extends Application {
    private static final String TAG = "APP";
    private static final String APP_THEME = "app_theme";

    private static AppController mInstance;
    private HRestEngine mRestEngine;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mRestEngine = new HRestEngine(getApplicationContext());
        Storage.getInstance().init(this.getApplicationContext());
        Fresco.initialize(this);
    }

    public HRestEngine getRestEngine() {
        return mRestEngine;
    }

    public boolean isNetworkAvailable() {
        return mRestEngine.isNetworkAvailable();
    }

    //https://gist.github.com/steveliles/dd9035dbfaa776ee5a51
    public boolean isAppIsInBackground() {
        Context context = getApplicationContext();

        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public void saveDefaultTheme(String theme){
        Storage.getInstance().putPreferences(APP_THEME,theme);
    }

    public int getDefaultTheme(boolean actionBar){

        String theme = Storage.getInstance().getPreferences(APP_THEME);

        if(theme==null || theme.length()==0)
            return actionBar?R.style.AppTheme:R.style.AppTheme_NoActionBar;

        if(theme.equalsIgnoreCase("material-color-darkblue.css"))
            return actionBar?R.style.AppThemeDarkBlue:R.style.AppThemeDarkBlue_NoActionBar;

        if(theme.equalsIgnoreCase("material-color-darkorange.css"))
            return actionBar?R.style.AppThemeDarkOrange:R.style.AppThemeDarkOrange_NoActionBar;

        if(theme.equalsIgnoreCase("material-color-orange.css"))
            return actionBar?R.style.AppThemeOrange:R.style.AppThemeOrange_NoActionBar;

        if(theme.equalsIgnoreCase("material-color-purple.css"))
            return actionBar?R.style.AppThemePurple:R.style.AppThemePurple_NoActionBar;

        if(theme.equalsIgnoreCase("material-color-red.css"))
            return actionBar?R.style.AppThemeRed:R.style.AppThemeRed_NoActionBar;

        if(theme.equalsIgnoreCase("material-color-green.css"))
            return actionBar?R.style.AppThemeGreen:R.style.AppThemeGreen_NoActionBar;

        return actionBar?R.style.AppTheme:R.style.AppTheme_NoActionBar;
    }

    public void saveDate(){
        Log.e(TAG, "saveDate() -----------------");

        Date date = Calendar.getInstance().getTime();
        Log.e(TAG, "Date to Save: " + date.toString() + " - " + date.getTime());
        Log.e(TAG, "isAppIsInBackground: " + isAppIsInBackground());

       // if (isAppIsInBackground()){
            Storage.getInstance().setTimestamp(date.getTime());
       // }
    }

    public void appOnBackground(final ICallback callback){
        Log.e(TAG, "appOnBackground() -----------------");

        Long dateTimestamp = Storage.getInstance().getTimestamp();
        Log.e(TAG, "date: " + dateTimestamp);
        if (dateTimestamp > 0){

            Date dateNow = Calendar.getInstance().getTime();

            long diff = dateNow.getTime() - dateTimestamp;
            long minutes = ( diff / 60000 );
            Log.e(TAG, "diff: " + diff);
            Log.e(TAG, "minutos pasados: " + minutes);
            Log.e(TAG, "BACKGROUND_LIMIT: " + BACKGROUND_LIMIT);

            Storage.getInstance().setTimestamp(dateNow.getTime());

            if (minutes >= BACKGROUND_LIMIT){
                Log.e(TAG, "RefreshApp");
                callback.onRequest();
            }
        }
    }
}
