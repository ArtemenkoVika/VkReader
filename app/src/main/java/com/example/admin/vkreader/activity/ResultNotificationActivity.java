package com.example.admin.vkreader.activity;

import android.content.DialogInterface;
import android.os.Bundle;

import com.example.admin.vkreader.R;

public class ResultNotificationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_notification);
        String title = getResources().getString(R.string.contentTitle2);
        String message = getResources().getString(R.string.app_updated);
        showDialogInfo(title, message);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        onBackPressed();
    }
}
