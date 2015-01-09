package com.example.admin.vkreader.interfaces;

import android.content.Context;

import com.example.admin.vkreader.entity.DataBaseOfFavoriteEntity;

import java.util.ArrayList;

public interface IDataBase {
    public void addArticles(Context context, DataBaseOfFavoriteEntity data);

    public void deleteArticles(Context context, int positionDelete);

    public ArrayList showSavedArticles(Context context);

    public boolean isCursorToFirst();

    public void deleteAll(Context context);
}
