package com.example.smartcook;

public class RecommendationModel {
    private String title;
    private String owner;
    private String time;
    private String imageUrl; // Assuming you want to display an image
    private int random;

    public RecommendationModel() {
        // Default constructor is required by Firebase
    }

    public RecommendationModel(String title, String owner,String time, String imageUrl, int random) {
        this.title = title;
        this.owner = owner;
        this.imageUrl = imageUrl;
        this.time = time;
        this.random = random;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getRandom() {
        return random;
    }

    public void setRandom(int random) {
        this.random = random;
    }
}
