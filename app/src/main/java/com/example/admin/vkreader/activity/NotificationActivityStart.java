package com.example.admin.vkreader.activity;

import android.content.DialogInterface;
import android.os.Bundle;

import com.example.admin.vkreader.R;

public class NotificationActivityStart extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_activity_start);
        String title = getResources().getString(R.string.contentTitle);
        String message = getResources().getString(R.string.service_started);
        showDialogInfo(title, message);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        onBackPressed();
    }
}
