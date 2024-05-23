package com.example.smartcook;

public class MenuModel2 {
    private String imageUrl;
    private String title;
    private String selectedIngredients;
    private String id;

    // Add more fields if needed

    // Default constructor (required by Firebase)
    public MenuModel2() {
    }

    public MenuModel2(String imageUrl, String title, String selectedIngredients, String id) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.selectedIngredients = selectedIngredients;
        this.id = id;

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSelectedIngredients() {
        return selectedIngredients;
    }

    public void setSelectedIngredients(String selectedIngredients) {
        this.selectedIngredients = selectedIngredients;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
