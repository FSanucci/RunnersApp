package com.app.runners.rest.core;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.SSLCertificateSocketFactory;
import android.net.SSLSessionCache;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.app.runners.manager.UserController;
import com.app.runners.model.User;
import com.app.runners.utils.AppController;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by sergio on 10/24/15.
 */
public class HRestEngine{

    public static final String TAG = HRestEngine.class.getSimpleName();
    //public static final int SOCKET_TIMEOUT_MS = 7000;
    //public static final int SOCKET_TIMEOUT_MS = 100000;
    public static final int SOCKET_TIMEOUT_MS = 180 * 1000; // 3 minutes


    private RequestQueue mRequestQueue;
    private Context mContext;
    private HurlStack mStack;

    public HRestEngine(Context ctx){
        mContext = ctx;
        mRequestQueue = Volley.newRequestQueue(mContext);

    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setRetryPolicy(new DefaultRetryPolicy(
                SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        getRequestQueue().getCache().clear();
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(final HRequest<T> req, boolean autologinEnabled) {
        if(req!=null) {
            req.setRetryPolicy(new DefaultRetryPolicy(
                    SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            if(autologinEnabled) {
                req.loginErrorListener = new LoginErrorListener<T>() {
                    @Override
                    public void onErrorResponse(HRequest<T> request) {

                        UserController.getInstance().doAutoLogin(new Response.Listener<User>() {
                            @Override
                            public void onResponse(User responseUser) {
                                getRequestQueue().add(req);
                            }

                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                req.notifyError(error);
                            }
                        });
                    }
                };
            }

            getRequestQueue().getCache().clear();
            getRequestQueue().add(req);
        }
    }

    public <T> void addToRequestQueue(final HRequest<T> req) {
        addToRequestQueue(req,true);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public static void setToken(String token){
        HRequest.authorizationHeaderValue = token;
    }

    public boolean isNetworkAvailable(){
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

}

