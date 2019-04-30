package com.app.runners.rest.put;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.runners.rest.RestConstants;
import com.app.runners.rest.core.HRequest;
import com.app.runners.rest.core.HVolleyError;
import com.app.runners.rest.core.ParserUtils;

import org.json.JSONObject;

/**
 * Created by sergiocirasa on 23/8/17.
 */

public class HPutFirebaseTokenRequest extends HRequest<Void> {

    private static final String PATH = RestConstants.HOST + RestConstants.POST_FIREBASE_TOKEN_SERVICE;
    private String mToken;

    public HPutFirebaseTokenRequest(String token, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        super(Request.Method.PUT, PATH+token, listener, errorListener);
        mToken = token;
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
