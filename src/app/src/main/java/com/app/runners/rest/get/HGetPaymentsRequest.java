package com.app.runners.rest.get;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.runners.model.Payment;
import com.app.runners.rest.RestConstants;
import com.app.runners.rest.core.HRequest;
import com.app.runners.rest.core.HVolleyError;
import com.app.runners.rest.core.ParserUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import static com.app.runners.rest.core.ParserUtils.DATE_TIME_FORMAT;

/**
 * Created by sergiocirasa on 27/10/17.
 */

public class HGetPaymentsRequest extends HRequest<ArrayList<Payment>> {

    private static final String PATH = RestConstants.HOST + RestConstants.GET_PAYMENTS_SERVICE;

    public HGetPaymentsRequest(Response.Listener<ArrayList<Payment>> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, PATH, listener, errorListener);
    }

    @Override
    protected ArrayList<Payment> parseObject(Object obj) {
        if (obj instanceof JSONObject) {
            ArrayList<Payment> list = new ArrayList<>();
            JSONArray jsonArray = ((JSONObject) obj).optJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj1 = jsonArray.optJSONObject(i);
                String desc = ParserUtils.optString(obj1,"description");
                long amount = obj1.optLong("amount",0);
                Date date = ParserUtils.parseDate(obj1,"date",DATE_TIME_FORMAT);
                Payment payment = new Payment(amount,desc,date);
                list.add(payment);
            }
            return list;
        }
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }
}
