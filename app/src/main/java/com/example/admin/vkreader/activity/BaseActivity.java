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

import com.example.admin.vkreader.adapters.CustomAdapter;
import com.example.admin.vkreader.adapters.DataDeleteAdapter;
import com.example.admin.vkreader.async_task.LoadImageFromNetwork;
import com.example.admin.vkreader.entity.DataBaseOfFavoriteEntity;
import com.example.admin.vkreader.entity.ResultClass;
import com.example.admin.vkreader.java_classes.DataBase;
import com.example.admin.vkreader.patterns.Singleton;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;

public class BaseActivity extends FragmentActivity implements DialogInterface.OnClickListener,
        View.OnClickListener {
    protected Singleton singleton = Singleton.getInstance();
    protected int position;
    protected DataBaseOfFavoriteEntity dataEntity;
    protected DataBase dataBase = new DataBase();
    protected ResultClass resultClass = ResultClass.getInstance();
    protected int positionDelete;
    protected CustomAdapter customAdapter;
    protected DataDeleteAdapter deleteAdapter;
    protected TextView textView;
    protected ImageView imageView;
    protected Fragment fragment2;
    protected MenuItem menuSave;
    protected boolean back = false;
    protected AlertDialog dialogDelete;
    protected AlertDialog dialogInfo;

    public void saveArticles(MenuItem menuSave) {
        menuSave.setEnabled(false);
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
                resultClass.getText().get(position), bytes);
        dataBase.addArticles(this, dataEntity);
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

    public void showDialogDelete(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setAdapter(deleteAdapter, this);
        builder.setNegativeButton("Cancel", this);
        builder.setPositiveButton("Delete all", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (singleton.isDataBase()) {
                    inVisible();
                    singleton.getArrayAdapter().clear();
                }
                singleton.setDataBase(false);
                dataBase.deleteAll(BaseActivity.this);
            }
        });
        dialogDelete = builder.create();
        dialogDelete.setOwnerActivity(BaseActivity.this);
        dialogDelete.show();
    }

    @Override
    public void onClick(View v) {
        positionDelete = (Integer) v.getTag();
        dataBase.deleteArticles(this, positionDelete);
        deleteAdapter.clear();
        deleteAdapter.addAll(dataBase.showSavedArticles(this));

        if (back == true) {
            singleton.getArrayAdapter().clear();
            singleton.getArrayAdapter().addAll(dataBase.showSavedArticles(this));
        }

        if (!dataBase.isCursorToFirst()) {
            if (singleton.isDataBase()) {
                inVisible();
                singleton.getArrayAdapter().clear();
            }
            dialogDelete.cancel();
            singleton.setDataBase(false);
        }
    }

    public void inVisible() {
        if (fragment2 != null) {
            textView.setText("");
            imageView.setVisibility(View.INVISIBLE);
        }
        menuSave.setEnabled(false);
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
