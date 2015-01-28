package com.example.admin.vkreader.entity;

public class DataBaseOfFavoriteEntity {
    private String title;
    private String text;
    private byte[] bytes;
    private String url;

    public DataBaseOfFavoriteEntity(String title, String text, byte[] bytes, String url) {
        this.title = title;
        this.text = text;
        this.bytes = bytes;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getUrl() {
        return url;
    }
}
