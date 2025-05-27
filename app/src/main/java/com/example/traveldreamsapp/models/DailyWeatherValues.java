// D:\travelDreamsMovil\app\src\main\java\com\example\traveldreamsapp\models\DailyWeatherValues.java

package com.example.traveldreamsapp.models;

import com.google.gson.annotations.SerializedName;

public class DailyWeatherValues {
    @SerializedName("temperatureAvg")
    private Double temperatureAvg;
    @SerializedName("temperatureMax")
    private Double temperatureMax;
    @SerializedName("temperatureMin")
    private Double temperatureMin;

    // Estos son los que necesitamos porque vienen en la API según tus logs
    @SerializedName("weatherCodeMax")
    private Integer weatherCodeMax;
    @SerializedName("weatherCodeMin")
    private Integer weatherCodeMin;

    @SerializedName("windSpeedAvg")
    private Double windSpeedAvg;
    @SerializedName("weatherCodeFullNight") // Este puede estar en tu JSON, si no, lo quitas.
    private Integer weatherCodeFullNight;


    // Getters
    public Double getTemperatureAvg() { return temperatureAvg; }
    public Double getTemperatureMax() { return temperatureMax; }
    public Double getTemperatureMin() { return temperatureMin; }

    public Integer getWeatherCodeMax() { return weatherCodeMax; }
    public Integer getWeatherCodeMin() { return weatherCodeMin; }

    public Double getWindSpeedAvg() { return windSpeedAvg; }
    public Integer getWeatherCodeFullNight() { return weatherCodeFullNight; }
}