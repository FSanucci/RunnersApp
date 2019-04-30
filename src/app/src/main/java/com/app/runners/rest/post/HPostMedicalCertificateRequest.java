package com.app.runners.rest.post;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.runners.model.Documentation;
import com.app.runners.rest.RestConstants;
import com.app.runners.rest.core.HRequest;
import com.app.runners.rest.core.HVolleyError;
import com.app.runners.rest.core.ParserUtils;

import org.json.JSONObject;

/**
 * Created by sergiocirasa on 23/8/17.
 */

public class HPostMedicalCertificateRequest extends HRequest<Documentation> {

    private static final String PATH = RestConstants.HOST + RestConstants.POST_MEDICAL_CERTIFICATE_SERVICE;
    private String mResource;

    public HPostMedicalCertificateRequest(String resource, Response.Listener<Documentation> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, PATH+resource, listener, errorListener);
        mResource = resource;
    }

    @Override
    protected Documentation parseObject(Object obj) {
        if (obj instanceof JSONObject) {
            JSONObject dic = ParserUtils.parseResponse((JSONObject) obj);
            if (dic != null) {
                try {
                    Documentation documentation = ParserUtils.parseDocumentation(dic.optJSONObject("documentation"));
                    return documentation;
                } catch (Exception e) {

                }
            }
        }
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj){
        return ParserUtils.parseError((JSONObject)obj);
    }
}
