package com.app.runners.manager;

import com.android.volley.Response;
import com.app.runners.model.Notification;
import com.app.runners.model.Payment;
import com.app.runners.rest.RestApiServices;
import com.app.runners.rest.core.HRequest;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by sergiocirasa on 27/10/17.
 */

public class PaymentController extends BaseListController<Payment> {

    private static final String NOTIF_KEY = "paymets_v1";
    private static final String LAST_UPDATE_KEY = "payments_last_update";
    private static final long UPDATE_EACH_SECONDS = 3600 * 6;
    private static PaymentController mInstance = new PaymentController();

    public static synchronized PaymentController getInstance() {
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

    public ArrayList<Payment> parseData(String json) {
        return null;
    }

    protected Type typeToken() {
        return new TypeToken<ArrayList<Notification>>() {
        }.getType();
    }

    public HRequest createService(Response.Listener<ArrayList<Payment>> listener, Response.ErrorListener errorListener) {
        return RestApiServices.createGetPaymentsRequest(listener, errorListener);
    }

    public void didUpdateData() {

    }


}
