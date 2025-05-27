// src/main/java/com/example/traveldreamsapp/network/TomorrowioApiService.java

package com.example.traveldreamsapp.network;

import com.example.traveldreamsapp.models.WeatherForecastResponse;
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
}