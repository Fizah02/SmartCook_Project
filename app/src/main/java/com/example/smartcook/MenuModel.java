package com.example.smartcook;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class MenuModel {
    private String imageUrl;
    private String videoUrl;
    private String title;
    private String owner;
    private String time;
    private String id;
    // Add more fields if needed

    // Default constructor (required by Firebase)
    public MenuModel() {
    }

    public MenuModel(String imageUrl,String videoUrl, String title, String owner, String time, String id) {
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.title = title;
        this.owner = owner;
        this.time = time;
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) { this.time = time;}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}


