package com.example.admin.vkreader.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.admin.vkreader.async_task.LoadImageFromNetwork;
import com.example.admin.vkreader.entity.DataBaseOfFavoriteEntity;
import com.example.admin.vkreader.entity.ResultClass;
import com.example.admin.vkreader.fragments.BaseFragment;
import com.example.admin.vkreader.fragments.ListFragment;
import com.example.admin.vkreader.java_classes.DataBase;
import com.example.admin.vkreader.patterns.Singleton;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class BaseActivity extends FragmentActivity implements DialogInterface.OnClickListener {
    protected Singleton singleton = Singleton.getInstance();
    protected DataBaseOfFavoriteEntity dataEntity;
    protected DataBase dataBase = new DataBase();
    protected ResultClass resultClass = ResultClass.getInstance();
    protected int positionDelete;
    protected TextView textView;
    protected ImageView imageView;
    protected Fragment detailsFragment;
    protected MenuItem menuSave;
    protected MenuItem menuFacebook;
    protected MenuItem menuGoogle;
    protected MenuItem menuDelete;
    protected AlertDialog dialogInfo;
    protected ArrayList arrayFavorite;
    protected ListFragment listFragment;
    protected ListView listView;
    protected byte[] bytes;
    protected int position;

    public void saveArticles(MenuItem menuSave) {
        LoadImageFromNetwork load = new LoadImageFromNetwork(this);
        load.execute(resultClass.getUrls().get(singleton.getPosition()));
        byte[] bytes = null;
        try {
            bytes = getByteArrayFromBitmap(load.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        dataEntity = new DataBaseOfFavoriteEntity(resultClass.getTitle().get(singleton.getPosition()),
                resultClass.getText().get(singleton.getPosition()), bytes, resultClass.getUrls().
                get(singleton.getPosition()));
        dataBase.addArticles(this, dataEntity);
    }

    public void deleteArticles() {
        menuDelete.setVisible(false);
        if (!singleton.isDataBase()) {
            arrayFavorite = dataBase.showSavedArticles(this);
            for (int i = 0; i < dataBase.showSavedArticles(this).size(); i++) {
                if (resultClass.getTitle().get(singleton.getPosition()).equals(arrayFavorite.get(i)))
                    positionDelete = i;
            }
            dataBase.deleteArticles(this, positionDelete);
            menuSave.setVisible(true);
        } else {
            dataBase.deleteArticles(this, singleton.getPosition());
            arrayFavorite = dataBase.showSavedArticles(this);
            singleton.getArrayAdapter().clear();
            singleton.getArrayAdapter().addAll(arrayFavorite);
            if (listView != null) {
                listView.setItemChecked(-1, true);
                listView.setSelection(0);
            }
        }
    }

    public void showDialogInfo(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("OK", this);
        dialogInfo = builder.create();
        dialogInfo.setOwnerActivity(BaseActivity.this);
        dialogInfo.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }

    public void inVisible() {
        if (detailsFragment != null) {
            textView.setText("");
            imageView.setVisibility(View.INVISIBLE);
        }
    }

    public byte[] getByteArrayFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() == null) {
            return false;
        } else return connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public void savedInRotation(Bundle savedInstanceState, String parameter, View view) {
        switch (savedInstanceState.getInt(parameter)) {

            case View.VISIBLE:
                view.setVisibility(View.VISIBLE);
                break;

            case View.INVISIBLE:
                view.setVisibility(View.INVISIBLE);
                break;

            case View.GONE:
                view.setVisibility(View.GONE);
                break;

            default:
                break;
        }
    }

    public void savedInRotation(Bundle savedInstanceState, String parameter, MenuItem menuItem) {
        if (savedInstanceState.getBoolean(parameter)) menuItem.setVisible(true);
        else menuItem.setVisible(false);
    }

    public void savedInRotation(Bundle outState) {
        String text = (String) textView.getText();
        try {
            bytes = getByteArrayFromBitmap(((BitmapDrawable) imageView.getDrawable()).
                    getBitmap());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        outState.putByteArray("bytes", bytes);
        outState.putString("text", text);
    }

    public void restoreInRotation(Bundle savedInstanceState) {
        textView.setText(savedInstanceState.getString("text"));
        try {
            imageView.setImageBitmap(new BaseFragment().getBitmapFromByteArray(savedInstanceState
                    .getByteArray("bytes")));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public Boolean checkIsArticlesInDateBase() {
        Boolean check = false;
        arrayFavorite = dataBase.showSavedArticles(this);
        if (dataBase.isCursorToFirst()) {
            for (int i = 0; i < arrayFavorite.size(); i++) {
                if (resultClass.getTitle().get(singleton.getPosition()).equals(arrayFavorite.get(i)))
                    check = true;
            }
        }
        if (check) {
            menuSave.setVisible(false);
            menuDelete.setVisible(true);
        } else {
            menuSave.setVisible(true);
            menuDelete.setVisible(false);
        }
        return check;
    }
}
