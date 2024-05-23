package com.example.smartcook;

public class NewRecipesModel {
    private String title;
    private String owner;
    private String imageUrl; // Assuming you want to display an image

    public NewRecipesModel() {
        // Default constructor is required by Firebase
    }

    public NewRecipesModel(String title, String owner, String imageUrl) {
        this.title = title;
        this.owner = owner;
        this.imageUrl = imageUrl;
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

}
