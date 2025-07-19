package com.kernelapps.onlinejobz.models;

public class Category {
    private final String id;
    private final String title;

    public Category(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
