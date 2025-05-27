// app/src/main/java/com/example/traveldreamsapp/ui/home/HomeFragment.java

package com.example.traveldreamsapp.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.traveldreamsapp.adapter.DestinosAdapter;
import com.example.traveldreamsapp.databinding.FragmentHomeBinding;
import com.example.traveldreamsapp.models.Destinos;
import com.example.traveldreamsapp.network.ApiClient;
import com.example.traveldreamsapp.network.ApiService;

import com.example.traveldreamsapp.R; // Importación necesaria para R.drawable
import com.example.traveldreamsapp.models.DailyForecast;
import com.example.traveldreamsapp.models.WeatherCodeMapper;
import com.example.traveldreamsapp.models.WeatherForecastResponse;
import com.example.traveldreamsapp.network.TomorrowioApiService;
import com.example.traveldreamsapp.network.RetrofitClientTomorrowio;

import java.io.IOException; // Mantener esta importación para el manejo de errorBody
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private DestinosAdapter destinosAdapter;
    private List<Destinos> destinos;

    private TextView textViewTemperature;
    private ImageView imageViewWeatherIcon;
    private TextView textViewWeatherCondition;

    private static final String TAG = "WeatherIntegration";
    // *** CAMBIA ESTA API KEY POR LA TUYA REAL DE TOMORROW.IO ***
    private static final String TOMORROW_IO_API_KEY = "i2jAyklSRVw7Fh9is8eA1gzPn7KhfZPw"; // ¡Asegúrate de que esta sea tu clave real!
    // Coordenadas de Córdoba, Argentina
    private static final String LOCATION_COORDS = "-31.4167,-64.1833"; // Coordenadas de Córdoba, Argentina
    private static final String UNITS = "metric"; // Unidades métricas (Celsius, km/h)
    private static final String TIMESTEPS = "1d"; // Pronóstico diario (1 día)
    // Campos que pedimos a la API: temperatura promedio, máxima, mínima, y los códigos de clima
    private static final String FIELDS = "temperatureAvg,temperatureMax,temperatureMin,weatherCodeMax,weatherCodeMin,windSpeedAvg";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inicializar vistas del clima
        textViewTemperature = root.findViewById(R.id.textViewTemperature);
        imageViewWeatherIcon = root.findViewById(R.id.imageViewWeatherIcon);
        textViewWeatherCondition = root.findViewById(R.id.textViewWeatherCondition);

        // Llamar a la API del clima
        fetchWeatherForecast();

        // Configuración y muestra de destinos (tu lógica existente)
        setupRecyclerView();
        showDestinos();

        return root;
    }

    private void setupRecyclerView() {
        // Asumiendo que binding.recyclerViewDestinos es el RecyclerView en tu layout
        binding.recyclerViewDestinos.setLayoutManager(new GridLayoutManager(getContext(), 1));
    }

    private void showDestinos() {
        // Lógica existente para cargar destinos
        Call<List<Destinos>> call = ApiClient.getClient().create(ApiService.class).getDestinos();
        call.enqueue(new Callback<List<Destinos>>() {
            @Override
            public void onResponse(Call<List<Destinos>> call, Response<List<Destinos>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    destinos = response.body();
                    destinosAdapter = new DestinosAdapter(destinos, requireContext());
                    binding.recyclerViewDestinos.setAdapter(destinosAdapter);
                } else {
                    Toast.makeText(getContext(), "No se encontraron destinos", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error al cargar destinos: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Destinos>> call, Throwable throwable) {
                Toast.makeText(getContext(), "ERROR DE CONEXIÓN AL CARGAR DESTINOS", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Fallo al cargar destinos: " + throwable.getMessage(), throwable);
            }
        });
    }

    private void fetchWeatherForecast() {
        TomorrowioApiService apiService = RetrofitClientTomorrowio.getTomorrowioApiService();

        apiService.getForecast(LOCATION_COORDS, TOMORROW_IO_API_KEY, UNITS, TIMESTEPS, FIELDS)
                .enqueue(new Callback<WeatherForecastResponse>() {
                    @Override
                    public void onResponse(Call<WeatherForecastResponse> call, Response<WeatherForecastResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            WeatherForecastResponse weatherResponse = response.body();

                            // *** ESTA LÍNEA ES CRUCIAL PARA LA DEPURACIÓN: Muestra la respuesta completa de la API ***
                            Log.d(TAG, "Respuesta COMPLETA de la API: " + response.body().toString());

                            if (weatherResponse.getTimelines() != null &&
                                    weatherResponse.getTimelines().getDaily() != null &&
                                    !weatherResponse.getTimelines().getDaily().isEmpty()) {

                                // Obtener el pronóstico del primer día
                                DailyForecast todayForecast = weatherResponse.getTimelines().getDaily().get(0);

                                if (todayForecast != null && todayForecast.getValues() != null) {
                                    Double tempAvg = todayForecast.getValues().getTemperatureAvg();
                                    Integer weatherCodeMax = todayForecast.getValues().getWeatherCodeMax();

                                    // Mostrar la temperatura
                                    if (tempAvg != null) {
                                        textViewTemperature.setText(String.format("%.0f°C", tempAvg));
                                    } else {
                                        textViewTemperature.setText("--°C");
                                    }

                                    // Mostrar el icono y la condición del clima
                                    if (weatherCodeMax != null) {
                                        // *** ESTA LÍNEA TAMBIÉN ES CRUCIAL PARA LA DEPURACIÓN: Muestra el weatherCodeMax recibido ***
                                        Log.d(TAG, "DEBUG: weatherCodeMax recibido de la API: " + weatherCodeMax);

                                        imageViewWeatherIcon.setImageResource(WeatherCodeMapper.getWeatherIconResId(weatherCodeMax));
                                        textViewWeatherCondition.setText(WeatherCodeMapper.getWeatherConditionText(weatherCodeMax));
                                    } else {
                                        Log.d(TAG, "weatherCodeMax es nulo, mostrando 'Desconocido'");
                                        imageViewWeatherIcon.setImageResource(R.drawable.ic_clima_desconocido);
                                        textViewWeatherCondition.setText("Desconocido");
                                    }

                                } else {
                                    Log.e(TAG, "Valores de pronóstico diario nulos o vacíos.");
                                    textViewTemperature.setText("--°C");
                                    imageViewWeatherIcon.setImageResource(R.drawable.ic_clima_desconocido);
                                    textViewWeatherCondition.setText("No hay valores de clima");
                                }
                            } else {
                                Log.e(TAG, "No hay pronóstico diario disponible o timelines es nulo/vacío.");
                                textViewTemperature.setText("--°C");
                                imageViewWeatherIcon.setImageResource(R.drawable.ic_clima_desconocido);
                                textViewWeatherCondition.setText("No hay pronóstico diario");
                            }

                        } else {
                            // Si la respuesta no es exitosa o el cuerpo es nulo
                            String errorBody = "N/A";
                            try {
                                if (response.errorBody() != null) {
                                    errorBody = response.errorBody().string();
                                }
                            } catch (IOException e) {
                                errorBody = "Error al leer errorBody";
                                Log.e(TAG, "Error al leer errorBody: " + e.getMessage());
                            }
                            Log.e(TAG, "Error en la respuesta de la API del clima: Código " + response.code() + ", Mensaje: " + response.message() + ", Body: " + errorBody);
                            Toast.makeText(getContext(), "Error al cargar el clima: " + response.code(), Toast.LENGTH_LONG).show();
                            textViewTemperature.setText("--°C");
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_clima_desconocido);
                            textViewWeatherCondition.setText("Error al cargar clima");
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherForecastResponse> call, Throwable t) {
                        // Error de conexión, red, etc.
                        Log.e(TAG, "Fallo al conectar con la API del clima: " + t.getMessage(), t);
                        Toast.makeText(getContext(), "ERROR DE RED AL CARGAR CLIMA", Toast.LENGTH_LONG).show();
                        textViewTemperature.setText("--°C");
                        imageViewWeatherIcon.setImageResource(R.drawable.ic_clima_desconocido);
                        textViewWeatherCondition.setText("Error de conexión");
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}