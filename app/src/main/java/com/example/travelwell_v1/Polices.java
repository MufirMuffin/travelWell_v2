package com.example.travelwell_v1;

public class Polices {

    private String name;
    private String description;
    private String number;
    private String image;
    private double latitude;
    private double longitude;

    public Polices() {
        // Default constructor required for Firebase
    }

    public Polices(String name, String description, String number, String image, double latitude, double longitude) {

        this.name = name;
        this.description = description;
        this.number = number;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters for the data fields

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

