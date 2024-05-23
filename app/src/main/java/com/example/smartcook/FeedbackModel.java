package com.example.smartcook;

public class FeedbackModel {
    private String username;
    private String date;
    private String currenttime;
    private String feedbackContent;

    // Constructor, getters, and setters
    public FeedbackModel(){

    }

    public FeedbackModel(String username, String date, String currenttime, String feedbackText) {
        this.username = username;
        this.date = date;
        this.currenttime = currenttime;
        this.feedbackContent = feedbackText;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCurrentTime() {
        return currenttime;
    }

    public void setCurrentTime(String time) {
        this.currenttime = time;
    }

    public String getFeedbackContent() {
        return feedbackContent;
    }

    public void setFeedbackContent(String feedbackContent) {
        this.feedbackContent = feedbackContent;
    }
}
