package com.example.admin.vkreader.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.admin.vkreader.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusOneButton;

public class GoogleShareActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final int REQUEST_CODE_RESOLVE = 9000;
    private ProgressDialog mConnectionProgressDialog;
    private GoogleApiClient mPlusClient;
    private PlusOneButton mPlusOneButton;
    private ConnectionResult mConnectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_share);
        mPlusOneButton = (PlusOneButton) findViewById(R.id.plus_one_button);
        mPlusClient = new GoogleApiClient.Builder(this, this, this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
        mPlusOneButton.initialize("https://developers.google.com/+/", REQUEST_CODE_RESOLVE);
        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in...");
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
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == REQUEST_CODE_RESOLVE && responseCode == RESULT_OK) {
            mConnectionResult = null;
            mPlusClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mConnectionProgressDialog.dismiss();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }
}
