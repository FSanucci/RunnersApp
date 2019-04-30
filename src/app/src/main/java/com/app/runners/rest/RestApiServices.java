package com.app.runners.rest;


import com.android.volley.Response;
import com.app.runners.model.Comment;
import com.app.runners.model.Documentation;
import com.app.runners.model.Notification;
import com.app.runners.model.Payment;
import com.app.runners.model.Race;
import com.app.runners.model.RunnerComment;
import com.app.runners.model.User;
import com.app.runners.model.WallComment;
import com.app.runners.rest.core.HRequest;
import com.app.runners.rest.delete.HDeleteCommentRequest;
import com.app.runners.rest.delete.HDeleteFacebookTokenRequest;
import com.app.runners.rest.delete.HDeleteFirebaseTokenRequest;
import com.app.runners.rest.delete.HDeleteRaceRequest;
import com.app.runners.rest.get.HGetNotificationsRequest;
import com.app.runners.rest.get.HGetPaymentsRequest;
import com.app.runners.rest.get.HGetUserRequest;
import com.app.runners.rest.get.HGetWallCommentsRequest;
import com.app.runners.rest.post.HFirstLoginRequest;
import com.app.runners.rest.post.HLoginRequest;
import com.app.runners.rest.post.HPostCommentRequest;
import com.app.runners.rest.post.HPostDayCommentRequest;
import com.app.runners.rest.post.HPostMedicalCertificateRequest;
import com.app.runners.rest.post.HPostProfilePictureImage;
import com.app.runners.rest.post.HPostRaceRequest;
import com.app.runners.rest.post.HPostRaceWishRequest;
import com.app.runners.rest.post.HPostSwornStatementRequest;
import com.app.runners.rest.post.HPostWallCommentRequest;
import com.app.runners.rest.post.HResetPasswordRequest;
import com.app.runners.rest.post.HUploadDocumentationRequest;
import com.app.runners.rest.put.HPutFacebookTokenRequest;
import com.app.runners.rest.put.HPutFirebaseTokenRequest;

import java.util.ArrayList;

public class RestApiServices {


    public static HRequest<?> createLoginRequest(String email, String password, Response.Listener<User> listener, Response.ErrorListener errorListener) {
        return new HLoginRequest(email,password,listener, errorListener);
    }

    public static HRequest<?> createFirstLoginRequest(String email, String password, Response.Listener<User> listener, Response.ErrorListener errorListener) {
        return new HFirstLoginRequest(email,password,listener, errorListener);
    }

    public static HRequest<?> createGetUserRequest(User user,Response.Listener<User> listener, Response.ErrorListener errorListener) {
        return new HGetUserRequest(user,listener, errorListener);
    }

    public static HRequest<?> createResetPasswordRequest(String email, Response.Listener<Void> listener, Response.ErrorListener errorListener){
        return new HResetPasswordRequest(email,listener,errorListener);
    }

    public static HRequest<?> createPostCommentRequest(User user, RunnerComment comment, Response.Listener<User> listener, Response.ErrorListener errorListener){
        return new HPostCommentRequest(user,comment,listener,errorListener);
    }

    public static HRequest<?> createPostWallCommentRequest(User user, RunnerComment comment, Response.Listener<String> listener, Response.ErrorListener errorListener){
        return new HPostWallCommentRequest(user,comment,listener,errorListener);
    }

    public static HRequest<?> createDeleteCommentRequest(User user, String path, Comment comment, Response.Listener<User> listener, Response.ErrorListener errorListener){
        return new HDeleteCommentRequest(user,path,comment,listener,errorListener);
    }

    public static HRequest<?> createPostRaceRequest(User user, Race race, Response.Listener<User> listener, Response.ErrorListener errorListener){
        return new HPostRaceRequest(user,race,listener,errorListener);
    }

    public static HRequest<?> createDeleteRaceRequest(User user, Race race, String path, Response.Listener<User> listener, Response.ErrorListener errorListener){
        return new HDeleteRaceRequest(user, race, path, listener, errorListener);
    }

    public static HRequest<?> createPostRaceWishRequest(User user, Race race, Response.Listener<User> listener, Response.ErrorListener errorListener){
        return new HPostRaceWishRequest(user,race,listener,errorListener);
    }

    public static HRequest<?> createPutFirebaseTokenRequest(String token, Response.Listener<Void> listener, Response.ErrorListener errorListener){
        return new HPutFirebaseTokenRequest(token,listener,errorListener);
    }

    public static HRequest<?> createDeleteFirebaseTokenRequest(Response.Listener<Void> listener, Response.ErrorListener errorListener){
        return new HDeleteFirebaseTokenRequest(listener,errorListener);
    }

    public static HRequest<?> createPutFacebookIdRequest(String fbId, Response.Listener<Void> listener, Response.ErrorListener errorListener){
        return new HPutFacebookTokenRequest(fbId,listener,errorListener);
    }

    public static HRequest<?> createDeleteFacebookIdRequest(Response.Listener<Void> listener, Response.ErrorListener errorListener){
        return new HDeleteFacebookTokenRequest(listener,errorListener);
    }

    public static HRequest<?> createPostMedicalCertificateRequest(String resource, Response.Listener<Documentation> listener, Response.ErrorListener errorListener){
        return new HPostMedicalCertificateRequest(resource,listener,errorListener);
    }

    public static HRequest<?> createPostSwornStatementRequest(String resource, Response.Listener<Documentation> listener, Response.ErrorListener errorListener){
        return new HPostSwornStatementRequest(resource,listener,errorListener);
    }

    public static HRequest<?> createPostProfilePictureImageRequest(String resource, User user, Response.Listener<User> listener, Response.ErrorListener errorListener){
        return new HPostProfilePictureImage(resource, user,listener,errorListener);
    }

    public static HRequest<?> createUploadDocumentationRequest(String path, Response.Listener<String> listener, Response.ErrorListener errorListener){
        return new HUploadDocumentationRequest(path,listener,errorListener);
    }

    public static HRequest<?> createGetNotificationsRequest(Response.Listener<ArrayList<Notification>> listener, Response.ErrorListener errorListener) {
        return new HGetNotificationsRequest(listener, errorListener);
    }

    public static HRequest<?> createGetPaymentsRequest(Response.Listener<ArrayList<Payment>> listener, Response.ErrorListener errorListener) {
        return new HGetPaymentsRequest(listener, errorListener);
    }


    public static HRequest<?> createPostDayCommentRequest(User user,int week,int day, String comment,Response.Listener<User> listener, Response.ErrorListener errorListener) {
        return new HPostDayCommentRequest(user, week, day, comment, listener, errorListener);
    }

    public static HRequest<?> createGetWallCommentsRequest(Response.Listener<ArrayList<WallComment>> listener, Response.ErrorListener errorListener){
        return new HGetWallCommentsRequest(listener, errorListener);
    }

}
