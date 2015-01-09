package com.example.admin.vkreader.data_base_helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DataBaseOfFavorite extends SQLiteOpenHelper implements BaseColumns {
    public static final String DATABASE_NAME = "favorite.db";
    public static final String TABLE_NAME = "article";
    public static final String TITLE = "title";
    public static final String TEXT = "text";
    public static final String PICTURES = "pictures";
    public static final int DATABASE_VERSION = 1;
    private static DataBaseOfFavorite instance;
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME +
            " (" + DataBaseOfFavorite._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE +
            "  VARCHAR(75), " + TEXT + " TEXT, " + PICTURES + " BLOB);";

    private DataBaseOfFavorite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final DataBaseOfFavorite getInstance(Context context) {
        if (instance == null) instance = new DataBaseOfFavorite(context);
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
