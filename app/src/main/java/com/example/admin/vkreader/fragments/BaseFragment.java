package com.example.admin.vkreader.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.vkreader.async_task.LoadImageFromNetwork;
import com.example.admin.vkreader.data_base_helper.DataBaseOfFavorite;
import com.example.admin.vkreader.entity.ResultClass;
import com.example.admin.vkreader.java_classes.DataBase;
import com.example.admin.vkreader.patterns.Singleton;

import java.util.concurrent.ExecutionException;

public class BaseFragment extends Fragment {
    protected ResultClass resultClass = ResultClass.getInstance();
    protected DataBase dataBase = new DataBase();
    protected Singleton singleton;
    protected TextView textView;
    protected ImageView imageView;
    protected LoadImageFromNetwork load;
    protected Bitmap bitmap;
    protected int position;

    public void click() {
        load = new LoadImageFromNetwork(getActivity());
        load.execute(resultClass.getUrls().get(singleton.getPosition()));
        try {
            imageView.setImageBitmap(load.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        textView.setText(resultClass.getText().get(singleton.getPosition()));
        imageView.setVisibility(View.VISIBLE);
    }

    public void clickOfDataBase() {
        SQLiteDatabase db = DataBaseOfFavorite.getInstance(getActivity()).getReadableDatabase();
        Cursor cursor = db.query(DataBaseOfFavorite.TABLE_NAME, new String[]{
                        DataBaseOfFavorite.TEXT, DataBaseOfFavorite.PICTURES},
                DataBaseOfFavorite._ID + "=" + singleton.getId().get(singleton.getPosition()),
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        String text = cursor.getString(cursor.getColumnIndex
                (DataBaseOfFavorite.TEXT));
        byte[] bytes = cursor.getBlob(cursor.getColumnIndex
                (DataBaseOfFavorite.PICTURES));
        try {
            bitmap = getBitmapFromByteArray(bytes);
        } catch (OutOfMemoryError e) {
            System.out.println(e + " - in the BaseFragment");
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);
        imageView.setVisibility(View.VISIBLE);
        textView.setText(text);
        db.close();
        cursor.close();
    }

    public Bitmap getBitmapFromByteArray(byte[] bitmap) {
        return BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() == null) {
            return false;
        } else return connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
