package com.app.runners.rest.get;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.runners.model.User;
import com.app.runners.model.WallComment;
import com.app.runners.rest.RestConstants;
import com.app.runners.rest.core.HRequest;
import com.app.runners.rest.core.HVolleyError;
import com.app.runners.rest.core.ParserUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by devcreative on 1/23/18.
 */

public class HGetWallCommentsRequest extends HRequest<ArrayList<WallComment>>{

    private static final String PATH = RestConstants.HOST + RestConstants.GET_WALL_COMMENTS;

    public HGetWallCommentsRequest(Response.Listener<ArrayList<WallComment>> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, PATH, listener, errorListener);
    }

    @Override
    protected ArrayList<WallComment> parseObject(Object obj) {
        if (obj instanceof JSONObject) {
            ArrayList<WallComment> list = new ArrayList<>();
            JSONArray jsonArray = ((JSONObject) obj).optJSONArray("data");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj1 = jsonArray.optJSONObject(i);

                String _id = ParserUtils.optString(obj1,"_id");
                Date creationDate = new Date();
                String textDate = null;
                boolean owner = false;
                boolean coach = false;

                try {
                    textDate = (String) obj1.get("creationDate");
                    creationDate = format.parse(textDate);
                    owner = (boolean) obj1.get("owner");
                    coach = (boolean) obj1.get("coach");
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
                String author = ParserUtils.optString(obj1,"author");
                String text = ParserUtils.optString(obj1,"text");
                WallComment comment = new WallComment(_id, owner, coach, creationDate, author, text);

                list.add(comment);
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
