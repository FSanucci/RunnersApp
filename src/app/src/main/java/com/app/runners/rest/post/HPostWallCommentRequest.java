package com.app.runners.rest.post;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.runners.model.RunnerComment;
import com.app.runners.model.User;
import com.app.runners.rest.RestConstants;
import com.app.runners.rest.core.HRequest;
import com.app.runners.rest.core.HVolleyError;
import com.app.runners.rest.core.ParserUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;

/**
 * Created by devcreative on 1/23/18.
 */

public class HPostWallCommentRequest extends HRequest<String> {

    private static final String PATH = RestConstants.HOST + RestConstants.POST_WALLCOMMENT_SERVICE;
    private RunnerComment mComment;
    private User mUser;

    public HPostWallCommentRequest(User user, RunnerComment comment, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, PATH, listener, errorListener);
        mComment = comment;
        mUser = user;
    }

    @Override
    public byte[] getBody() {
        JSONObject json = new JSONObject();
        try {
            if (mComment.body != null){
                json.put("comment", mComment.body);
            } else {
                //En caso de realizar chat Comunitario con imágenes
                //json.put("resourceId", mComment.resourceId);
            }
        }catch(Exception e){}
        String body = json.toString();
        return body.getBytes(Charset.forName("UTF-8"));
    }

    @Override
    protected String parseObject(Object obj) {
        String id = null;
        if (obj instanceof JSONObject) {
            JSONObject dic = ParserUtils.parseResponse((JSONObject) obj);
            //JSONObject obj1 = jsonArray.optJSONObject(0);
            String _id = ParserUtils.optString(dic,"_id");
            id = _id;
        }
        return id;
    }

    @Override
    protected HVolleyError parseError(Object obj){
        return ParserUtils.parseError((JSONObject)obj);
    }
}
