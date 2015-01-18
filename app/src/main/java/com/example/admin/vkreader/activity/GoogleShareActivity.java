package com.example.admin.vkreader.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.vkreader.R;
import com.example.admin.vkreader.async_task.LoadImageFromNetwork;
import com.example.admin.vkreader.entity.ResultClass;
import com.example.admin.vkreader.patterns.Singleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.plus.model.people.Person;

import java.util.concurrent.ExecutionException;

public class GoogleShareActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {
    public static final int REQUEST_CODE_RESOLVE = 9000;
    private GoogleApiClient mPlusClient;
    private SignInButton googleButton;
    private ProgressDialog mConnectionProgressDialog;
    private ConnectionResult mConnectionResult;
    private Button buttonOut;
    private Button buttonShare;
    private Button buttonProfile;
    private Singleton singleton = Singleton.getInstance();
    private ResultClass resultClass = ResultClass.getInstance();
    private TextView textView;
    private ImageView imageView;
    private String userName = "";
    private String userAvatar = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_share);
        mPlusClient = new GoogleApiClient.Builder(this, this, this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Wait, pleas");
        mConnectionProgressDialog.setOwnerActivity(this);

        googleButton = (SignInButton) findViewById(R.id.sign_in_button);
        googleButton.setOnClickListener(this);

        buttonOut = (Button) findViewById(R.id.out_button);
        buttonOut.setOnClickListener(this);

        buttonProfile = (Button) findViewById(R.id.g_profile_button);
        buttonProfile.setOnClickListener(this);

        buttonShare = (Button) findViewById(R.id.g_share_button);
        buttonShare.setOnClickListener(this);

        textView = (TextView) findViewById(R.id.g_plus_login_name);

        imageView = (ImageView) findViewById(R.id.g_plus_login_photo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPlusClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlusClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mConnectionProgressDialog.dismiss();
        if (Plus.PeopleApi.getCurrentPerson(mPlusClient) != null) {
            buttonProfile.setVisibility(View.VISIBLE);
            buttonShare.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mConnectionProgressDialog.dismiss();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.sign_in_button:
                if (!mPlusClient.isConnected()) {
                    if (mConnectionResult == null) {
                        mConnectionProgressDialog.show();
                    } else {
                        try {
                            mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE);
                        } catch (IntentSender.SendIntentException e) {
                            mConnectionResult = null;
                            mPlusClient.connect();
                        }
                    }
                    buttonProfile.setVisibility(View.VISIBLE);
                    buttonShare.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.out_button:
                if (mPlusClient.isConnected()) {
                    mPlusClient.disconnect();
                    mPlusClient.clearDefaultAccountAndReconnect();
                    mPlusClient.connect();

                    buttonProfile.setVisibility(View.INVISIBLE);
                    buttonShare.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.INVISIBLE);
                }
                break;

            case R.id.g_share_button:
                Intent shareIntent = new PlusShare.Builder(this)
                        .setType("text/plain")
                        .setText(resultClass.getText().get(singleton.getPosition()))
                        .setContentUrl(Uri.parse(resultClass.getUrls().get(singleton.getPosition())))
                        .getIntent();
                shareIntent.setPackage("com.google.android.apps.plus");
                startActivityForResult(shareIntent, 0);
                break;

            case R.id.g_profile_button:
                if (Plus.PeopleApi.getCurrentPerson(mPlusClient) != null) {
                    Person user = Plus.PeopleApi.getCurrentPerson(mPlusClient);
                    userName = user.getDisplayName();
                    userAvatar = user.getImage().getUrl();

                    LoadImageFromNetwork load = new LoadImageFromNetwork(this);
                    load.execute(userAvatar);
                    try {
                        imageView.setImageBitmap(load.get());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    textView.setText(userName);
                    textView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mConnectionProgressDialog.isShowing()) {
            if (result.hasResolution()) {
                try {
                    result.startResolutionForResult(this, REQUEST_CODE_RESOLVE);
                } catch (IntentSender.SendIntentException e) {
                    mPlusClient.connect();
                }
            }
        }
        mConnectionResult = result;
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == REQUEST_CODE_RESOLVE && responseCode == RESULT_OK) {
            mConnectionResult = null;
            mPlusClient.connect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.google_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.IDM_BACK_GOOGLE:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        int visButtonProfile = buttonProfile.getVisibility();
        int visButtonShare = buttonShare.getVisibility();
        int visTextView = textView.getVisibility();
        int visImageView = imageView.getVisibility();
        String text = (String) textView.getText();

        outState.putInt("visButtonProfile", visButtonProfile);
        outState.putInt("visButtonShare", visButtonShare);
        outState.putInt("visTextView", visTextView);
        outState.putInt("visImageView", visImageView);
        outState.putString("text", text);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        textView.setText(savedInstanceState.getString("text"));
        savedInRotation(savedInstanceState, "visButtonProfile", buttonProfile);
        savedInRotation(savedInstanceState, "visButtonShare", buttonShare);
        savedInRotation(savedInstanceState, "visTextView", textView);
        savedInRotation(savedInstanceState, "visImageView", imageView);
    }

    public void savedInRotation(Bundle savedInstanceState, String parameter, View view) {
        if (savedInstanceState.getInt(parameter) == View.VISIBLE)
            view.setVisibility(View.VISIBLE);
        else view.setVisibility(View.INVISIBLE);
    }
}

