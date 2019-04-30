package com.app.runners.rest.post;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.runners.model.Race;
import com.app.runners.model.User;
import com.app.runners.rest.RestConstants;
import com.app.runners.rest.core.HRequest;
import com.app.runners.rest.core.HVolleyError;
import com.app.runners.rest.core.ParserUtils;

import org.json.JSONObject;

import java.nio.charset.Charset;

public class HPostProfilePictureImage extends HRequest<User> {

    private static final String PATH = RestConstants.HOST + RestConstants.POST_PROFILE_PICTURE_IMAGE;
    private String mResource;
    private User mUser;

    public HPostProfilePictureImage(String resource, User user, Response.Listener<User> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, PATH+resource, listener, errorListener);
        mResource = resource;
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
    protected HVolleyError parseError(Object obj){
        return ParserUtils.parseError((JSONObject)obj);
    }
}