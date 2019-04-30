package com.app.runners.rest.post;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.runners.rest.RestConstants;
import com.app.runners.rest.core.HMultipartRequest;
import com.app.runners.rest.core.HVolleyError;
import com.app.runners.rest.core.ParserUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;

/**
 * Created by sergiocirasa on 23/8/17.
 */

public class HUploadDocumentationRequest extends HMultipartRequest<String> {

    private static final String PATH = RestConstants.HOST + RestConstants.POST_UPLOAD_RESOURCE_SERVICE;
    private String mPath;

    public HUploadDocumentationRequest(String path, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, PATH, listener, errorListener);
        this.mPath = path;
        buildMultipartEntity();
    }

    @Override
    protected void buildMultipartEntity() {
        File file = new File(mPath);
        entity.addPart("resources", new FileBody(file, ContentType.create("image/jpg"), file.getName()));
        didFinishToBuildMultipartEntity();
    }

    @Override
    protected String parseObject(Object obj) {
        if(obj instanceof JSONObject) {
            JSONArray array = ((JSONObject)obj).optJSONArray("data");
            if(array!=null && array.length()>0) {
                JSONObject data = array.optJSONObject(0);
                String fileID = ParserUtils.optString(data, "_id");
                return fileID;
            }
        }
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj){
        return ParserUtils.parseError((JSONObject)obj);
    }
}