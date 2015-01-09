package com.example.admin.vkreader.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.example.admin.vkreader.R;
import com.example.admin.vkreader.fragments.DetailsFragment;
import com.facebook.AppEventsLogger;

public class DetailsActivity extends BaseActivity {
    private Fragment fragment2;
    private MenuItem menuSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        savedInstanceState = getIntent().getExtras();
        position = savedInstanceState.getInt(MainActivity.IDE_EXTRA);
        fragment2 = new DetailsFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frm2, fragment2).commit();
        savedInstanceState.putInt(DetailsFragment.ARG_POSITION, position);
        fragment2.setArguments(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.text, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuSave = menu.findItem(R.id.IDM_SAVE);
        if (singleton.isDataBase() == true) menuSave.setEnabled(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.IDM_BACK:
                onBackPressed();
                return true;
            case R.id.IDM_SAVE:
                saveArticles(menuSave);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        boolean menu_vis = menuSave.isEnabled();
        outState.putBoolean("menu_vis", menu_vis);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        boolean menu_vis = savedInstanceState.getBoolean("menu_vis");
        if (menu_vis) menuSave.setEnabled(true);
        else menuSave.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this, getResources().getString(R.string.facebook_app_id));
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this, getResources().getString(R.string.facebook_app_id));
    }
}