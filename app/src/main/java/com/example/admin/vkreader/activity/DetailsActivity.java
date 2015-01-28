package com.example.admin.vkreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.vkreader.R;
import com.example.admin.vkreader.fragments.DetailsFragment;

public class DetailsActivity extends BaseActivity implements DetailsFragment.onSomeEvent {
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
        menuDelete = menu.findItem(R.id.IDM_DELETE);
        menuFacebook = menu.findItem(R.id.IDM_FACEBOOK);
        menuGoogle = menu.findItem(R.id.IDM_GOOGLE);

        if (!isOnline()) {
            menuFacebook.setVisible(false);
            menuGoogle.setVisible(false);
        } else {
            menuFacebook.setVisible(true);
            menuGoogle.setVisible(true);
        }
        if (singleton.isDataBase()) {
            menuSave.setVisible(false);
            menuDelete.setVisible(true);
        } else {
            checkIsArticlesInDateBase();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.IDM_BACK:
                if (singleton.isDataBase()) {
                    arrayFavorite = dataBase.showSavedArticles(this);
                    singleton.getArrayAdapter().clear();
                    singleton.getArrayAdapter().addAll(arrayFavorite);
                }
                onBackPressed();
                return true;

            case R.id.IDM_SAVE:
                saveArticles(menuSave);
                menuSave.setVisible(false);
                menuDelete.setVisible(true);
                return true;

            case R.id.IDM_DELETE:
                if (singleton.isDataBase()) {
                    singleton.setDelete(true);
                    imageView.setVisibility(View.INVISIBLE);
                    textView.setText("");
                }
                deleteArticles();
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
        try {
            outState.putBoolean("menu_save_vis", menuSave.isVisible());
            outState.putBoolean("menu_delete_vis", menuDelete.isVisible());
        } catch (Exception e) {
            System.out.println(e + " - in the DetailsActivity(onSaveInstanceState)");
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {
            savedInRotation(savedInstanceState, "menu_save_vis", menuSave);
            savedInRotation(savedInstanceState, "menu_delete_vis", menuDelete);
        } catch (Exception e) {
            System.out.println(e + " - in the DetailsActivity(onRestoreInstanceState)");
            e.printStackTrace();
        }
    }

    @Override
    public void someView(ImageView imageView, TextView textView) {
        this.imageView = imageView;
        this.textView = textView;
    }
}