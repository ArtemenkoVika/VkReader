package com.example.admin.vkreader.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.vkreader.R;
import com.example.admin.vkreader.async_task.LoadImageFromNetwork;
import com.example.admin.vkreader.entity.DataBaseOfFavoriteEntity;
import com.example.admin.vkreader.entity.ResultClass;
import com.example.admin.vkreader.fragments.ListFragment;
import com.example.admin.vkreader.java_classes.DataBase;
import com.example.admin.vkreader.patterns.Singleton;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class BaseActivity extends FragmentActivity implements DialogInterface.OnClickListener,
        View.OnClickListener {
    protected Singleton singleton = Singleton.getInstance();
    protected int position;
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
    protected boolean back = false;
    protected AlertDialog dialogInfo;
    protected ArrayList arrayFavorite;
    protected Boolean check = false;
    protected ListFragment listFragment;

    public void saveArticles(MenuItem menuSave) {
        arrayFavorite = dataBase.showSavedArticles(this);
        if (dataBase.isCursorToFirst()) {
            for (int i = 0; i < arrayFavorite.size(); i++) {
                if (resultClass.getTitle().get(position).equals(arrayFavorite.get(i))) {
                    check = true;
                }
            }
        }
        if (check) {
            showDialogInfo("", getResources().getString(R.string.checked));
            check = false;
        } else {
            LoadImageFromNetwork load = new LoadImageFromNetwork(this);
            load.execute(resultClass.getUrls().get(position));
            byte[] bytes = null;
            try {
                bytes = getByteArrayFromBitmap(load.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            dataEntity = new DataBaseOfFavoriteEntity(resultClass.getTitle().get(position),
                    resultClass.getText().get(position), bytes, resultClass.getUrls().get(position));
            dataBase.addArticles(this, dataEntity);
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

    @Override
    public void onClick(View v) {
        positionDelete = (Integer) v.getTag();
        dataBase.deleteArticles(this, positionDelete);
        singleton.getDeleteAdapter().clear();
        singleton.getDeleteAdapter().addAll(dataBase.showSavedArticles(this));
        dataBase.showSavedArticles(this);
        if (!dataBase.isCursorToFirst()) {
            singleton.setDataBase(false);
            inVisible();
            singleton.getArrayAdapter().clear();
            if (!singleton.isDataBase()) singleton.getArrayAdapter().addAll(resultClass.getTitle());
            getSupportFragmentManager().beginTransaction().replace(R.id.frm, listFragment).commit();
        }
    }

    public void inVisible() {
        if (detailsFragment != null) {
            textView.setText("");
            imageView.setVisibility(View.INVISIBLE);
        }
        menuSave.setVisible(false);
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
}
