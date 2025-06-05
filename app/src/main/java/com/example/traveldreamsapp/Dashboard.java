package com.example.traveldreamsapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.traveldreamsapp.R;
import com.example.traveldreamsapp.models.DailyForecast;
import com.example.traveldreamsapp.models.WeatherCodeMapper;
import com.example.traveldreamsapp.models.WeatherForecastResponse;
import com.example.traveldreamsapp.network.TomorrowioApiService;
import com.example.traveldreamsapp.network.RetrofitClientTomorrowio;

import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Dashboard extends AppCompatActivity {

    private static final String TAG = "WeatherIntegration";
    // Tu API Key de Tomorrow.io confirmada
    private static final String TOMORROW_IO_API_KEY = "i2jAyklSRVw7Fh9is8eA1gzPn7KhfZPw";
    // Coordenadas de Córdoba, Argentina (mantengo las que usas en el código)
    // Si quieres las de Boston (42.3478,-71.0466), CÁMBIALAS AQUÍ Y EN HomeFragment.java
    private static final String LOCATION_COORDS = "-31.4167,-64.1833";
    private static final String UNITS = "metric";
    private static final String TIMESTEPS = "1d";
    // Campos que pedimos a la API: temperatura promedio, máxima, mínima, y weatherCodeMax
    private static final String FIELDS = "temperatureAvg,temperatureMax,temperatureMin,weatherCodeMax,windSpeedAvg";

    private TextView textViewTemperature;
    private ImageView imageViewWeatherIcon;
    private TextView textViewWeatherCondition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        // Ajuste de insets para barras del sistema

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Inicializar los elementos de UI
        textViewTemperature = findViewById(R.id.textViewTemperature);
        imageViewWeatherIcon = findViewById(R.id.imageViewWeatherIcon);
        textViewWeatherCondition = findViewById(R.id.textViewWeatherCondition);

        // 2. Llamar a la API de clima al iniciar la actividad
        fetchWeatherForecast();
    }

    private void fetchWeatherForecast() {
        TomorrowioApiService apiService = RetrofitClientTomorrowio.getTomorrowioApiService();

        apiService.getForecast(LOCATION_COORDS, TOMORROW_IO_API_KEY, UNITS, TIMESTEPS, FIELDS)
                .enqueue(new Callback<WeatherForecastResponse>() {
                    @Override
                    public void onResponse(Call<WeatherForecastResponse> call, Response<WeatherForecastResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            WeatherForecastResponse weatherResponse = response.body();
                            Log.d(TAG, "Respuesta COMPLETA de la API en Dashboard: " + response.body().toString()); // Log para depuración

                            if (weatherResponse.getTimelines() != null &&
                                    weatherResponse.getTimelines().getDaily() != null &&
                                    !weatherResponse.getTimelines().getDaily().isEmpty()) {

                                DailyForecast todayForecast = weatherResponse.getTimelines().getDaily().get(0);
                                if (todayForecast.getValues() != null) {
                                    Double tempAvg = todayForecast.getValues().getTemperatureAvg();
                                    // *** USAMOS getWeatherCodeMax() que es el campo correcto ***
                                    Integer weatherCode = todayForecast.getValues().getWeatherCodeMax();

                                    if (tempAvg != null) {
                                        textViewTemperature.setText(String.format("%.0f°C", tempAvg));
                                    } else {
                                        textViewTemperature.setText("--°C");
                                    }

                                    // Log para ver el weatherCode que se está procesando
                                    Log.d(TAG, "DEBUG en Dashboard: weatherCodeMax recibido: " + weatherCode);

                                    int iconResId = WeatherCodeMapper.getWeatherIconResId(weatherCode);
                                    imageViewWeatherIcon.setImageResource(iconResId);

                                    String weatherCondition = WeatherCodeMapper.getWeatherConditionText(weatherCode);
                                    textViewWeatherCondition.setText(weatherCondition);

                                    Log.d(TAG, "Clima cargado en Dashboard: " + weatherCondition + " " + tempAvg + "°C");
                                } else {
                                    textViewTemperature.setText("--°C");
                                    imageViewWeatherIcon.setImageResource(R.drawable.ic_clima_desconocido);
                                    textViewWeatherCondition.setText("Datos de clima no disponibles");
                                    Log.e(TAG, "Valores de pronóstico diario son nulos en Dashboard.");
                                }
                            } else {
                                textViewTemperature.setText("--°C");
                                imageViewWeatherIcon.setImageResource(R.drawable.ic_clima_desconocido);
                                textViewWeatherCondition.setText("No hay pronóstico diario disponible");
                                Log.e(TAG, "No se encontraron datos de pronóstico diario en la respuesta del Dashboard.");
                            }
                        } else {
                            String errorBody = "";
                            try {
                                if (response.errorBody() != null) {
                                    errorBody = response.errorBody().string();
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "Error leyendo errorBody en Dashboard: " + e.getMessage());
                            }
                            textViewTemperature.setText("--°C");
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_clima_desconocido);
                            textViewWeatherCondition.setText("Error al cargar el clima");
                            Log.e(TAG, "Error en la respuesta de la API en Dashboard: " + response.code() + " " + response.message() + " " + errorBody);
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherForecastResponse> call, Throwable t) {
                        textViewTemperature.setText("--°C");
                        imageViewWeatherIcon.setImageResource(R.drawable.ic_clima_desconocido);
                        textViewWeatherCondition.setText("Error de red");
                        Log.e(TAG, "Fallo al llamar a la API de clima en Dashboard: " + t.getMessage(), t);
                    }
                });
    }
}