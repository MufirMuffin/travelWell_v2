package com.example.travelwell_v1;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private String email;
    private String username;
    private String password;
    private List<Review> reviews;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email, String username, String password) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.reviews = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<Review> getReviews() {return reviews;}

    public void addReview(Review review) { reviews.add(review);}
}

