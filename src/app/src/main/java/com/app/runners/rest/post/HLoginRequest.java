package com.app.runners.rest.post;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.app.runners.model.Resource;
import com.app.runners.model.User;
import com.app.runners.rest.RestConstants;
import com.app.runners.rest.core.HRequest;
import com.app.runners.rest.core.HVolleyError;
import com.app.runners.rest.core.ParserUtils;
import com.app.runners.utils.ResourcesHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sergiocirasa on 20/8/17.
 */

public class HLoginRequest extends HRequest<User> {

    private static final String PATH = RestConstants.HOST + RestConstants.LOGIN_SERVICE;
    private String mEmail;
    private String mPassword;

    public HLoginRequest(String email, String password, Response.Listener<User> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, PATH, listener, errorListener);
        mEmail = email;
        mPassword = password;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
/*        headers.put("username", mEmail);
        headers.put("password", mPassword);*/

        String text = mEmail+":"+mPassword;
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        String account = Base64.encodeToString(data, Base64.DEFAULT);
        headers.put("Authorization","Basic "+account);
        return headers;
    }

    @Override
    protected User parseObject(Object obj) {
        if(obj instanceof JSONObject) {
            JSONObject dic = ParserUtils.parseResponse((JSONObject)obj);
            if(dic!=null){
                User user = new User();
                user.token = ParserUtils.optString(dic,"token");

                JSONObject dicGroup = dic.optJSONObject("group");
                user.group = ParserUtils.optString(dicGroup,"groupName");
                user.logo = ParserUtils.optString(dicGroup,"siteLogo");
                user.theme = ParserUtils.optString(dicGroup,"siteStyle");
                user.email = mEmail;
                user.password = mPassword;



                ArrayList<Resource> list = new ArrayList<>();
                JSONArray jsonArray = ((JSONObject) dicGroup).optJSONArray("advertising");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj1 = jsonArray.optJSONObject(i);
                    String hash = ParserUtils.optString(obj1,"hash");
                    String url = ParserUtils.optString(obj1,"url");
                    String type = ParserUtils.optString(obj1,"type");

                    Resource res = new Resource(hash, url, type);
                    list.add(res);
                }
                user.resources = list;

                return user;
            }
        }
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj){
        return ParserUtils.parseError((JSONObject)obj);
    }
}
