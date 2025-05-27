// src/main/java/com/example/traveldreamsapp/models/DailyForecast.java

package com.example.traveldreamsapp.models;

import com.google.gson.annotations.SerializedName;

public class DailyForecast {
    @SerializedName("time")
    private String time;
    @SerializedName("values")
    private DailyWeatherValues values;

    public String getTime() {
        return time;
    }

    public DailyWeatherValues getValues() {
        return values;
    }
}