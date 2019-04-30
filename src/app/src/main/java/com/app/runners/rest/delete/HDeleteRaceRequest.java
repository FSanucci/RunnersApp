package com.app.runners.rest.delete;

import com.android.volley.Response;
import com.app.runners.model.Race;
import com.app.runners.model.User;
import com.app.runners.rest.core.HRequest;
import com.app.runners.rest.core.HVolleyError;
import com.app.runners.rest.core.ParserUtils;
import org.json.JSONObject;

public class HDeleteRaceRequest extends HRequest<User> {

    //private static final String PATH = RestConstants.HOST + RestConstants.DELETE_RACE_SERVICE;
    private Race mRace;
    private User mUser;

    public HDeleteRaceRequest(User user, Race race, String path, Response.Listener<User> listener, Response.ErrorListener errorListener) {
        super(Method.DELETE, path+race.getId(), listener, errorListener);
        mRace = race;
        mUser = user;
    }

    @Override
    public byte[] getBody() {
/*
        JSONObject json = new JSONObject();
        try {
            json.put("race", mRace.body);
        }catch(Exception e){}
        String body = json.toString();
        return body.getBytes(Charset.forName("UTF-8"));
*/
        return null;
    }

    @Override
    protected User parseObject(Object obj) {
/*
        if (obj instanceof JSONObject) {
            JSONObject dic = ParserUtils.parseResponse((JSONObject) obj);
            mUser = ParserUtils.parseUser(mUser,dic);
        }
*/
        return mUser;
    }

    @Override
    protected HVolleyError parseError(Object obj){
        return ParserUtils.parseError((JSONObject)obj);
    }
}