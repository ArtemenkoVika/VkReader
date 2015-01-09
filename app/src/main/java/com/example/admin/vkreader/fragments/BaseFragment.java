package com.example.admin.vkreader.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.vkreader.async_task.LoadImageFromNetwork;
import com.example.admin.vkreader.data_base_helper.DataBaseOfFavorite;
import com.example.admin.vkreader.entity.ResultClass;
import com.example.admin.vkreader.patterns.Singleton;

import java.util.concurrent.ExecutionException;

public class BaseFragment extends Fragment {
    protected ResultClass resultClass = ResultClass.getInstance();
    protected Singleton singleton;
    protected TextView textView;
    protected ImageView imageView;
    protected int position;
    protected LoadImageFromNetwork load;
    protected Bitmap bitmap;

    public void click() {
        load = new LoadImageFromNetwork(getActivity());
        load.execute(resultClass.getUrls().get(position));
        try {
            imageView.setImageBitmap(load.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        textView.setText(resultClass.getText().get(position));
        imageView.setVisibility(View.VISIBLE);
    }

    public void clickOfDataBase() {
        SQLiteDatabase db = DataBaseOfFavorite.getInstance(getActivity()).getReadableDatabase();
        Cursor cursor = db.query(DataBaseOfFavorite.TABLE_NAME, new String[]{
                        DataBaseOfFavorite.TEXT, DataBaseOfFavorite.PICTURES},
                DataBaseOfFavorite._ID + "=" + singleton.getId().get(position),
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        String text = cursor.getString(cursor.getColumnIndex
                (DataBaseOfFavorite.TEXT));
        byte[] bytes = cursor.getBlob(cursor.getColumnIndex
                (DataBaseOfFavorite.PICTURES));
        bitmap = getBitmapFromByteArray(bytes);
        imageView.setImageBitmap(bitmap);
        imageView.setVisibility(View.VISIBLE);
        textView.setText(text);
        db.close();
        cursor.close();
    }

    public Bitmap getBitmapFromByteArray(byte[] bitmap) {
        return BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
    }
}
