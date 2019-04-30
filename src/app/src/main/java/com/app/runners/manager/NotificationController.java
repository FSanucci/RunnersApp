package com.app.runners.manager;

import android.content.Intent;

import com.android.volley.Response;
import com.app.runners.model.Notification;
import com.app.runners.rest.RestApiServices;
import com.app.runners.rest.core.HRequest;
import com.app.runners.utils.AppController;
import com.app.runners.utils.Constants;
import com.app.runners.utils.Storage;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sergiocirasa on 23/8/17.
 */

public class NotificationController extends BaseListController<Notification> {

    private static final String NOTIF_READ_KEY = "notif_read_v2";
    private static final String NOTIF_KEY = "notif_v2";
    private static final String LAST_UPDATE_KEY = "zone_last_update_v2";
    private static final long UPDATE_EACH_SECONDS = 3600 * 1;
    private static NotificationController mInstance = new NotificationController();
    public int countUnReadNotif = 0;

    public static synchronized NotificationController getInstance() {
        return mInstance;
    }

    public String getObjectKey() {
        return NOTIF_KEY;
    }

    public String getLastUpdateKey() {
        return LAST_UPDATE_KEY;
    }

    public long getSecondsToUpdate() {
        return UPDATE_EACH_SECONDS;
    }

    public String getLocalJsonName() {
        return null;
    }

    public ArrayList<Notification> parseData(String json) {
        return null;
    }

    protected Type typeToken() {
        return new TypeToken<ArrayList<Notification>>() {
        }.getType();
    }

    public HRequest createService(Response.Listener<ArrayList<Notification>> listener, Response.ErrorListener errorListener) {
        return RestApiServices.createGetNotificationsRequest(listener, errorListener);
    }

    public void didUpdateData(){
        long mills = Storage.getInstance().getLongPreferences(NOTIF_READ_KEY);
        Date date = new Date(mills);
        if(mills==0){
            countUnReadNotif = mList.size();
            return;
        }

        int count = 0;
        for(Notification notif : mList){
            if(notif.date.after(date))
                count++;
        }

        countUnReadNotif = count;

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Constants.NOTIF_UPDATED);
        AppController.getInstance().sendBroadcast(broadcastIntent);
    }

    public void readNotifications() {
        countUnReadNotif = 0;
        Date date = new Date(System.currentTimeMillis());
        Storage.getInstance().putLongPreferences(NOTIF_READ_KEY, date.getTime());
    }

}
