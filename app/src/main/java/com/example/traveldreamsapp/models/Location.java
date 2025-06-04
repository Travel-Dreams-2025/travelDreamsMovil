// src/main/java/com/example/traveldreamsapp/models/Location.java
package com.example.traveldreamsapp.models;

import com.google.gson.annotations.SerializedName;

public class Location {
    @SerializedName("lat")
    private Double lat;
    @SerializedName("lon")
    private Double lon;

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }
}