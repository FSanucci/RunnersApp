package com.app.runners.rest.post;

/**
 * Created by devcreative on 11/30/17.
 */

import android.util.Log;

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

public class HPostRaceWishRequest extends HRequest<User> {

    private static final String PATH = RestConstants.HOST + RestConstants.POST_RACEWISH_SERVICE;
    private Race mRace;
    private User mUser;

    public HPostRaceWishRequest(User user,Race race, Response.Listener<User> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, PATH, listener, errorListener);
        mRace = race;
        mUser = user;
    }

    @Override
    public byte[] getBody() {
        JSONObject json = new JSONObject();
        try {
            json.put("race", mRace.title);
            json.put("distance", mRace.km);
            json.put("date", ParserUtils.parseDate(mRace.runningDate));
        }catch(Exception e){}
        String body = json.toString();
        Log.e("JOSN", body);
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