package com.app.runners.rest.get;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.runners.model.User;
import com.app.runners.rest.RestConstants;
import com.app.runners.rest.core.HRequest;
import com.app.runners.rest.core.HVolleyError;
import com.app.runners.rest.core.ParserUtils;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by sergiocirasa on 20/8/17.
 */

public class HGetUserRequest extends HRequest<User> {

    private static final String PATH = RestConstants.HOST + RestConstants.GER_USER_SERVICE;
    private User mUser;

    public HGetUserRequest(User user, Response.Listener<User> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, PATH + "?random" + new Date().getTime(), listener, errorListener);
        mUser = user;
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
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }
}
