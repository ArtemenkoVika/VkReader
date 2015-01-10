package com.example.admin.vkreader.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
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
import com.example.admin.vkreader.adapters.DataDeleteAdapter;
import com.example.admin.vkreader.fragments.BaseFragment;
import com.example.admin.vkreader.fragments.DetailsFragment;
import com.example.admin.vkreader.fragments.FacebookFragment;
import com.example.admin.vkreader.fragments.ListFragment;
import com.example.admin.vkreader.service.UpdateService;
import com.facebook.AppEventsLogger;
import com.facebook.Session;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements ListFragment.onSomeEventListener {
    public static final int ACTION_EDIT = 101;
    public static final String IDE_EXTRA = "param";
    public static final String IDE_EXTRA_BOOL = "bool";
    public static final String IDE_BUNDLE_BOOL = "bool";
    private ListFragment listFragment;
    private Intent intent;
    private FrameLayout frameLayout;
    private MenuItem menuBack;
    private boolean isOnline;
    private ArrayList arrayFavorite;
    private ArrayList arrayDelete;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isOnline = isOnline();
        intent = new Intent(this, UpdateService.class);
        if (!isOnline) {
            if (singleton.count == 0)
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.net),
                        Toast.LENGTH_LONG).show();
        }
        singleton.count++;
        if (isOnline) startService(intent);
        listFragment = new ListFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frm, listFragment).commit();
        savedInstanceState = new Bundle();
        savedInstanceState.putBoolean(IDE_BUNDLE_BOOL, isOnline);
        listFragment.setArguments(savedInstanceState);
        detailsFragment = getSupportFragmentManager().findFragmentById(R.id.details_frag);
        if (detailsFragment != null) {
            //savedInstanceState = new Bundle();
            savedInstanceState.putBoolean(DetailsFragment.ARG_BOOL, isOnline);
            detailsFragment.setArguments(savedInstanceState);
        }
        imageView = (ImageView) findViewById(R.id.image);
        textView = (TextView) findViewById(R.id.text);
        frameLayout = (FrameLayout) findViewById(R.id.frm);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(getApplicationContext(), getResources().getString(R.string.
                facebook_app_id));
    }

    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this, getResources().getString(R.string.facebook_app_id));
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() == null) {
            return false;
        } else return connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public void someEvent(Integer position) {
        this.position = position;
        if (singleton.isDataBase() == true) menuSave.setEnabled(false);
        else menuSave.setEnabled(true);
        if (detailsFragment == null) {
            Intent intent = new Intent();
            intent.putExtra(IDE_EXTRA, position);
            intent.putExtra(IDE_EXTRA_BOOL, isOnline);
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
        menuSave = menu.findItem(R.id.IDM_SAVE).setEnabled(false);
        if (detailsFragment == null) {
            menuBack.setVisible(false);
            menuSave.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.IDM_BACK:
                if (back && (frameLayout.getVisibility() == View.VISIBLE)) {
                    menuSave.setEnabled(false);
                    singleton.setDataBase(false);
                    singleton.getArrayAdapter().clear();
                    inVisible();
                    if (isOnline) {
                        singleton.getArrayAdapter().addAll(resultClass.getTitle());
                        listView.setItemChecked(-1, true);
                        listView.setSelection(0);
                    }
                }
                frameLayout.setVisibility(View.VISIBLE);
                return true;


            case R.id.IDM_SAVE:
                saveArticles(menuSave);
                return true;


            case R.id.IDM_FAVORITE:
                arrayFavorite = dataBase.showSavedArticles(this);
                if (dataBase.isCursorToFirst()) {
                    inVisible();
                    listView.setItemChecked(-1, true);
                    listView.setSelection(0);
                    menuSave.setEnabled(false);
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
                    arrayDelete = dataBase.showSavedArticles(this);
                    deleteAdapter = new DataDeleteAdapter(this, R.layout.row_delete, arrayDelete);
                    deleteAdapter.setNotifyOnChange(true);
                    showDialogDelete(getResources().getString(R.string.delete));
                } else {
                    showDialogInfo("", getResources().getString(R.string.dialog_nothing));
                }
                return true;


            case R.id.IDM_BACK_TO_MAIN:
                inVisible();
                menuSave.setEnabled(false);
                singleton.setDataBase(false);
                back = false;
                frameLayout.setVisibility(View.VISIBLE);
                singleton.getArrayAdapter().clear();
                if (isOnline) {
                    singleton.getArrayAdapter().addAll(resultClass.getTitle());
                    listView.setItemChecked(-1, true);
                    listView.setSelection(0);
                }
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
                boolean menu_vis = menuSave.isEnabled();
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
        boolean b1 = false;
        boolean b2 = false;
        if (dialogDelete != null) b1 = dialogDelete.isShowing();
        if (dialogInfo != null) b2 = dialogInfo.isShowing();
        outState.putInt("check", check);
        outState.putBoolean("back", back);
        outState.putBoolean("isDataBase", singleton.isDataBase());
        outState.putBoolean("b1", b1);
        outState.putBoolean("b2", b2);
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
                if (menu_vis) menuSave.setEnabled(true);
                else menuSave.setEnabled(false);
            }
        }
        try {
            listView.setItemChecked(savedInstanceState.getInt("check"), true);
            listView.setSelection(savedInstanceState.getInt("check"));
        } catch (NullPointerException e) {
        }
        back = savedInstanceState.getBoolean("back");
        singleton.setDataBase(savedInstanceState.getBoolean("isDataBase"));
        if (savedInstanceState.getBoolean("b1")) {
            arrayDelete = dataBase.showSavedArticles(this);
            deleteAdapter = new DataDeleteAdapter(this, R.layout.row_delete, arrayDelete);
            showDialogDelete(getResources().getString(R.string.delete));
        }
        if (savedInstanceState.getBoolean("b2")) {
            showDialogInfo("", getResources().getString(R.string.dialog_nothing));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.stopService(intent);
    }
}
