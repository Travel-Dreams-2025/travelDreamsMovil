package com.example.traveldreamsapp.ui.destinos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.traveldreamsapp.R;
import com.example.traveldreamsapp.adapter.DestinosAdapter;
import com.example.traveldreamsapp.databinding.FragmentDestinosBinding;
import com.example.traveldreamsapp.models.Destinos;
import com.example.traveldreamsapp.network.ApiClient;
import com.example.traveldreamsapp.network.ApiService;

import java.util.ArrayList;
import java.util.List;

public class DestinosFragment extends Fragment implements DestinosAdapter.OnItemClickListener {

    private List<Destinos> destinos = new ArrayList<>();
    private FragmentDestinosBinding binding;
    private DestinosAdapter destinosAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDestinosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupRecyclerView();
        showDestinos();

        return root;
    }

    private void setupRecyclerView() {
        binding.navDestinos.setLayoutManager(new GridLayoutManager(getContext(), 1));
        // CORREGIDO: Pasar 'this' como OnItemClickListener al adaptador
        destinosAdapter = new DestinosAdapter(destinos, requireContext(), this);
        binding.navDestinos.setAdapter(destinosAdapter);
    }

    public void showDestinos() {
        Call<List<Destinos>> call = ApiClient.getClient().create(ApiService.class).getDestinos();
        call.enqueue(new Callback<List<Destinos>>(){
            @Override
            public void onResponse(Call<List<Destinos>> call, Response<List<Destinos>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    destinos.clear();
                    destinos.addAll(response.body());
                    destinosAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "ERROR: " + response.message(), Toast.LENGTH_SHORT).show();
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
        Bundle bundle = new Bundle();
        bundle.putParcelable("destino_seleccionado", destino);

        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_nav_destinos_to_destinosDetallesFragment, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}