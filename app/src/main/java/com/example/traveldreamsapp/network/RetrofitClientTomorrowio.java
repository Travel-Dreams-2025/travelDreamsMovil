// src/main/java/com/example/traveldreamsapp/network/RetrofitClientTomorrowio.java

package com.example.traveldreamsapp.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientTomorrowio {

    // URL base de la API de Tomorrow.io
    private static final String TOMORROW_IO_BASE_URL = "https://api.tomorrow.io/";

    private static Retrofit retrofit = null;
    private static TomorrowioApiService tomorrowioApiService = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(TOMORROW_IO_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build();
        }
        return retrofit;
    }

    public static TomorrowioApiService getTomorrowioApiService() {
        if (tomorrowioApiService == null) {
            tomorrowioApiService = getClient().create(TomorrowioApiService.class);
        }
        return tomorrowioApiService;
    }
}