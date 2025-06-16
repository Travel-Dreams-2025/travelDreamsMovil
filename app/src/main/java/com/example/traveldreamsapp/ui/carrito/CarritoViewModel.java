package com.example.traveldreamsapp.ui.carrito;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.traveldreamsapp.models.Carrito;
import com.example.traveldreamsapp.repository.CarritoRepository;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;

public class CarritoViewModel extends ViewModel {
    private final CarritoRepository repository;
    private final MutableLiveData<List<Carrito>> carritoItems = new MutableLiveData<>();
    private final MutableLiveData<Double> totalCarrito = new MutableLiveData<>(0.0);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public CarritoViewModel(CarritoRepository repository) {
        this.repository = repository;
        cargarCarrito();
    }

    public LiveData<List<Carrito>> getCarritoItems() {
        return carritoItems;
    }

    public LiveData<Double> getTotalCarrito() {
        return totalCarrito;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void cargarCarrito() {
        isLoading.setValue(true);
        repository.obtenerCarritoRemoto(new Callback<List<Carrito>>() {
            @Override
            public void onResponse(@NonNull Call<List<Carrito>> call, @NonNull Response<List<Carrito>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    carritoItems.setValue(response.body());
                    calcularTotal(response.body());
                    repository.guardarCarritoLocal(response.body());
                } else {
                    errorMessage.setValue("Error al cargar el carrito");
                    cargarCarritoLocal();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Carrito>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Error de conexión: " + t.getMessage());
                cargarCarritoLocal();
            }
        });
    }

    private void cargarCarritoLocal() {
        List<Carrito> items = repository.obtenerCarritoLocal();
        if (!items.isEmpty()) {
            carritoItems.setValue(items);
            calcularTotal(items);
        } else {
            errorMessage.setValue("No hay items en el carrito");
        }
    }

    private void calcularTotal(List<Carrito> items) {
        double total = 0;
        for (Carrito item : items) {
            total += item.getSubTotal();
        }
        totalCarrito.setValue(total);
    }

    public void eliminarItem(int idCompra) {
        isLoading.setValue(true);
        repository.eliminarItemRemoto(idCompra, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    cargarCarrito();
                } else {
                    errorMessage.setValue("Error al eliminar el item");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void actualizarCantidad(int idCompra, int nuevaCantidad) {
        isLoading.setValue(true);
        repository.actualizarCantidadRemoto(idCompra, nuevaCantidad, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    cargarCarrito();
                } else {
                    errorMessage.setValue("Error al actualizar la cantidad");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void actualizarFecha(int idCompra, String nuevaFecha) {
        isLoading.setValue(true);
        repository.actualizarFechaRemoto(idCompra, nuevaFecha, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    cargarCarrito();
                } else {
                    errorMessage.setValue("Error al actualizar la fecha");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void realizarCheckout(int metodoPagoId) {
        isLoading.setValue(true);
        repository.realizarCheckout(metodoPagoId, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    errorMessage.setValue("Compra realizada con éxito");
                    carritoItems.setValue(new ArrayList<>());
                    totalCarrito.setValue(0.0);
                } else {
                    errorMessage.setValue("Error al realizar el checkout");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Error de conexión: " + t.getMessage());
            }
        });
    }
}