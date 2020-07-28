package com.example.myapplication.activity;

public class DataModel {

    private String name;
    private String desc;
    private String imageUrl;

    public DataModel(String name, String desc, String imageUrl) {
        this.name = name;
        this.desc = desc;
        this.imageUrl = imageUrl;

    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}