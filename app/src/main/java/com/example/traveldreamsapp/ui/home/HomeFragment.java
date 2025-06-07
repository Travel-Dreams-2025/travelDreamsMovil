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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traveldreamsapp.R;
import com.example.traveldreamsapp.adapter.DestinosAdapter;
import com.example.traveldreamsapp.databinding.FragmentHomeBinding;
import com.example.traveldreamsapp.models.Destinos;
import com.example.traveldreamsapp.network.ApiClient;
import com.example.traveldreamsapp.network.ApiService;

import com.example.traveldreamsapp.models.RealtimeWeatherResponse;
import com.example.traveldreamsapp.models.WeatherCodeMapper;
import com.example.traveldreamsapp.network.TomorrowioApiService;
import com.example.traveldreamsapp.network.RetrofitClientTomorrowio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements DestinosAdapter.OnItemClickListener {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private List<Destinos> destinos = new ArrayList<>();
    private DestinosAdapter destinosAdapter;

    private TextView textViewTemperature;
    private ImageView imageViewWeatherIcon;
    private TextView textViewWeatherCondition;

    private static final String TAG = "WeatherIntegration";
    private static final String TOMORROW_IO_API_KEY = "i2jAyklSRVw7Fh9is8eA1gzPn7KhfZPw";
    private static final String LOCATION_COORDS = "-31.4167,-64.1833";
    private static final String UNITS = "metric";

    private static final String FIELDS = "temperature,weatherCode,temperatureApparent,windSpeed";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        textViewTemperature = root.findViewById(R.id.textViewTemperature);
        imageViewWeatherIcon = root.findViewById(R.id.imageViewWeatherIcon);
        textViewWeatherCondition = root.findViewById(R.id.textViewWeatherCondition);

        fetchRealtimeWeather();

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
                    Log.e(TAG, "Error al cargar destinos: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Destinos>> call, Throwable throwable) {
                Toast.makeText(getContext(), "ERROR DE CONEXIÓN: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Fallo al cargar destinos: " + throwable.getMessage(), throwable);
            }
        });
    }

    private void fetchRealtimeWeather() {
        TomorrowioApiService apiService = RetrofitClientTomorrowio.getTomorrowioApiService();

        apiService.getRealtimeWeather(LOCATION_COORDS, TOMORROW_IO_API_KEY, UNITS, FIELDS)
                .enqueue(new Callback<RealtimeWeatherResponse>() {
                    @Override
                    public void onResponse(Call<RealtimeWeatherResponse> call, Response<RealtimeWeatherResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            RealtimeWeatherResponse realtimeResponse = response.body();
                            Log.d(TAG, "Respuesta COMPLETA de la API (Realtime) en HomeFragment: " + realtimeResponse.toString());

                            if (realtimeResponse.getData() != null && realtimeResponse.getData().getValues() != null) {
                                Double currentTemp = realtimeResponse.getData().getValues().getTemperature();
                                Integer weatherCode = realtimeResponse.getData().getValues().getWeatherCode();
                                Double apparentTemp = realtimeResponse.getData().getValues().getTemperatureApparent();

                                if (currentTemp != null) {
                                    textViewTemperature.setText(String.format("%.0f°C", currentTemp));
                                } else {
                                    textViewTemperature.setText("--°C");
                                }

                                Log.d(TAG, "DEBUG en HomeFragment: weatherCode recibido (Realtime): " + weatherCode);

                                if (weatherCode != null) {
                                    int iconResId = WeatherCodeMapper.getWeatherIconResId(weatherCode);
                                    imageViewWeatherIcon.setImageResource(iconResId);

                                    String weatherCondition = WeatherCodeMapper.getWeatherConditionText(weatherCode);
                                    if (apparentTemp != null) {
                                        textViewWeatherCondition.setText(String.format("%s (ST %.0f°C)", weatherCondition, apparentTemp));
                                    } else {
                                        textViewWeatherCondition.setText(weatherCondition);
                                    }
                                } else {
                                    Log.d(TAG, "weatherCode es nulo en tiempo real, mostrando 'Desconocido'");
                                    imageViewWeatherIcon.setImageResource(R.drawable.ic_clima_desconocido);
                                    textViewWeatherCondition.setText("Desconocido");
                                }

                                Log.d(TAG, "Clima cargado en HomeFragment (Realtime): " + textViewWeatherCondition.getText() + " " + textViewTemperature.getText());
                            } else {
                                Log.e(TAG, "Valores de clima en tiempo real son nulos o vacíos en HomeFragment.");
                                textViewTemperature.setText("--°C");
                                imageViewWeatherIcon.setImageResource(R.drawable.ic_clima_desconocido);
                                textViewWeatherCondition.setText("Datos de clima no disponibles");
                            }
                        } else {
                            String errorBody = "N/A";
                            try {
                                if (response.errorBody() != null) {
                                    errorBody = response.errorBody().string();
                                }
                            } catch (IOException e) {
                                errorBody = "Error al leer errorBody";
                                Log.e(TAG, "Error al leer errorBody en HomeFragment: " + e.getMessage());
                            }
                            Log.e(TAG, "Error en la respuesta de la API del clima (Realtime) en HomeFragment: Código " + response.code() + ", Mensaje: " + response.message() + ", Body: " + errorBody);
                            Toast.makeText(getContext(), "Error al cargar el clima: " + response.code(), Toast.LENGTH_LONG).show();
                            textViewTemperature.setText("--°C");
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_clima_desconocido);
                            textViewWeatherCondition.setText("Error al cargar clima");
                        }
                    }

                    @Override
                    public void onFailure(Call<RealtimeWeatherResponse> call, Throwable t) {
                        Log.e(TAG, "Fallo al conectar con la API del clima (Realtime) en HomeFragment: " + t.getMessage(), t);
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