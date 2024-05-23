package com.example.smartcook;

public class IngredientModel {
    private String title;
    private String amount;
    private String imageUrl; // You can use resource ID or a URL for the image

    public IngredientModel() {
        // Default constructor required for Firebase
    }

    public IngredientModel(String title, String amount, String imageUrl) {
        this.title = title;
        this.amount = amount;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getAmount() {
        return amount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
