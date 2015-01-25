package com.example.admin.vkreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.vkreader.R;
import com.example.admin.vkreader.fragments.ListFragment;
import com.example.admin.vkreader.service.UpdateService;

public class MainActivity extends BaseActivity implements ListFragment.onSomeEventListener {
    public static final int ACTION_EDIT = 101;
    public static final String IDE_EXTRA = "param";
    private Intent intent;
    private FrameLayout frameLayoutList;
    private FrameLayout frameLayoutDetails;
    private Button buttonDeleteAll;
    private MenuItem menuBack;
    private MenuItem menuShare;
    private boolean delete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(this, UpdateService.class);
        if (!isOnline()) {
            if (singleton.count == 0)
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.net),
                        Toast.LENGTH_LONG).show();
        }
        singleton.count++;
        if (isOnline()) startService(intent);
        listFragment = new ListFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frm_list_frag, listFragment).commit();
        detailsFragment = getSupportFragmentManager().findFragmentById(R.id.details_frag);
        imageView = (ImageView) findViewById(R.id.image);
        textView = (TextView) findViewById(R.id.text);
        frameLayoutList = (FrameLayout) findViewById(R.id.frm_list_frag);
        frameLayoutDetails = (FrameLayout) findViewById(R.id.frm_details_frag);
    }

    @Override
    protected void onResume() {
        super.onResume();
        singleton.count2 = 0;
        if (singleton.isDelete()) {
            listView.setItemChecked(-1, true);
            listView.setSelection(0);
            singleton.setDelete(false);
        }
    }

    @Override
    public void someEvent(Integer position) {
        if (detailsFragment != null) {
            frameLayoutList.setVisibility(View.GONE);
            frameLayoutDetails.setVisibility(View.VISIBLE);
            if (singleton.isDataBase()) {
                menuSave.setVisible(false);
                menuDelete.setVisible(true);
            } else {
                checkIsArticlesInDateBase();
            }
        } else {
            Intent intent = new Intent();
            intent.putExtra(IDE_EXTRA, position);
            intent.setClass(this, DetailsActivity.class);
            startActivityForResult(intent, ACTION_EDIT);
        }
    }

    @Override
    public void someListView(ListView listView, Button buttonDeleteAll) {
        this.listView = listView;
        this.buttonDeleteAll = buttonDeleteAll;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuBack = menu.findItem(R.id.IDM_BACK);
        menuSave = menu.findItem(R.id.IDM_SAVE).setVisible(false);
        menuDelete = menu.findItem(R.id.IDM_DELETE).setVisible(false);
        menuShare = menu.findItem(R.id.IDM_SHARE);
        menuFacebook = menu.findItem(R.id.IDM_FACEBOOK);
        menuGoogle = menu.findItem(R.id.IDM_GOOGLE);

        if (!isOnline()) {
            menuFacebook.setVisible(false);
            menuGoogle.setVisible(false);
            menuShare.setVisible(false);
        } else {
            menuFacebook.setVisible(true);
            menuGoogle.setVisible(true);
            menuShare.setVisible(true);
        }

        if (detailsFragment == null) {
            menuBack.setVisible(false);
            menuFacebook.setVisible(false);
            menuGoogle.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.IDM_BACK:
                if (frameLayoutList.getVisibility() == View.VISIBLE && singleton.isDataBase()) {
                    buttonDeleteAll.setVisibility(View.GONE);
                    menuSave.setVisible(false);
                    menuDelete.setVisible(false);
                    singleton.setDataBase(false);
                    singleton.getArrayAdapter().clear();
                    inVisible();
                    if (isOnline()) {
                        singleton.getArrayAdapter().addAll(resultClass.getTitle());
                        listView.setItemChecked(-1, true);
                        listView.setSelection(0);
                    }
                }
                if (singleton.isDataBase()) {
                    arrayFavorite = dataBase.showSavedArticles(this);
                    singleton.getArrayAdapter().clear();
                    singleton.getArrayAdapter().addAll(arrayFavorite);
                    if (delete) {
                        listView.setItemChecked(-1, true);
                        listView.setSelection(0);
                        delete = false;
                    }
                }
                frameLayoutList.setVisibility(View.VISIBLE);
                frameLayoutDetails.setVisibility(View.GONE);
                return true;


            case R.id.IDM_SAVE:
                saveArticles(menuSave);
                menuSave.setVisible(false);
                menuDelete.setVisible(true);
                return true;


            case R.id.IDM_FAVORITE:
                arrayFavorite = dataBase.showSavedArticles(this);
                if (dataBase.isCursorToFirst()) {
                    buttonDeleteAll.setVisibility(View.VISIBLE);
                    inVisible();
                    listView.setItemChecked(-1, true);
                    listView.setSelection(0);
                    menuSave.setVisible(false);
                    menuDelete.setVisible(false);
                    singleton.setDataBase(true);
                    if (detailsFragment != null) {
                        frameLayoutList.setVisibility(View.VISIBLE);
                        frameLayoutDetails.setVisibility(View.GONE);
                    }
                    singleton.getArrayAdapter().clear();
                    singleton.getArrayAdapter().addAll(arrayFavorite);
                } else {
                    showDialogInfo("", getResources().getString(R.string.dialog_nothing));
                }
                return true;


            case R.id.IDM_DELETE:
                if (singleton.isDataBase()) {
                    delete = true;
                    imageView.setVisibility(View.INVISIBLE);
                    textView.setText("");
                }
                deleteArticles();
                return true;


            case R.id.IDM_BACK_TO_MAIN:
                if (detailsFragment != null) {
                    frameLayoutList.setVisibility(View.VISIBLE);
                    frameLayoutDetails.setVisibility(View.GONE);
                }
                buttonDeleteAll.setVisibility(View.GONE);
                inVisible();
                menuSave.setVisible(false);
                menuDelete.setVisible(false);
                singleton.setDataBase(false);
                if (detailsFragment != null) {
                    frameLayoutList.setVisibility(View.VISIBLE);
                    frameLayoutDetails.setVisibility(View.GONE);
                }
                singleton.getArrayAdapter().clear();
                if (isOnline()) {
                    singleton.getArrayAdapter().addAll(resultClass.getTitle());
                    listView.setItemChecked(-1, true);
                    listView.setSelection(0);
                }
                return true;


            case R.id.IDM_SHARE:
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject text here");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://vk.com/christian_parable");
                startActivity(shareIntent);
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
        if (detailsFragment != null) {
            savedInRotation(outState);
            if (menuSave != null) {
                outState.putBoolean("menu_save_vis", menuSave.isVisible());
                outState.putBoolean("menu_delete_vis", menuDelete.isVisible());
            }
            outState.putInt("visibilityFrameList", frameLayoutList.getVisibility());
            outState.putInt("visibilityFrameDetails", frameLayoutDetails.getVisibility());
            outState.putInt("imVis", imageView.getVisibility());
            outState.putBoolean("delete", delete);
        }
        int check = 0;
        try {
            check = listView.getCheckedItemPosition();
        } catch (NullPointerException e) {
        }
        boolean b = false;
        if (dialogInfo != null) b = dialogInfo.isShowing();
        outState.putInt("visDeleteAll", buttonDeleteAll.getVisibility());
        outState.putInt("check", check);
        outState.putBoolean("dialog", b);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (detailsFragment != null) {
            restoreInRotation(savedInstanceState);
            savedInRotation(savedInstanceState, "visibilityFrameList", frameLayoutList);
            savedInRotation(savedInstanceState, "visibilityFrameDetails", frameLayoutDetails);
            savedInRotation(savedInstanceState, "imVis", imageView);
            if (menuSave != null) {
                savedInRotation(savedInstanceState, "menu_save_vis", menuSave);
                savedInRotation(savedInstanceState, "menu_delete_vis", menuDelete);
            }
            delete = savedInstanceState.getBoolean("delete");
        }
        try {
            listView.setItemChecked(savedInstanceState.getInt("check"), true);
            listView.setSelection(savedInstanceState.getInt("check"));
        } catch (NullPointerException e) {
        }
        savedInRotation(savedInstanceState, "visDeleteAll", buttonDeleteAll);
        if (savedInstanceState.getBoolean("dialog")) {
            showDialogInfo("", getResources().getString(R.string.dialog_nothing));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.stopService(intent);
    }
}
