package com.example.admin.vkreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.admin.vkreader.R;
import com.example.admin.vkreader.fragments.FacebookFragment;
import com.facebook.AppEventsLogger;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;

public class FacebookShareActivity extends FragmentActivity {
    private UiLifecycleHelper uiHelper;

    public UiLifecycleHelper getUiHelper() {
        return uiHelper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_share);
        AppEventsLogger.activateApp(getApplicationContext(), getResources().getString(R.string.
                facebook_app_id));
        uiHelper = new UiLifecycleHelper(this, callback);
        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, callback, savedInstanceState);
                uiHelper.onCreate(savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
        }


        System.out.println("Start Facebook login");
        Session.openActiveSession(this, true, callback);
    }

    public final static Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            System.out.println("Call");
            if (session.isOpened()) {
                System.out.println("Open");
                Request.newMeRequest(session, new Request.GraphUserCallback() {
                    public void onCompleted(GraphUser user, Response response) {
                        System.out.println("Complete");
                        if (user != null) {
                            System.out.println(user.getName());
                        } else {
                            System.out.println("User NULL");
                        }
                    }
                }).executeAsync();
            } else {
                System.out.println("Not open");
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {

            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("Activity", "Success!");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //AppEventsLogger.activateApp(getApplicationContext(), getResources().getString(R.string.facebook_app_id));
        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed())) {
            new FacebookFragment().onSessionStateChange(session.getState());
        }
        uiHelper.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        AppEventsLogger.deactivateApp(this, getResources().getString(R.string.facebook_app_id));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }
}
