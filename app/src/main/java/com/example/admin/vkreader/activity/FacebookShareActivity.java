package com.example.admin.vkreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.vkreader.R;
import com.example.admin.vkreader.async_task.LoadImageFromNetwork;
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
import com.facebook.widget.ProfilePictureView;
import com.facebook.widget.WebDialog;

import java.util.concurrent.ExecutionException;

public class FacebookShareActivity extends FragmentActivity implements View.OnClickListener {
    private UiLifecycleHelper uiHelper = null;
    private Singleton singleton = Singleton.getInstance();
    private ResultClass resultClass = ResultClass.getInstance();
    private boolean pendingPublishReauthorization = false;
    private Button shareButton;
    private Button profileButton;
    private TextView textView;
    private ProfilePictureView profilePictureView;
    private String userName = "";
    private String userId = "";

    public UiLifecycleHelper getUiHelper() {
        return uiHelper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_share);
        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);
        Session session = Session.restoreSession(this, null, callback, savedInstanceState);
        Session.setActiveSession(session);
        Session.openActiveSession(this, true, callback);
        shareButton = (Button) findViewById(R.id.share_button);
        profileButton = (Button) findViewById(R.id.profile_button);
        textView = (TextView) findViewById(R.id.nik);
        profilePictureView = (ProfilePictureView) findViewById(R.id.profileView);

        shareButton.setOnClickListener(this);
        profileButton.setOnClickListener(this);
        textView.setVisibility(View.GONE);
        profilePictureView.setVisibility(View.GONE);
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
                            userName = user.getName();
                            userId = user.getId();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_button:
                facebookPublish(resultClass.getTitle().get(singleton.getPosition()), "",
                        resultClass.getText().get(singleton.getPosition()),
                        "https://vk.com/christian_parable",
                        resultClass.getUrls().get(singleton.getPosition()));
                break;
            case R.id.profile_button:
                textView.setText(userName);

                LoadImageFromNetwork load = new LoadImageFromNetwork(this);
                load.execute("http://graph.facebook.com/" + userId + "/picture?type=large");
                try {
                    profilePictureView.setDefaultProfilePicture(load.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                shareButton.setVisibility(View.GONE);
                profileButton.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                profilePictureView.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.facebook_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.IDM_BACK_SHARE:
                if (shareButton.getVisibility() == View.GONE) {
                    shareButton.setVisibility(View.VISIBLE);
                    profileButton.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                    profilePictureView.setVisibility(View.GONE);
                    return true;
                } else {
                    onBackPressed();
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
