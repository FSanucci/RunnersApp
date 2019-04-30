package com.app.runners.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.runners.BuildConfig;
import com.app.runners.R;
import com.app.runners.fragment.CommunityChatFragment;
import com.app.runners.fragment.NotificationFragment;
import com.app.runners.fragment.PaymentsFragment;
import com.app.runners.fragment.PersonalInfoFragment;
import com.app.runners.fragment.ProfessionalNotesFragment;
import com.app.runners.fragment.StatisticsFragment;
import com.app.runners.fragment.TrainingPlanFragment;
import com.app.runners.fragment.UserDocsFragment;
import com.app.runners.fragment.racingsFragments.TabNavigatorRacings;
import com.app.runners.manager.NotificationController;
import com.app.runners.manager.UserController;
import com.app.runners.model.ICallback;
import com.app.runners.model.User;
import com.app.runners.utils.AppController;
import com.app.runners.utils.Constants;
import com.app.runners.utils.IntentHelper;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MAIN_ACTV";

    private NavigationView mNavigationView;
    private View mHeaderContainerView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private Fragment mCurrentFragment;
    private TextView mBadgeTextView;
    private String mGroupName;
    private IntentFilter filter;
    private boolean mFirstTime=true;
    private SimpleDraweeView mNavLogo;
    private ImageView mNavLogoDefault;
    private LoginButton mFBLoginButton;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker mAccessTokenTracker;
    private int mSelectedItemMenu;
    protected ProgressDialog pDialog;

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            UserController.getInstance().updateUser(new Response.Listener<User>() {
                @Override
                public void onResponse(User response) {
                    if(mCurrentFragment instanceof TrainingPlanFragment){
                        ((TrainingPlanFragment)mCurrentFragment).refreshContent();
                    }
                    updateNotificationBadge();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    updateNotificationBadge();
                }
            });
        /*    String body = intent.getExtras().getString("body");
            if (body != null) {
                Toast.makeText(AppController.getInstance().getApplicationContext(), body, Toast.LENGTH_LONG).show();
            }*/
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(AppController.getInstance().getDefaultTheme(false), true);

        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        View v = mNavigationView.getHeaderView(0);
        mNavLogo = (SimpleDraweeView) v.findViewById(R.id.nav_logo);
        mNavLogoDefault = (ImageView) v.findViewById(R.id.nav_logo_default);

        User user = UserController.getInstance().getSignedUser();
        if(user!=null && user.logo!=null){
            mNavLogo.setVisibility(View.VISIBLE);
            mNavLogoDefault.setVisibility(View.GONE);
            Uri uri1 = Uri.parse(user.getLogoPath());
            if (uri1 != null)
                mNavLogo.setImageURI(uri1);
        }else{
            mNavLogo.setVisibility(View.GONE);
            mNavLogoDefault.setVisibility(View.VISIBLE);
        }


        mNavigationView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {

                mNavigationView.removeOnLayoutChangeListener(this);
                mHeaderContainerView = mNavigationView.findViewById(R.id.menu_header);
                mBadgeTextView = (TextView) mNavigationView.findViewById(R.id.badge);
                updateNotificationBadge();
            }
        });

        mGroupName = UserController.getInstance().getSignedUser().group;

        filter = new IntentFilter();
        filter.addAction(Constants.NOTIF_UPDATED);

        // Si esta cerrada la app, llega un push y se presiona sobre este en la barra de notificaciones
        //Entonces se abrira esta activity y se recibiran los parametros
        if (getIntent().getExtras() != null) {
            if(getIntent().getExtras().get("google.message_id")!=null){
                gotoNotification();
            }else{
                setupFirstFragment();
            }
        }else{
            setupFirstFragment();
        }

        //Firebase
        String token = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("general");
        //FirebaseMessaging.getInstance().subscribeToTopic();

        if(BuildConfig.facebookEnable) {
            mFBLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
            mFBLoginButton.setReadPermissions("email");

            mCallbackManager = CallbackManager.Factory.create();
            mFBLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    if(loginResult.getAccessToken()!=null && loginResult.getAccessToken().getUserId()!=null){
                        UserController.getInstance().updateFBData(loginResult.getAccessToken().getUserId());
                        mNavigationView.getMenu().findItem(R.id.fb_login).setVisible(false);
                        mNavigationView.getMenu().findItem(R.id.fb_logout).setVisible(true);
                    }
                }

                @Override
                public void onCancel() {
                    // App code
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                }
            });

            mAccessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                           AccessToken currentAccessToken) {
                    if (currentAccessToken == null) {
                        UserController.getInstance().deleteFBData();
                        mNavigationView.getMenu().findItem(R.id.fb_login).setVisible(true);
                        mNavigationView.getMenu().findItem(R.id.fb_logout).setVisible(false);
                    }
                }
            };
            mAccessTokenTracker.startTracking();

            if(isFBLoggedIn()){
                mNavigationView.getMenu().findItem(R.id.fb_login).setVisible(false);
                mNavigationView.getMenu().findItem(R.id.fb_logout).setVisible(true);
            }else{
                mNavigationView.getMenu().findItem(R.id.fb_login).setVisible(true);
                mNavigationView.getMenu().findItem(R.id.fb_logout).setVisible(false);
            }

        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(myReceiver);
        super.onPause();

        //Start: BackgroundTimer
        //AppController.getInstance().saveDate();
        //End: BackgroundTimer
    }

    @Override
    public void onUserInteraction(){
        AppController.getInstance().saveDate();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Start: BackgroundTimer
        AppController.getInstance().appOnBackground(new ICallback() {
            @Override
            public void onRequest() {
                Log.e(TAG, "onRequest() --------------------");
                Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //End: BackgroundTimer

        registerReceiver(myReceiver, filter);
        if(!mFirstTime){
            UserController.getInstance().updateUser(new Response.Listener<User>() {
                @Override
                public void onResponse(User response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
        mFirstTime = false;
        NotificationController.getInstance().updateIfNeeded();
        UserController.getInstance().sendFirebaseTokenIfNeeded();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

/*        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean returnState = true;

        Log.e("ITEM SELECTED:", "" + item.getTitle());
        if (id == R.id.current_plan) {
            goToCurrentPlan();

        } else if (id == R.id.notifications) {
            gotoNotification();
/*
        } else if (id == R.id.coach_comments) {
            gotoCoachComments();
*/
        } else if (id == R.id.professional_notes) {
            gotoProfessionalNotes();

        } else if (id == R.id.community) {
            gotoCommunityChat();

        } else if (id == R.id.personal_info) {
            gotoPersonalInfo();

        } else if (id == R.id.user_history) {
            gotoUserDocs();

        }else if(id == R.id.payments) {
            gotoPayments();

        }else if (id == R.id.statistics){
            gotoStatistics();

        } else if (id == R.id.racing) {
            gotoRacings();

        } else if (id == R.id.fb_login) {
            if(BuildConfig.facebookEnable) {
                onFacebookLogin();
                returnState = false;
                mNavigationView.getMenu().findItem(mSelectedItemMenu).setChecked(true);
                id = mSelectedItemMenu;
            }
        } else if (id == R.id.fb_logout) {
            if(BuildConfig.facebookEnable) {
                onFacebookLogout();
                returnState = false;
                mNavigationView.getMenu().findItem(mSelectedItemMenu).setChecked(true);
                id = mSelectedItemMenu;
            }
        } else if (id == R.id.logout) {
            logout();
            returnState = false;
        }

        mSelectedItemMenu = id;
        mDrawer.closeDrawer(GravityCompat.START);
        return returnState;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(mCurrentFragment instanceof UserDocsFragment || mCurrentFragment instanceof PersonalInfoFragment) {
            mCurrentFragment.onActivityResult(requestCode, resultCode, data);
        }else{
            if(BuildConfig.facebookEnable) {
                mCallbackManager.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(mCurrentFragment instanceof UserDocsFragment) {
            mCurrentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void updateUserData(){


    }

    private void replaceFragment(Fragment fragment, String title, int flags, int id) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
        params.setScrollFlags(flags);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commitNow();

        mNavigationView.getMenu().findItem(id).setChecked(true);
        getSupportActionBar().setTitle(title);
        mCurrentFragment = fragment;
    }

    private void updateNotificationBadge(){
        if(mBadgeTextView==null)
            return;

        long count = NotificationController.getInstance().countUnReadNotif;
        if(count>0) {
            mBadgeTextView.setText("" + count);
            mBadgeTextView.setVisibility(View.VISIBLE);
        }else{
            mBadgeTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void setupFirstFragment() {
        mSelectedItemMenu = R.id.current_plan;
        goToCurrentPlan();
    }

    public void goToCurrentPlan() {
        TrainingPlanFragment fragment = new TrainingPlanFragment();
        //fragment.menuListener = this;
        replaceFragment(fragment, mGroupName, AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP, R.id.current_plan);
    }

    private void gotoNotification(){
        NotificationController.getInstance().countUnReadNotif = 0;
        updateNotificationBadge();
        NotificationFragment fragment = new NotificationFragment();
        replaceFragment(fragment, mGroupName, AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP, R.id.notifications);
    }

    private void gotoPayments(){
        PaymentsFragment fragment = new PaymentsFragment();
        replaceFragment(fragment, mGroupName, AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP, R.id.payments);
    }

    private void gotoStatistics(){
        StatisticsFragment fragment = new StatisticsFragment();
        replaceFragment(fragment, mGroupName, AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP, R.id.statistics);
    }
/*
    private void gotoCoachComments(){
        CoachFragment fragment = new CoachFragment();
        replaceFragment(fragment, mGroupName, AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP, R.id.professional_notes);
    }
*/
    private void gotoProfessionalNotes(){
        ProfessionalNotesFragment fragment = new ProfessionalNotesFragment();
        replaceFragment(fragment, mGroupName, AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP, R.id.professional_notes);
    }

    public void gotoCommunityChat(){
        CommunityChatFragment fragment = new CommunityChatFragment();
        replaceFragment(fragment, mGroupName, AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP, R.id.community);
    }

    public void gotoPersonalInfo(){
        PersonalInfoFragment fragment = new PersonalInfoFragment();
        replaceFragment(fragment, mGroupName, AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP, R.id.personal_info);
    }

    private void gotoUserDocs(){
        UserDocsFragment fragment = new UserDocsFragment();
        replaceFragment(fragment, mGroupName, AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP, R.id.user_history);
    }

    public void gotoRacings(){
        //RacingsFragment fragment = new RacingsFragment();
        TabNavigatorRacings fragment = new TabNavigatorRacings();
        replaceFragment(fragment, mGroupName, AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP, R.id.racing);
    }

    public void gotoZoomImage(){
        Log.i("MAIN","Show Fullsized Image");

        Intent intent = new Intent(MainActivity.this, ImageDisplayActivity.class);
        startActivityForResult(intent, 10);
    }

    private void onFacebookLogin(){
        if(BuildConfig.facebookEnable) {
            mFBLoginButton.performClick();
        }
    }

    private void onFacebookLogout(){
        if(BuildConfig.facebookEnable) {
            mFBLoginButton.performClick();
        }
    }

    private void logout(){
        showProgressDialog(R.string.loginout);
        UserController.getInstance().logout(new Response.Listener<Void>() {
            @Override
            public void onResponse(Void v) {
                dismissProgressDialog();
                IntentHelper.goToLoginActivity(MainActivity.this);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dismissProgressDialog();
                IntentHelper.goToLoginActivity(MainActivity.this);
            }
        });

    }

    public boolean isFBLoggedIn(){
        if(BuildConfig.facebookEnable) {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            return accessToken != null;
        }else{
            return false;
        }
    }

    protected void showProgressDialog(int message) {
        try {
            if(pDialog!=null)
                pDialog.dismiss();

            pDialog = new ProgressDialog(this);
            pDialog.setMessage(getResources().getString(message));
            pDialog.setCancelable(true);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
        } catch (Throwable e) {
        }
    }

    protected void dismissProgressDialog() {
        try {
            if (pDialog != null) {
                pDialog.dismiss();
            }
        } catch (Throwable e) {
        }
    }
}
