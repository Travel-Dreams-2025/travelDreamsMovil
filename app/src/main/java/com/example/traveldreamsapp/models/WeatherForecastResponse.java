package com.example.traveldreamsapp.models;

import com.google.gson.annotations.SerializedName;

public class WeatherForecastResponse {

    @SerializedName("timelines") // Debe coincidir con la clave "timelines" en el JSON
    private Timelines timelines; // Esta clase 'Timelines' debe contener la lista 'daily'

    @SerializedName("location") // Debe coincidir con la clave "location" en el JSON
    private Location location; // Esta clase 'Location' es para lat/lon, aunque no la usamos directamente para mostrar el clima

    public Timelines getTimelines() {
        return timelines;
    }

    // Puedes añadir el getter para location si lo necesitas en el futuro
    public Location getLocation() {
        return location;
    }
}