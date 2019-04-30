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
 * Created by sergiocirasa on 28/2/18.
 */

public class HPostDayCommentRequest extends HRequest<User> {

    private static final String PATH = RestConstants.HOST + RestConstants.POST_DAY_COMMENT_SERVICE;
    private String mComment;
    private User mUser;

    public HPostDayCommentRequest(User user,int week,int day, String comment, Response.Listener<User> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, PATH+""+week+"/"+day+"/", listener, errorListener);
        mComment = comment;
        mUser = user;
    }

    @Override
    public byte[] getBody() {
        JSONObject json = new JSONObject();
        try {
            json.put("comment", mComment);
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
