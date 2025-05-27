package com.example.traveldreamsapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView; // Importar RecyclerView

import com.example.traveldreamsapp.R;
import com.example.traveldreamsapp.adapter.DestinosAdapter;
import com.example.traveldreamsapp.databinding.FragmentHomeBinding;
import com.example.traveldreamsapp.models.Destinos;
import com.example.traveldreamsapp.network.ApiClient;
import com.example.traveldreamsapp.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements DestinosAdapter.OnItemClickListener { // Implementa el listener

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private List<Destinos> destinos = new ArrayList<>(); // Inicializar para evitar NullPointerException
    private DestinosAdapter destinosAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupRecyclerView();
        showDestinos(); // Llama a la función para cargar los destinos

        return root;
    }

    private void setupRecyclerView() {
        // CORREGIDO: Usar el ID correcto del RecyclerView de fragment_home.xml
        binding.recyclerViewDestinos.setLayoutManager(new GridLayoutManager(getContext(), 1));
        // CORREGIDO: Pasar 'this' como OnItemClickListener al adaptador
        destinosAdapter = new DestinosAdapter(destinos, requireContext(), this);
        binding.recyclerViewDestinos.setAdapter(destinosAdapter);
    }

    private void showDestinos() {
        Call<List<Destinos>> call = ApiClient.getClient().create(ApiService.class).getDestinos();
        call.enqueue(new Callback<List<Destinos>>() {
            @Override
            public void onResponse(Call<List<Destinos>> call, Response<List<Destinos>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    destinos.clear(); // Limpia la lista existente
                    destinos.addAll(response.body()); // Agrega los nuevos destinos
                    destinosAdapter.notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
                } else {
                    Toast.makeText(getContext(), "No se encontraron destinos o error en la respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Destinos>> call, Throwable throwable) {
                Toast.makeText(getContext(), "ERROR DE CONEXIÓN: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(Destinos destino) {
        // Manejar el clic en un ítem del RecyclerView
        Bundle bundle = new Bundle();
        bundle.putParcelable("destino_seleccionado", destino); // El objeto Destinos debe ser Parcelable

        NavController navController = Navigation.findNavController(requireView());
        // La acción 'action_nav_home_to_destinosDetallesFragment' debe estar definida en mobile_navigation.xml
        navController.navigate(R.id.action_nav_home_to_destinosDetallesFragment, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}