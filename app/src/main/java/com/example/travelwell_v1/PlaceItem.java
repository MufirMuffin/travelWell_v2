package com.example.travelwell_v1;

// Create a new class to hold the place key and Places object
public class PlaceItem {
    private String placeKey;
    private Places place;

    public PlaceItem(String placeKey, Places place) {
        this.placeKey = placeKey;
        this.place = place;
    }

    public String getPlaceKey() {
        return placeKey;
    }

    public Places getPlace() {
        return place;
    }
}
