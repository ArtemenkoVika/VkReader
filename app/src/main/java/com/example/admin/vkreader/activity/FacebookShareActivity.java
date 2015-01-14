package com.example.admin.vkreader.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.admin.vkreader.R;
import com.example.admin.vkreader.patterns.Singleton;
import com.facebook.AppEventsLogger;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.ProfilePictureView;

public class FacebookShareActivity extends FragmentActivity {
    private UiLifecycleHelper uiHelper = null;
    private Button shareButton;
    private Button profileButton;
    private TextView textView;
    private ProfilePictureView profilePictureView;
    private ProgressDialog progressDialog;
    private Singleton singleton = Singleton.getInstance();

    public UiLifecycleHelper getUiHelper() {
        return uiHelper;
    }

    public void setUiHelper(UiLifecycleHelper uiHelper) {
        this.uiHelper = uiHelper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_share);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Wait, pleas");
        progressDialog.setOwnerActivity(this);
        if (singleton.count2 == 0) progressDialog.show();
        singleton.count2++;

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        Session session = Session.restoreSession(this, null, callback, savedInstanceState);
        Session.setActiveSession(session);
        Session.openActiveSession(this, true, callback);

        shareButton = (Button) findViewById(R.id.share_button);
        profileButton = (Button) findViewById(R.id.profile_button);
        textView = (TextView) findViewById(R.id.nik);
        profilePictureView = (ProfilePictureView) findViewById(R.id.profileView);
    }

    public Session.StatusCallback callback = new Session.StatusCallback() {
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
                            progressDialog.dismiss();
                            shareButton.setVisibility(View.VISIBLE);
                            profileButton.setVisibility(View.VISIBLE);
                            textView.setText(user.getName());
                            profilePictureView.setProfileId(user.getId());
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
        uiHelper.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);

        int visShareButton = shareButton.getVisibility();
        int visProfileButton = profileButton.getVisibility();
        int visTextView = textView.getVisibility();
        int visProfilePictureView = profilePictureView.getVisibility();
        String text = (String) textView.getText();

        outState.putInt("visShareButton", visShareButton);
        outState.putInt("visProfileButton", visProfileButton);
        outState.putInt("visTextView", visTextView);
        outState.putInt("visProfilePictureView", visProfilePictureView);
        outState.putString("text", text);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        AppEventsLogger.deactivateApp(this, getResources().getString(R.string.facebook_app_id));
        progressDialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.facebook_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.IDM_BACK_FACEBOOK:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        textView.setText(savedInstanceState.getString("text"));
        savedInRotation(savedInstanceState, "visShareButton", shareButton);
        savedInRotation(savedInstanceState, "visProfileButton", profileButton);
        savedInRotation(savedInstanceState, "visTextView", textView);
        savedInRotation(savedInstanceState, "visProfilePictureView", profilePictureView);
    }

    public void savedInRotation(Bundle savedInstanceState, String parameter, View view) {
        if (savedInstanceState.getInt(parameter) == View.VISIBLE)
            view.setVisibility(View.VISIBLE);
        else view.setVisibility(View.INVISIBLE);
    }
}
