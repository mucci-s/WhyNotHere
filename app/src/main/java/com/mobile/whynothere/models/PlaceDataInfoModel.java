package com.mobile.whynothere.models;

import com.google.android.gms.maps.model.LatLng;

public class PlaceDataInfoModel {

    private String placeID;
    private LatLng position;
    private String title;
    private String description;
    private int stars;
    private double distance;

    public PlaceDataInfoModel(String placeID, LatLng position, String title, String description, int stars) {
        this.placeID = placeID;
        this.position = position;
        this.title = title;
        this.description = description;
        this.stars = stars;
        this.distance = 0;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
