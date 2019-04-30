package com.app.runners.rest;


public class RestConstants {

    public static final int SOCKET_TIMEOUT_MS = 7000;
    public static final String API_KEY = "api_key";

    public static final String HOST = "https://runnersapp.com.ar/";
    //public static final String API_VERSION = "uat/api/v1.1/"; //<-DEV
    public static final String API_VERSION = "api/v1.1/"; //<-PROD

    public static final String LOGIN_SERVICE = API_VERSION + "runner/login/";
    public static final String FIRST_LOGIN_SERVICE = API_VERSION + "runner/firstLoginAndSetPassword/";
    public static final String GER_USER_SERVICE = API_VERSION + "runner/user/";
    public static final String RESET_PASSWORD_SERVICE = API_VERSION + "recoverPassword/";

    public static final String POST_RACE_SERVICE = API_VERSION + "user/race/";
    public static final String POST_RACEWISH_SERVICE = API_VERSION + "user/raceWish/";
    public static final String DELETE_RACE_SERVICE = API_VERSION + "user/race/";
    public static final String DELETE_RACEWISH_SERVICE = API_VERSION + "user/raceWish/";
    public static final String POST_COMMENT_SERVICE = API_VERSION + "user/runnerComment/";

    public static final String POST_DAY_COMMENT_SERVICE = API_VERSION + "runner/plan/commentDay/";

    public static final String GET_WALL_COMMENTS = API_VERSION + "user/wall/comments";
    public static final String DELETE_COMMENT_SERVICE = API_VERSION + "user/runnerComment/";
    public static final String POST_WALLCOMMENT_SERVICE = API_VERSION + "user/wall/comment/";


    public static final String POST_FACEBOOK_ID_SERVICE = API_VERSION + "user/facebookId/";
    public static final String POST_FIREBASE_TOKEN_SERVICE = API_VERSION + "user/firebase/";
    public static final String POST_UPLOAD_RESOURCE_SERVICE = API_VERSION + "resource/";
    public static final String POST_MEDICAL_CERTIFICATE_SERVICE = API_VERSION + "user/medicalCertificateImage/";
    public static final String POST_SWORN_STATEMENT_SERVICE = API_VERSION + "user/swornStatementExpirationImage/";
    public static final String POST_PROFILE_PICTURE_IMAGE = API_VERSION + "user/profilePictureImage/";
    public static final String GET_NOTIFICATIONS_SERVICE = API_VERSION + "runner/notification/";
    public static final String GET_PAYMENTS_SERVICE = API_VERSION + "runner/payments/";


    public static final String IMAGE_HOST = HOST + API_VERSION + "resource/bin/";
}
