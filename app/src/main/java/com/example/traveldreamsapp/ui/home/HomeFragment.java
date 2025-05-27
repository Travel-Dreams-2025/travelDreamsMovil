// app/src/main/java/com/example/traveldreamsapp/ui/home/HomeFragment.java

package com.example.traveldreamsapp.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // Mantener esta importación
import android.widget.TextView;  // Mantener esta importación
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView; // Mantener esta importación

import com.example.traveldreamsapp.R;
import com.example.traveldreamsapp.adapter.DestinosAdapter;
import com.example.traveldreamsapp.databinding.FragmentHomeBinding;
import com.example.traveldreamsapp.models.Destinos;
import com.example.traveldreamsapp.network.ApiClient;
import com.example.traveldreamsapp.network.ApiService;

// Importaciones para el clima (todas las que tenías)
import com.example.traveldreamsapp.models.DailyForecast;
import com.example.traveldreamsapp.models.WeatherCodeMapper;
import com.example.traveldreamsapp.models.WeatherForecastResponse;
import com.example.traveldreamsapp.network.TomorrowioApiService;
import com.example.traveldreamsapp.network.RetrofitClientTomorrowio;

import java.io.IOException; // Mantener esta importación para el manejo de errorBody
import java.util.ArrayList; // Mantener esta importación
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements DestinosAdapter.OnItemClickListener {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private List<Destinos> destinos = new ArrayList<>();
    private DestinosAdapter destinosAdapter;

    // Vistas del clima
    private TextView textViewTemperature;
    private ImageView imageViewWeatherIcon;
    private TextView textViewWeatherCondition;

    private static final String TAG = "WeatherIntegration";
    private static final String TOMORROW_IO_API_KEY = "i2jAyklSRVw7Fh9is8eA1gzPn7KhfZPw"; // ¡Asegúrate de que esta sea tu clave real!
    private static final String LOCATION_COORDS = "-31.4167,-64.1833"; // Coordenadas de Córdoba, Argentina
    private static final String UNITS = "metric";
    private static final String TIMESTEPS = "1d";
    private static final String FIELDS = "temperatureAvg,temperatureMax,temperatureMin,weatherCodeMax,weatherCodeMin,windSpeedAvg";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

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
        binding.recyclerViewDestinos.setLayoutManager(new GridLayoutManager(getContext(), 1));
        destinosAdapter = new DestinosAdapter(destinos, requireContext(), this);
        binding.recyclerViewDestinos.setAdapter(destinosAdapter);
    }

    private void showDestinos() {
        Call<List<Destinos>> call = ApiClient.getClient().create(ApiService.class).getDestinos();
        call.enqueue(new Callback<List<Destinos>>() {
            @Override
            public void onResponse(Call<List<Destinos>> call, Response<List<Destinos>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    destinos.clear();
                    destinos.addAll(response.body());
                    destinosAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "No se encontraron destinos o error en la respuesta", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error al cargar destinos: " + response.code() + " " + response.message()); // Mantener esta línea de log
                }
            }

            @Override
            public void onFailure(Call<List<Destinos>> call, Throwable throwable) {
                Toast.makeText(getContext(), "ERROR DE CONEXIÓN: " + throwable.getMessage(), Toast.LENGTH_SHORT).show(); // Mantener este Toast
                Log.e(TAG, "Fallo al cargar destinos: " + throwable.getMessage(), throwable); // Mantener esta línea de log
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

                            Log.d(TAG, "Respuesta COMPLETA de la API: " + response.body().toString());

                            if (weatherResponse.getTimelines() != null &&
                                    weatherResponse.getTimelines().getDaily() != null &&
                                    !weatherResponse.getTimelines().getDaily().isEmpty()) {

                                DailyForecast todayForecast = weatherResponse.getTimelines().getDaily().get(0);

                                if (todayForecast != null && todayForecast.getValues() != null) {
                                    Double tempAvg = todayForecast.getValues().getTemperatureAvg();
                                    Integer weatherCodeMax = todayForecast.getValues().getWeatherCodeMax();

                                    if (tempAvg != null) {
                                        textViewTemperature.setText(String.format("%.0f°C", tempAvg));
                                    } else {
                                        textViewTemperature.setText("--°C");
                                    }

                                    if (weatherCodeMax != null) {
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
                        Log.e(TAG, "Fallo al conectar con la API del clima: " + t.getMessage(), t);
                        Toast.makeText(getContext(), "ERROR DE RED AL CARGAR CLIMA", Toast.LENGTH_LONG).show();
                        textViewTemperature.setText("--°C");
                        imageViewWeatherIcon.setImageResource(R.drawable.ic_clima_desconocido);
                        textViewWeatherCondition.setText("Error de conexión");
                    }
                });
    }

    @Override
    public void onItemClick(Destinos destino) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("destino_seleccionado", destino);

        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_nav_home_to_destinosDetallesFragment, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}