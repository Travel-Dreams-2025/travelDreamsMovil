package com.example.traveldreamsapp.network;

import com.example.traveldreamsapp.models.WeatherForecastResponse;
import com.example.traveldreamsapp.models.RealtimeWeatherResponse; // Importación del nuevo modelo

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TomorrowioApiService {
    @GET("v4/weather/forecast")
    Call<WeatherForecastResponse> getForecast(
            @Query("location") String location,
            @Query("apikey") String apiKey,
            @Query("units") String units,
            @Query("timesteps") String timesteps,
            @Query("fields") String fields
    );

    // Método para obtener el clima en tiempo real
    @GET("v4/weather/realtime")
    Call<RealtimeWeatherResponse> getRealtimeWeather(
            @Query("location") String location,
            @Query("apikey") String apiKey,
            @Query("units") String units,
            @Query("fields") String fields
    );
}