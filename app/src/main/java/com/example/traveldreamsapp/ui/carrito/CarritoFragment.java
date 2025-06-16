package com.example.traveldreamsapp.ui.carrito;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traveldreamsapp.LoginActivity;
import com.example.traveldreamsapp.R;
import com.example.traveldreamsapp.SessionManager;
import com.example.traveldreamsapp.adapter.CarritoAdapter;
import com.example.traveldreamsapp.databinding.FragmentCarritoBinding;
import com.example.traveldreamsapp.models.MetodoPago;
import com.example.traveldreamsapp.repository.CarritoRepository;
import java.util.Locale;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;

public class CarritoFragment extends Fragment implements CarritoAdapter.OnCarritoActionListener {

    private FragmentCarritoBinding binding;
    private CarritoViewModel carritoViewModel;
    private CarritoAdapter carritoAdapter;
    private List<MetodoPago> metodosPago = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCarritoBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurar ViewModel
        CarritoRepository repository = new CarritoRepository(requireContext());
        carritoViewModel = new ViewModelProvider(this, new CarritoViewModelFactory(repository))
                .get(CarritoViewModel.class);

        // Configurar RecyclerView
        carritoAdapter = new CarritoAdapter(new ArrayList<>(), requireContext(), this);
        binding.recyclerViewCarrito.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewCarrito.setAdapter(carritoAdapter);

        // Configurar Spinner de métodos de pago
        setupMetodosPagoSpinner();

        // Observar cambios en el ViewModel
        carritoViewModel.getCarritoItems().observe(getViewLifecycleOwner(), items -> {
            carritoAdapter = new CarritoAdapter(items, requireContext(), CarritoFragment.this);
            binding.recyclerViewCarrito.setAdapter(carritoAdapter);
        });

        carritoViewModel.getTotalCarrito().observe(
                getViewLifecycleOwner(),
                total -> binding.txtTotalCarrito.setText(String.format(Locale.US, "Total: USD %.2f", total)
                )
        );

        carritoViewModel.getIsLoading().observe(
                getViewLifecycleOwner(),
                isLoading -> binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE)
        );


        carritoViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar botón de finalizar compra
        binding.btnFinalizarCompra.setOnClickListener(v -> {
            if (carritoViewModel.getCarritoItems().getValue() == null ||
                    carritoViewModel.getCarritoItems().getValue().isEmpty()) {
                Toast.makeText(requireContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            Spinner spinner = binding.spinnerMedioPago;
            if (spinner.getSelectedItem() == null || spinner.getSelectedItemPosition() == 0) {
                Toast.makeText(requireContext(), "Seleccione un método de pago", Toast.LENGTH_SHORT).show();
                return;
            }

            MetodoPago metodoSeleccionado = (MetodoPago) spinner.getSelectedItem();
            carritoViewModel.realizarCheckout(metodoSeleccionado.getId());
        });

        // Verificar si el usuario está logueado
        SessionManager sessionManager = new SessionManager(requireContext());

        if (sessionManager.isLoggedIn()) {
            carritoViewModel.cargarCarrito(); // ✅ Cargar el carrito si está logueada
        } else {
            Toast.makeText(requireContext(), "Debes iniciar sesión para ver el carrito", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish(); // Opcional: cerrar fragmento actual para evitar regresar con el botón atrás
            return;
        }

    }

    private void setupMetodosPagoSpinner() {
        // Simular datos de métodos de pago (en una app real estos vendrían del backend)
        metodosPago.add(new MetodoPago(1, "Tarjeta de Crédito"));
        metodosPago.add(new MetodoPago(2, "PayPal"));
        metodosPago.add(new MetodoPago(3, "Transferencia Bancaria"));

        ArrayAdapter<MetodoPago> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                metodosPago
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerMedioPago.setAdapter(adapter);
    }

    @Override
    public void onEliminarItem(int idCompra) {
        carritoViewModel.eliminarItem(idCompra);
    }

    @Override
    public void onActualizarCantidad(int idCompra, int nuevaCantidad) {
        carritoViewModel.actualizarCantidad(idCompra, nuevaCantidad);
    }

    @Override
    public void onActualizarFecha(int idCompra, String nuevaFecha) {
        // Implementar si es necesario
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}