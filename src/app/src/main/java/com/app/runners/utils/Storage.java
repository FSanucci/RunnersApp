package com.app.runners.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

/**
 * Created by sergiocirasa on 14/8/17.
 */

public class Storage {

    private static final String TAG = "Storage";
    private static final String SHARED_PREFERENCES = "runners_v1";
    private static final String OLD_SHARED_PREFERENCES = "runners_v0";

    private Context ctx;
    private SharedPreferences sharedPreferences;
    private static Storage instance;

    private String dataImage;
    private Date backgroundTimestamp = null;

    public static synchronized Storage getInstance() {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }

    public void init(Context ctx) {
        this.ctx = ctx;
        if (sharedPreferences == null) {
            sharedPreferences = ctx.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
            clearOldSharedPreferenceIfNeeded();
        }
    }

    public Context getContext() {
        return ctx;
    }

    public String getPreferences(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void putPreferences(String key, String value) {
        if (key != null && key.trim().length() > 0 && value != null && value.trim().length() > 0) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.commit();
        }
    }

    public void clearOldSharedPreferenceIfNeeded(){
        if(getBoolPreferences(OLD_SHARED_PREFERENCES)) {
            SharedPreferences sp = ctx.getSharedPreferences(OLD_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.commit();
            putBoolPreferences(OLD_SHARED_PREFERENCES,false);
        }
    }

    public long getLongPreferences(String key) {
        return sharedPreferences.getLong(key,0);
    }

    public void putLongPreferences(String key, long value) {
        if (key != null && key.trim().length() > 0) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(key,value);
            editor.commit();
        }
    }

    public boolean getBoolPreferences(String key) {
        return sharedPreferences.getBoolean(key,true);
    }

    public void putBoolPreferences(String key, boolean value) {
        if (key != null && key.trim().length() > 0) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(key,value);
            editor.commit();
        }
    }

    public void remove(String key){
        if (key != null && key.trim().length() > 0) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(key);
            editor.commit();
        }
    }

    public void setImage(String data){ this.dataImage = data; }
    public String getImage(){ return this.dataImage; }

    public Long getTimestamp() {
       return this.getLongPreferences("TIMESTAMP_USE_DATE");
    }

    public void setTimestamp(Long backgroundTimestamp) {
        this.putLongPreferences("TIMESTAMP_USE_DATE", backgroundTimestamp);
    }
}