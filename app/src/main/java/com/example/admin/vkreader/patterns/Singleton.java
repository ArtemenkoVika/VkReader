package com.example.admin.vkreader.patterns;

import com.example.admin.vkreader.adapters.CustomAdapter;
import com.example.admin.vkreader.adapters.DataDeleteAdapter;

import java.util.ArrayList;

public class Singleton {
    public int count = 0;
    public int count2 = 0;
    private static final Singleton instance = new Singleton();
    private CustomAdapter arrayAdapter;
    private DataDeleteAdapter deleteAdapter;
    private boolean isDataBase = false;
    private boolean delete = false;
    private ArrayList id;
    private int position;

    private Singleton() {
    }

    public DataDeleteAdapter getDeleteAdapter() {
        return deleteAdapter;
    }

    public void setDeleteAdapter(DataDeleteAdapter deleteAdapter) {
        this.deleteAdapter = deleteAdapter;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public synchronized static final Singleton getInstance() {
        return instance;
    }

    public CustomAdapter getArrayAdapter() {
        return arrayAdapter;
    }

    public void setArrayAdapter(CustomAdapter arrayAdapter) {
        this.arrayAdapter = arrayAdapter;
    }

    public boolean isDataBase() {
        return isDataBase;
    }

    public void setDataBase(boolean isDataBase) {
        this.isDataBase = isDataBase;
    }

    public ArrayList getId() {
        return id;
    }

    public void setId(ArrayList id) {
        this.id = id;
    }
}
