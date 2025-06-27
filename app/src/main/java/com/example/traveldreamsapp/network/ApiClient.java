package com.example.traveldreamsapp.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;
    public static Retrofit getClient (){
        retrofit = new Retrofit.Builder()
                .baseUrl("https://dreamtravelmp.pythonanywhere.com/api/v1/") // Actualizado [cite: 1]
                .addConverterFactory(GsonConverterFactory.create()) //Gson
                .build();
        return retrofit;
    }
}
