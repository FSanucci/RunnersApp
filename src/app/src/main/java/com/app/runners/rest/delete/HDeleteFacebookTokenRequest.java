package com.app.runners.rest.delete;

import com.android.volley.Response;
import com.app.runners.rest.RestConstants;
import com.app.runners.rest.core.HRequest;
import com.app.runners.rest.core.HVolleyError;
import com.app.runners.rest.core.ParserUtils;

import org.json.JSONObject;

/**
 * Created by sergiocirasa on 30/8/17.
 */

public class HDeleteFacebookTokenRequest extends HRequest<Void> {

    private static final String PATH = RestConstants.HOST + RestConstants.POST_FACEBOOK_ID_SERVICE;

    public HDeleteFacebookTokenRequest(Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        super(Method.DELETE, PATH, listener, errorListener);
    }

    @Override
    protected Void parseObject(Object obj) {
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj){
        return ParserUtils.parseError((JSONObject)obj);
    }
}
