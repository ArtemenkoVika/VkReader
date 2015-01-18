package com.example.admin.vkreader.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.vkreader.R;
import com.example.admin.vkreader.fragments.BaseFragment;
import com.example.admin.vkreader.fragments.DeleteFragment;
import com.example.admin.vkreader.fragments.ListFragment;
import com.example.admin.vkreader.service.UpdateService;
import com.facebook.AppEventsLogger;

public class MainActivity extends BaseActivity implements ListFragment.onSomeEventListener {
    public static final int ACTION_EDIT = 101;
    public static final String IDE_EXTRA = "param";
    private Intent intent;
    private FrameLayout frameLayout;
    private MenuItem menuBack;
    private ListView listView;
    private MenuItem menuShare;

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
        if (!singleton.isDelete()) getSupportFragmentManager().beginTransaction().
                add(R.id.frm, listFragment).commit();
        detailsFragment = getSupportFragmentManager().findFragmentById(R.id.details_frag);
        imageView = (ImageView) findViewById(R.id.image);
        textView = (TextView) findViewById(R.id.text);
        frameLayout = (FrameLayout) findViewById(R.id.frm);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(getApplicationContext(), getResources().getString(R.string.
                facebook_app_id));
        singleton.count2 = 0;
    }

    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this, getResources().getString(R.string.facebook_app_id));
    }

    @Override
    public void someEvent(Integer position) {
        this.position = position;
        if (singleton.isDataBase() == true || detailsFragment == null) menuSave.setVisible(false);
        else menuSave.setVisible(true);
        if (detailsFragment == null) {
            Intent intent = new Intent();
            intent.putExtra(IDE_EXTRA, position);
            intent.setClass(this, DetailsActivity.class);
            startActivityForResult(intent, ACTION_EDIT);
        }
    }

    @Override
    public void someListView(ListView listView) {
        this.listView = listView;
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

                arrayFavorite = dataBase.showSavedArticles(this);
                if (dataBase.isCursorToFirst()) {
                    if (singleton.isDataBase() && singleton.isDelete()) {
                        singleton.getArrayAdapter().clear();
                        singleton.getArrayAdapter().addAll(arrayFavorite);
                        back = false;
                    }
                }

                if (singleton.isDelete()) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frm, listFragment).commit();
                    singleton.setDelete(false);
                }
                if (back && (frameLayout.getVisibility() == View.VISIBLE)) {
                    back = false;
                    menuSave.setVisible(false);
                    singleton.setDataBase(false);
                    singleton.getArrayAdapter().clear();
                    inVisible();
                    if (isOnline()) {
                        singleton.getArrayAdapter().addAll(resultClass.getTitle());
                        if (listView != null) {
                            listView.setItemChecked(-1, true);
                            listView.setSelection(0);
                        }
                    }
                }
                frameLayout.setVisibility(View.VISIBLE);
                singleton.setDelete(false);
                return true;


            case R.id.IDM_SAVE:
                saveArticles(menuSave);
                return true;


            case R.id.IDM_FAVORITE:
                arrayFavorite = dataBase.showSavedArticles(this);
                if (dataBase.isCursorToFirst()) {
                    if (singleton.isDelete()) getSupportFragmentManager().beginTransaction().
                            replace(R.id.frm, listFragment).commit();
                    singleton.setDelete(false);
                    inVisible();
                    if (listView != null) {
                        listView.setItemChecked(-1, true);
                        listView.setSelection(0);
                    }
                    menuSave.setVisible(false);
                    singleton.setDataBase(true);
                    back = true;
                    frameLayout.setVisibility(View.VISIBLE);
                    singleton.getArrayAdapter().clear();
                    singleton.getArrayAdapter().addAll(arrayFavorite);
                } else {
                    showDialogInfo("", getResources().getString(R.string.dialog_nothing));
                }
                return true;


            case R.id.IDM_DELETE:
                dataBase.showSavedArticles(this);
                if (dataBase.isCursorToFirst()) {
                    singleton.setDelete(true);
                    DeleteFragment deleteFragment = new DeleteFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frm, deleteFragment)
                            .commit();
                    frameLayout.setVisibility(View.VISIBLE);
                } else {
                    showDialogInfo("", getResources().getString(R.string.dialog_nothing));
                }
                return true;


            case R.id.IDM_BACK_TO_MAIN:
                if (singleton.isDelete()) {
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.frm, listFragment).commit();
                    singleton.setDelete(false);
                }
                inVisible();
                menuSave.setVisible(false);
                singleton.setDataBase(false);
                back = false;
                frameLayout.setVisibility(View.VISIBLE);
                singleton.getArrayAdapter().clear();
                if (isOnline()) {
                    singleton.getArrayAdapter().addAll(resultClass.getTitle());
                    if (listView != null) {
                        listView.setItemChecked(-1, true);
                        listView.setSelection(0);
                    }
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
            String text = (String) textView.getText();
            try {
                byte[] bytes = getByteArrayFromBitmap(((BitmapDrawable) imageView.getDrawable()).
                        getBitmap());
                outState.putByteArray("bytes", bytes);
            } catch (NullPointerException e) {
                System.out.println(e + " - in MainActivity");
            }
            int visibility = frameLayout.getVisibility();
            int imVis = imageView.getVisibility();
            if (menuSave != null) {
                boolean menu_vis = menuSave.isVisible();
                outState.putBoolean("menu_vis", menu_vis);
            }
            outState.putString("text", text);
            outState.putInt("visibility", visibility);
            outState.putInt("imVis", imVis);
        }
        int check = 0;
        try {
            check = listView.getCheckedItemPosition();
        } catch (NullPointerException e) {
        }
        boolean b = false;
        if (dialogInfo != null) b = dialogInfo.isShowing();
        outState.putInt("check", check);
        outState.putBoolean("back", back);
        outState.putBoolean("isDataBase", singleton.isDataBase());
        outState.putBoolean("b", b);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (detailsFragment != null) {
            textView.setText(savedInstanceState.getString("text"));
            try {
                imageView.setImageBitmap(new BaseFragment().getBitmapFromByteArray(savedInstanceState
                        .getByteArray("bytes")));
            } catch (NullPointerException e) {
                System.out.println(e + " - in MainActivity");
            }
            int visibility = savedInstanceState.getInt("visibility");
            int imVis = savedInstanceState.getInt("imVis");
            if (visibility == View.VISIBLE) frameLayout.setVisibility(View.VISIBLE);
            if (visibility == View.GONE) frameLayout.setVisibility(View.GONE);
            if (imVis == View.VISIBLE) imageView.setVisibility(View.VISIBLE);
            if (imVis == View.INVISIBLE) imageView.setVisibility(View.INVISIBLE);
            if (menuSave != null) {
                boolean menu_vis = savedInstanceState.getBoolean("menu_vis");
                if (menu_vis) menuSave.setVisible(true);
                else menuSave.setVisible(false);
            }
        }
        try {
            listView.setItemChecked(savedInstanceState.getInt("check"), true);
            listView.setSelection(savedInstanceState.getInt("check"));
        } catch (NullPointerException e) {
        }
        back = savedInstanceState.getBoolean("back");
        singleton.setDataBase(savedInstanceState.getBoolean("isDataBase"));
        if (savedInstanceState.getBoolean("b")) {
            showDialogInfo("", getResources().getString(R.string.dialog_nothing));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.stopService(intent);
    }
}
