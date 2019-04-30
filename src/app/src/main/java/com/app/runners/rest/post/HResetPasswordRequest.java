package com.app.runners.rest.post;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.runners.rest.RestConstants;
import com.app.runners.rest.core.HRequest;
import com.app.runners.rest.core.HVolleyError;
import com.app.runners.rest.core.ParserUtils;

import org.json.JSONObject;

import java.nio.charset.Charset;

/**
 * Created by sergiocirasa on 20/8/17.
 */

public class HResetPasswordRequest extends HRequest<Void> {

    private static final String PATH = RestConstants.HOST + RestConstants.RESET_PASSWORD_SERVICE;
    private String mEmail;

    public HResetPasswordRequest(String email, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, PATH, listener, errorListener);
        mEmail = email;
    }

    @Override
    public byte[] getBody() {
        JSONObject json = new JSONObject();
        try {
            json.put("email", mEmail);
        }catch(Exception e){}
        String body = json.toString();
        return body.getBytes(Charset.forName("UTF-8"));
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
