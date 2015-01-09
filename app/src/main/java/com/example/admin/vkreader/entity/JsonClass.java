package com.example.admin.vkreader.entity;

public class JsonClass {
    private String textContent;
    private int date;
    private String imageContent;

    public JsonClass(String textContent, int date, String imageContent) {
        this.textContent = textContent;
        this.date = date;
        this.imageContent = imageContent;
    }

    public String getTextContent() {
        return textContent;
    }

    public String getImageContent() {
        return imageContent;
    }

    public int getDate() {
        return date;
    }
}
