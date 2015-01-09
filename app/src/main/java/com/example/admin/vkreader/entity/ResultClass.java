package com.example.admin.vkreader.entity;

import java.util.ArrayList;

public class ResultClass {
    private ArrayList<String> title;
    private ArrayList<String> text;
    private ArrayList<String> urls;

    private static final ResultClass instance = new ResultClass();

    public static final ResultClass getInstance() {
        return instance;
    }

    private ResultClass() {
    }

    public ArrayList<String> getTitle() {
        return title;
    }

    public void setTitle(ArrayList<String> title) {
        this.title = title;
    }

    public ArrayList<String> getText() {
        return text;
    }

    public void setText(ArrayList<String> text) {
        this.text = text;
    }

    public ArrayList<String> getUrls() {
        return urls;
    }

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
    }
}
