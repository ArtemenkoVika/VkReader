package com.example.admin.vkreader.java_classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.admin.vkreader.R;
import com.example.admin.vkreader.data_base_helper.DataBaseOfFavorite;
import com.example.admin.vkreader.entity.DataBaseOfFavoriteEntity;
import com.example.admin.vkreader.interfaces.IDataBase;
import com.example.admin.vkreader.patterns.Singleton;

import java.util.ArrayList;

public class DataBase implements IDataBase {
    private Singleton singleton = Singleton.getInstance();
    private boolean b_cursor = false;

    @Override
    public void addArticles(Context context, DataBaseOfFavoriteEntity data) {
        SQLiteDatabase db = DataBaseOfFavorite.getInstance(context).getWritableDatabase();
        if (db == null)
            Toast.makeText(context, context.getResources().getString(R.string.no_connection_database),
                    Toast.LENGTH_LONG).show();
        else {
            ContentValues values = new ContentValues();
            values.put(DataBaseOfFavorite.TITLE, data.getTitle());
            values.put(DataBaseOfFavorite.TEXT, data.getText());
            values.put(DataBaseOfFavorite.PICTURES, data.getBytes());
            db.insert(DataBaseOfFavorite.TABLE_NAME, null, values);
        }
        db.close();
    }

    @Override
    public ArrayList showSavedArticles(Context context) {
        ArrayList arrayList = new ArrayList();
        SQLiteDatabase db = DataBaseOfFavorite.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(DataBaseOfFavorite.TABLE_NAME, null, null, null, null,
                null, null);
        b_cursor = cursor.moveToFirst();
        if (b_cursor) {
            singleton.setId(new ArrayList());
            do {
                int id = cursor.getInt(cursor.getColumnIndex(DataBaseOfFavorite._ID));
                String title = cursor.getString(cursor.getColumnIndex
                        (DataBaseOfFavorite.TITLE));
                singleton.getId().add(id);
                arrayList.add(title);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return arrayList;
    }

    @Override
    public void deleteArticles(Context context, int positionDelete) {
        SQLiteDatabase db = DataBaseOfFavorite.getInstance(context).getWritableDatabase();
        db.delete(DataBaseOfFavorite.TABLE_NAME, DataBaseOfFavorite._ID + "=?",
                new String[]{String.valueOf(singleton.getId().get(positionDelete))});
        db.close();
    }

    @Override
    public void deleteAll(Context context) {
        SQLiteDatabase db = DataBaseOfFavorite.getInstance(context).getReadableDatabase();
        db.delete(DataBaseOfFavorite.TABLE_NAME, null, null);
        db.close();
    }

    @Override
    public boolean isCursorToFirst() {
        return b_cursor;
    }
}
