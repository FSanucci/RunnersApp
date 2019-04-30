package com.app.runners.manager;

import android.util.Log;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.runners.model.Comment;
import com.app.runners.model.Documentation;
import com.app.runners.model.Race;
import com.app.runners.model.RunnerComment;
import com.app.runners.model.User;
import com.app.runners.model.WallComment;
import com.app.runners.rest.RestApiServices;
import com.app.runners.rest.core.HRequest;
import com.app.runners.rest.core.HVolleyError;
import com.app.runners.rest.core.ParserUtils;
import com.app.runners.utils.AppController;
import com.app.runners.utils.Storage;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by sergiocirasa on 14/8/17.
 */

public class UserController {

    private static final String TAG = "USER_MANAGER";
    private static final String USER_KEY = "current_user";
    private static final String EMAIL_KEY = "last_email_logged";
    private static final String FIREBASE_TOKEN = "firebase_token";
    private static final String FIREBASE_TOKEN_CHANGED = "firebase_token_changed";

    private static UserController mInstance = new UserController();

    public static synchronized UserController getInstance() {
        return mInstance;
    }

    private User mUser;

    public UserController() {
        mUser = loadUser();
    }

    public User getSignedUser() {
        return mUser;
    }

    public void login(String email, String password, final Response.Listener<User> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createLoginRequest(email, password, new Response.Listener<User>() {
            @Override
            public void onResponse(User user) {
                if (user != null) {
                    Log.e("Login", "Successs");
                    mUser = user;
                    saveUser(mUser);
                    HRequest.authorizationHeaderValue = mUser.token;
                    updateUser(listener, errorListener);
                    sendFirebaseTokenIfNeeded();
                } else {
                    errorListener.onErrorResponse(HVolleyError.unexpectedError());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
                Log.e("Login", "Failed");
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }

    public void updateUser(final Response.Listener<User> listener, final Response.ErrorListener errorListener) {
        Log.e("TAG", mUser.token + " - " + mUser.email);
        HRequest request = RestApiServices.createGetUserRequest(mUser, new Response.Listener<User>() {
            @Override
            public void onResponse(User user) {
                mUser = user;
                saveUser(mUser);
                listener.onResponse(mUser);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
                Log.e("updateUser ERROR", volleyError.getMessage());
                Log.e("Login", "Failed");
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    public void doAutoLogin(Response.Listener<User> listener, Response.ErrorListener errorListener) {
        login(mUser.email, mUser.password, listener, errorListener);
    }

    public void passRecovery(String email, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createResetPasswordRequest(email, listener, errorListener);
        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    public void logout(final Response.Listener<Void> listener, final Response.ErrorListener errorListener) {

        Storage.getInstance().putBoolPreferences(FIREBASE_TOKEN_CHANGED, true);
        HRequest request = RestApiServices.createDeleteFirebaseTokenRequest(new Response.Listener<Void>() {
            @Override
            public void onResponse(Void v) {
                HRequest.authorizationHeaderValue = null;
                listener.onResponse(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                HRequest.authorizationHeaderValue = null;
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request);

        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (IOException e) {
        }

        NotificationController.getInstance().clearData();
        mUser = null;
        removeUser();
    }


    private void saveUser(User user) {
        String json = ParserUtils.parseToJSON(user);
        Storage.getInstance().putPreferences(USER_KEY, json);
        Storage.getInstance().putPreferences(EMAIL_KEY, user.email);
    }

    public String lastEmailLogued() {
        return Storage.getInstance().getPreferences(EMAIL_KEY);
    }

    private User loadUser() {
        String json = Storage.getInstance().getPreferences(USER_KEY);

        if (json != null) {
            User user = ParserUtils.parseFromJSON(json, User.class);
            if (user != null)
                return user;
        }
        return null;
    }

    private void removeUser() {
        Storage.getInstance().remove(USER_KEY);
    }

    public void postComment(final RunnerComment comment, final Response.Listener<ArrayList<RunnerComment>> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createPostCommentRequest(mUser, comment, new Response.Listener<User>() {
            @Override
            public void onResponse(User user) {
                if (user.plan == null) {
                    listener.onResponse(null);
                    return;
                }
                mUser = user;

                saveUser(mUser);
                listener.onResponse(mUser.plan.runnerComments);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    public void postWallComment(final RunnerComment comment, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createPostWallCommentRequest(mUser, comment, new Response.Listener<String>() {
            @Override
            public void onResponse(String id) {
/*
                if(user.plan==null){
                    listener.onResponse(null);
                    return;
                }
                mUser = user;

                saveUser(mUser);
                listener.onResponse(mUser.plan.runnerComments);
*/
                listener.onResponse(id);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }


    public void postDayComment(int week, int day, final String comment, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createPostDayCommentRequest(mUser, week, day, comment, new Response.Listener<User>() {
            @Override
            public void onResponse(User user) {
                if (user.plan == null) {
                    listener.onResponse(null);
                    return;
                }
                mUser = user;

                saveUser(mUser);
                listener.onResponse(comment);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }


    public void postRace(final Race race, final Response.Listener<Void> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createPostRaceRequest(mUser, race, new Response.Listener<User>() {
            @Override
            public void onResponse(User user) {
                mUser = user;
                saveUser(mUser);
                listener.onResponse(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    public void firstLogin(String email, String password, final Response.Listener<User> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createFirstLoginRequest(email, password, new Response.Listener<User>() {
            @Override
            public void onResponse(User user) {
                if (user != null) {
                    Log.e("Login", "Successs");
                    mUser = user;
                    saveUser(mUser);
                    HRequest.authorizationHeaderValue = mUser.token;
                    updateUser(listener, errorListener);
                    sendFirebaseTokenIfNeeded();
                } else {
                    errorListener.onErrorResponse(HVolleyError.unexpectedError());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
                Log.e("Login", "Failed");
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }

    public void deleteComment(final Comment comment, final String path, final Response.Listener<Void> listener, final Response.ErrorListener errorListener) {

        HRequest request = RestApiServices.createDeleteCommentRequest(mUser, path + comment.getId(), comment, new Response.Listener<User>() {
            @Override
            public void onResponse(User user) {
                saveUser(mUser);
                listener.onResponse(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    public void deleteRace(final Race race, final String path, final Response.Listener<Void> listener, final Response.ErrorListener errorListener) {

        HRequest request = RestApiServices.createDeleteRaceRequest(mUser, race, path, new Response.Listener<User>() {
            @Override
            public void onResponse(User user) {
                //mUser.races.remove(race);
                //mUser = user;
                saveUser(mUser);
                listener.onResponse(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    public void updateUserRequestStatus(User userUpdate) {
        this.mUser = userUpdate;
    }

    public void postRaceWish(final Race race, final Response.Listener<Void> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createPostRaceWishRequest(mUser, race, new Response.Listener<User>() {
            @Override
            public void onResponse(User user) {
                mUser = user;
                saveUser(mUser);
                listener.onResponse(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    public void refreshFirebaseToken(String token, final Response.Listener<Void> listener, final Response.ErrorListener errorListener) {

        Log.e("FB_TOKEN", token);
        Storage.getInstance().putPreferences(FIREBASE_TOKEN, token);
        Storage.getInstance().putBoolPreferences(FIREBASE_TOKEN_CHANGED, true);

        if (mUser == null) {
            if (errorListener != null)
                errorListener.onErrorResponse(null);
            return;
        }

        mUser.firebaseToken = token;
        saveUser(mUser);

        HRequest request = RestApiServices.createPutFirebaseTokenRequest(mUser.firebaseToken, new Response.Listener<Void>() {
            @Override
            public void onResponse(Void v) {
                if (listener != null)
                    listener.onResponse(v);

                Storage.getInstance().putBoolPreferences(FIREBASE_TOKEN_CHANGED, false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (errorListener != null)
                    errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    public void sendFirebaseTokenIfNeeded() {

        String token = Storage.getInstance().getPreferences(FIREBASE_TOKEN);
        Log.e("FB_TOKEN", token);
        if (token == null || token.length() == 0)
            return;

        boolean needUpdate = Storage.getInstance().getBoolPreferences(FIREBASE_TOKEN_CHANGED);
        if (needUpdate) {
            UserController.getInstance().refreshFirebaseToken(token, new Response.Listener<Void>() {
                @Override
                public void onResponse(Void response) {
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
        }
    }

    public void uploadSwornStatement(final String path, final Response.Listener<Void> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createUploadDocumentationRequest(path, new Response.Listener<String>() {
            @Override
            public void onResponse(String resource) {

                HRequest request = RestApiServices.createPostSwornStatementRequest(resource, new Response.Listener<Documentation>() {
                    @Override
                    public void onResponse(Documentation doc) {
                        if (doc != null) {
                            mUser.documentation = doc;
                            saveUser(mUser);
                            listener.onResponse(null);
                        } else {
                            if (errorListener != null)
                                errorListener.onErrorResponse(null);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (errorListener != null)
                            errorListener.onErrorResponse(volleyError);
                    }
                });

                AppController.getInstance().getRestEngine().addToRequestQueue(request);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (errorListener != null)
                    errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    public void uploadMedicalCertificate(final String path, final Response.Listener<Void> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createUploadDocumentationRequest(path, new Response.Listener<String>() {
            @Override
            public void onResponse(String resource) {

                HRequest request = RestApiServices.createPostMedicalCertificateRequest(resource, new Response.Listener<Documentation>() {
                    @Override
                    public void onResponse(Documentation doc) {
                        if (doc != null) {
                            mUser.documentation = doc;
                            saveUser(mUser);
                            listener.onResponse(null);
                        } else {
                            if (errorListener != null)
                                errorListener.onErrorResponse(null);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (errorListener != null)
                            errorListener.onErrorResponse(volleyError);
                    }
                });

                AppController.getInstance().getRestEngine().addToRequestQueue(request);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (errorListener != null)
                    errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    public void uploadProfilePicture(final String path, final Response.Listener<Void> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createUploadDocumentationRequest(path, new Response.Listener<String>() {
            @Override
            public void onResponse(String resource) {
                Log.e(TAG, "createUploadDocumentationRequest() -> resource: " + resource);

                HRequest request = RestApiServices.createPostProfilePictureImageRequest(resource, mUser, new Response.Listener<User>() {
                    @Override
                    public void onResponse(User user) {
                        mUser = user;
                        saveUser(mUser);
                        listener.onResponse(null);


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (errorListener != null)
                            errorListener.onErrorResponse(volleyError);

                    }
                });

                AppController.getInstance().getRestEngine().addToRequestQueue(request);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (errorListener != null)
                    errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    public void updateFBData(String userId) {
        mUser.facebookId = userId;
        saveUser(mUser);

        HRequest request = RestApiServices.createPutFacebookIdRequest(userId, new Response.Listener<Void>() {
            @Override
            public void onResponse(Void v) {
                Log.e("OKOKOK", "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("ERRRROOO", "");
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }

    public void deleteFBData() {
        HRequest request = RestApiServices.createDeleteFacebookIdRequest(new Response.Listener<Void>() {
            @Override
            public void onResponse(Void v) {
                Log.e("OKOKOK", "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("ERRRROOO", "");
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }

    public void uploadImageForChat(final String path, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createUploadDocumentationRequest(path, new Response.Listener<String>() {
            @Override
            public void onResponse(String resource) {

                listener.onResponse(resource);
/*
                HRequest request = RestApiServices.createUploadDocumentationRequest(resource,new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("POSTIMAGE", "Upload Image success");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if(errorListener!=null)
                            errorListener.onErrorResponse(volleyError);
                    }
                });

                AppController.getInstance().getRestEngine().addToRequestQueue(request);
*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (errorListener != null)
                    errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    public void getWallComments(final Response.Listener<ArrayList<WallComment>> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createGetWallCommentsRequest(new Response.Listener<ArrayList<WallComment>>() {
            @Override
            public void onResponse(ArrayList<WallComment> response) {
                Log.i("WALLCOMMENT", "onResponse success -> createGetWallCommentRequest");
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (errorListener != null)
                    errorListener.onErrorResponse(volleyError);
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }

}
