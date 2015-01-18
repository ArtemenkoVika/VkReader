package com.example.admin.vkreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.example.admin.vkreader.R;
import com.example.admin.vkreader.fragments.DetailsFragment;

public class DetailsActivity extends BaseActivity {
    private Fragment fragment2;

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
    protected void onResume() {
        super.onResume();
        singleton.count2 = 0;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuSave = menu.findItem(R.id.IDM_SAVE);
        menuFacebook = menu.findItem(R.id.IDM_FACEBOOK);
        menuGoogle = menu.findItem(R.id.IDM_GOOGLE);
        if (!isOnline()) {
            menuFacebook.setVisible(false);
            menuGoogle.setVisible(false);
            menuSave.setVisible(false);
        } else {
            menuFacebook.setVisible(true);
            menuGoogle.setVisible(true);
            menuSave.setVisible(true);
        }
        if (singleton.isDataBase() == true) menuSave.setVisible(false);
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

            case R.id.IDM_FACEBOOK:
                Intent intent = new Intent();
                intent.setClass(this, FacebookShareActivity.class);
                startActivityForResult(intent, 1);
                return true;

            case R.id.IDM_GOOGLE:
                Intent intentGoogle = new Intent();
                intentGoogle.setClass(this, GoogleShareActivity.class);
                startActivityForResult(intentGoogle, GoogleShareActivity.REQUEST_CODE_RESOLVE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        boolean menu_vis = menuSave.isVisible();
        outState.putBoolean("menu_vis", menu_vis);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        boolean menu_vis = savedInstanceState.getBoolean("menu_vis");
        if (menu_vis) menuSave.setVisible(true);
        else menuSave.setVisible(false);
    }
}