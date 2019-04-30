package com.app.runners.rest.post;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.runners.model.RunnerComment;
import com.app.runners.model.User;
import com.app.runners.rest.RestConstants;
import com.app.runners.rest.core.HRequest;
import com.app.runners.rest.core.HVolleyError;
import com.app.runners.rest.core.ParserUtils;

import org.json.JSONObject;

import java.nio.charset.Charset;

/**
 * Created by sergiocirasa on 20/8/17.
 */

public class HPostCommentRequest extends HRequest<User> {

    private static final String PATH = RestConstants.HOST + RestConstants.POST_COMMENT_SERVICE;
    private RunnerComment mComment;
    private User mUser;

    public HPostCommentRequest(User user, RunnerComment comment, Response.Listener<User> listener, Response.ErrorListener errorListener) {
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
                json.put("resourceId", mComment.resourceId);
            }
        }catch(Exception e){}
        String body = json.toString();
        return body.getBytes(Charset.forName("UTF-8"));
    }

    @Override
    protected User parseObject(Object obj) {
        if (obj instanceof JSONObject) {
            JSONObject dic = ParserUtils.parseResponse((JSONObject) obj);
            mUser = ParserUtils.parseUser(mUser,dic);
        }
        return mUser;
    }

    @Override
    protected HVolleyError parseError(Object obj){
        return ParserUtils.parseError((JSONObject)obj);
    }
}
