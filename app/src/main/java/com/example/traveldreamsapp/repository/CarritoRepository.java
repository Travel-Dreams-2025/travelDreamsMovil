package com.example.traveldreamsapp.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.traveldreamsapp.SessionManager;
import com.example.traveldreamsapp.models.Carrito;
import com.example.traveldreamsapp.network.ApiService;
import com.example.traveldreamsapp.network.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.traveldreamsapp.network.ApiService.UpdateQuantityRequest;
import com.example.traveldreamsapp.network.ApiService.AddToCartRequest;
import com.example.traveldreamsapp.network.ApiService.UpdateDateRequest;
import com.example.traveldreamsapp.network.ApiService.CheckoutRequest;
import com.example.traveldreamsapp.network.ApiService.CheckoutItem;

public class CarritoRepository {
    private static final String PREF_NAME = "CarritoPrefs";
    private static final String CARRITO_KEY = "carrito_items";
    private final SharedPreferences sharedPreferences;
    private final ApiService apiService;
    private final Gson gson = new Gson();

    public CarritoRepository(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.apiService = RetrofitClient.getRetrofitInstance(new SessionManager(context).getToken()).create(ApiService.class);
    }

    // Métodos para persistencia local
    public void guardarCarritoLocal(List<Carrito> items) {
        String json = gson.toJson(items);
        sharedPreferences.edit().putString(CARRITO_KEY, json).apply();
    }

    public List<Carrito> obtenerCarritoLocal() {
        String json = sharedPreferences.getString(CARRITO_KEY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Carrito>>() {}.getType();
        return gson.fromJson(json, type);
    }

    // Métodos para operaciones con el backend
    public void obtenerCarritoRemoto(Callback<List<Carrito>> callback) {
        Call<List<Carrito>> call = apiService.obtenerCarrito();
        call.enqueue(callback);
    }

    public void agregarAlCarritoRemoto(int idDestino, int cantidad, String fechaSalida, Callback<Void> callback) {
        AddToCartRequest request = new AddToCartRequest(idDestino, cantidad, fechaSalida);
        Call<Void> call = apiService.agregarAlCarrito(request);
        call.enqueue(callback);
    }


    public void eliminarItemRemoto(int idCompra, Callback<Void> callback) {
        Call<Void> call = apiService.eliminarItem(idCompra);
        call.enqueue(callback);
    }

    public void actualizarCantidadRemoto(int idCompra, int nuevaCantidad, Callback<Void> callback) {
        UpdateQuantityRequest request = new UpdateQuantityRequest(nuevaCantidad);
        Call<Void> call = apiService.actualizarCantidad(idCompra, request);
        call.enqueue(callback);
    }


    public void actualizarFechaRemoto(int idCompra, String nuevaFecha, Callback<Void> callback) {
        UpdateDateRequest request = new UpdateDateRequest(nuevaFecha);
        Call<Void> call = apiService.actualizarFecha(idCompra, request);
        call.enqueue(callback);
    }


    public void realizarCheckout(int metodoPagoId, Callback<Void> callback) {
        CheckoutRequest request = new CheckoutRequest(metodoPagoId);
        Call<Void> call = apiService.checkout(request);
        call.enqueue(callback);
    }


}