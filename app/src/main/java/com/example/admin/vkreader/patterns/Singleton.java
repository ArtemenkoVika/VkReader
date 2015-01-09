package com.example.admin.vkreader.patterns;

import com.example.admin.vkreader.adapters.CustomAdapter;

import java.util.ArrayList;

public class Singleton {
    public int count = 0;
    private static final Singleton instance = new Singleton();
    private CustomAdapter arrayAdapter;
    private boolean isDataBase = false;
    private ArrayList id;

    private Singleton() {
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
