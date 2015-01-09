package com.example.admin.vkreader.entity;

public class DataBaseOfFavoriteEntity {
    private String title;
    private String text;
    private byte[] bytes;

    public DataBaseOfFavoriteEntity(String title, String text, byte[] bytes) {
        this.title = title;
        this.text = text;
        this.bytes = bytes;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
