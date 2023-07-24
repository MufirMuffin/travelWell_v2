package com.example.travelwell_v1;

import java.io.Serializable;

public class Review implements Serializable {
    private String placeID;
    private String userID;
    private int rating;
    private String comment;

    // Constructor

    public Review(){
        //empty constructor
    }

    public Review(String userID, int rating, String comment) {
        this.userID = userID;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and setters for the class variables

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
