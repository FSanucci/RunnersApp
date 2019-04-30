package com.app.runners.rest.get;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.runners.model.Notification;
import com.app.runners.rest.RestConstants;
import com.app.runners.rest.core.HRequest;
import com.app.runners.rest.core.HVolleyError;
import com.app.runners.rest.core.ParserUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import static com.app.runners.rest.core.ParserUtils.DATE_TIME_FORMAT;

/**
 * Created by sergiocirasa on 23/8/17.
 */

public class HGetNotificationsRequest extends HRequest<ArrayList<Notification>> {

    private static final String PATH = RestConstants.HOST + RestConstants.GET_NOTIFICATIONS_SERVICE;

    public HGetNotificationsRequest(Response.Listener<ArrayList<Notification>> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, PATH, listener, errorListener);
    }

    @Override
    protected ArrayList<Notification> parseObject(Object obj) {
        if (obj instanceof JSONObject) {
            ArrayList<Notification> list = new ArrayList<>();
            JSONArray jsonArray = ((JSONObject) obj).optJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj1 = jsonArray.optJSONObject(i);
                String body = ParserUtils.optString(obj1,"text");
                String title = ParserUtils.optString(obj1,"title");
                Date date = ParserUtils.parseDate(obj1,"deliverDate",DATE_TIME_FORMAT);
                String contentType = ParserUtils.optString(obj1, "contentType");
                Notification notif = new Notification(title,body,date, contentType);
                list.add(notif);
            }
            return list;
        }
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }
}
