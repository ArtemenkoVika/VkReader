package com.example.admin.vkreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.admin.vkreader.R;
import com.example.admin.vkreader.entity.ResultClass;
import com.example.admin.vkreader.patterns.Singleton;
import com.facebook.AppEventsLogger;
import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;

public class FacebookShareActivity extends FragmentActivity {
    private UiLifecycleHelper uiHelper = null;
    private Singleton singleton = Singleton.getInstance();
    private ResultClass resultClass = ResultClass.getInstance();
    private boolean pendingPublishReauthorization = false;

    public UiLifecycleHelper getUiHelper() {
        return uiHelper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_share);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, callback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
        }
        Session.openActiveSession(this, true, callback);
        facebookPublish(resultClass.getTitle().get(singleton.getPosition()), "",
                resultClass.getText().get(singleton.getPosition()), "https://vk.com/christian_parable",
                resultClass.getUrls().get(singleton.getPosition()));
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
        AppEventsLogger.activateApp(getApplicationContext(), getResources().getString(R.string.
                facebook_app_id));
        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session.getState());
        }
        uiHelper.onResume();
        onBackPressed();
    }

    public void onSessionStateChange(SessionState state) {
        if (state.isOpened()) {
            if (pendingPublishReauthorization && state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
                pendingPublishReauthorization = false;
            }
        }
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

    public final void facebookPublish(String name, String caption, String description, String link, String pictureLink) {
        if (FacebookDialog.canPresentShareDialog(getApplicationContext(), FacebookDialog.
                ShareDialogFeature.SHARE_DIALOG)) {
            //Facebook-client is installed
            FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
                    .setName(name)
                    .setCaption(caption)
                    .setDescription(description)
                    .setLink(link)
                    .setPicture(pictureLink)
                    .build();
            try {
                getUiHelper().trackPendingDialogCall
                        (shareDialog.present());
            } catch (NullPointerException e) {
                System.out.println("nulmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
            }
        } else {
            //Facebook-client is not installed – use web-dialog
            Bundle params = new Bundle();
            params.putString("name", name);
            params.putString("caption", caption);
            params.putString("description", description);
            params.putString("link", link);
            params.putString("picture", pictureLink);
            WebDialog feedDialog = new WebDialog.FeedDialogBuilder(this,
                    Session.getActiveSession(), params)
                    .setOnCompleteListener(new WebDialog.OnCompleteListener() {
                        //Listener for web-dialog
                        @Override
                        public void onComplete(Bundle values, FacebookException error) {
                            if ((values != null) && (values.getString("post_id") != null) &&
                                    (error == null)) {
                                Toast.makeText(getApplicationContext(),
                                        "This entry was posted",
                                        Toast.LENGTH_LONG);
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "This entry was posted", Toast.LENGTH_LONG);
                            }
                        }
                    })
                    .build();
            feedDialog.show();
        }
    }
}
