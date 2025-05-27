// src/main/java/com/example/traveldreamsapp/models/Timelines.java

package com.example.traveldreamsapp.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Timelines {
    @SerializedName("daily")
    private List<DailyForecast> daily;

    public List<DailyForecast> getDaily() {
        return daily;
    }
}