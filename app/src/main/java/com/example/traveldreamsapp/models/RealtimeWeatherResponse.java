package com.example.traveldreamsapp.models;

import com.google.gson.annotations.SerializedName;

public class RealtimeWeatherResponse {
    @SerializedName("data")
    private RealtimeData data;

    public RealtimeData getData() {
        return data;
    }

    public static class RealtimeData {
        @SerializedName("time")
        private String time;
        @SerializedName("values")
        private RealtimeValues values;

        public RealtimeValues getValues() {
            return values;
        }

        public String getTime() {
            return time;
        }
    }

    public static class RealtimeValues {
        @SerializedName("temperature")
        private Double temperature;
        @SerializedName("weatherCode")
        private Integer weatherCode;
        @SerializedName("temperatureApparent")
        private Double temperatureApparent;
        @SerializedName("windSpeed")
        private Double windSpeed;

        public Double getTemperature() {
            return temperature;
        }

        public Integer getWeatherCode() {
            return weatherCode;
        }

        public Double getTemperatureApparent() {
            return temperatureApparent;
        }

        public Double getWindSpeed() {
            return windSpeed;
        }
    }
}