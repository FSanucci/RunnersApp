package com.app.runners.rest.core;

/**
 * Created by sergio on 8/11/16.
 */
public interface LoginErrorListener<T>  {
    public void onErrorResponse(HRequest<T> request);
}