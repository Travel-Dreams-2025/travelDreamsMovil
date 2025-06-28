package com.example.traveldreamsapp.network;

import com.example.traveldreamsapp.models.Carrito;
import com.example.traveldreamsapp.models.Destinos;
import com.example.traveldreamsapp.models.RegisterRequest;
import com.example.traveldreamsapp.models.RegisterResponse;
import com.example.traveldreamsapp.models.PasswordResetRequest;
import com.example.traveldreamsapp.models.UserProfileResponse;

import java.util.List;
import retrofit2.http.Header;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;



import com.google.gson.annotations.SerializedName;

public interface ApiService {

    @GET("destinos/")
    Call<List<Destinos>> getDestinos();

    @GET("destinos/{id_destino}")
    Call<Destinos> getDestinoById(@Path("id_destino") int id);

    @POST("api/v1/auth/login/")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("api/v1/auth/register/")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("accounts/password-reset/")
    Call<Void> sendPasswordResetEmail(@Body PasswordResetRequest request);

    @GET("api/v1/usuarios/")
    Call<List<User>> getUsuarios();

    @GET("api/v1/profiles/me/")
    Call<UserProfileResponse> getUserProfile(@Header("Authorization") String token);

    @PATCH("api/v1/profiles/me/update/")
    Call<UserProfileResponse> updateUserProfile(
            @Header("Authorization") String token,
            @Body UpdateProfileRequest request
    );

// Inicia CARRITO

    @GET("api/v1/carrito/")
    Call<List<Carrito>> obtenerCarrito();

    @POST("api/v1/carrito/add_item/")
    Call<Void> agregarAlCarrito(
            @Body AddToCartRequest request
    );

    @DELETE("api/v1/carrito/{id_compra}/")
    Call<Void> eliminarItem(@Path("id_compra") int idCompra);

    @PUT("api/v1/carrito/{id_compra}/")
    Call<Void> actualizarCantidad(
            @Path("id_compra") int idCompra,
            @Body UpdateQuantityRequest request
    );

    @PUT("api/v1/cart/{id_compra}/update-date/")
    Call<Void> actualizarFecha(
            @Path("id_compra") int idCompra,
            @Body UpdateDateRequest request
    );

    @POST("api/v1/checkout/")
    Call<Void> checkout(@Body CheckoutRequest request);

    // Clases para los request bodies
    class AddToCartRequest {
        @SerializedName("id_destino")
        private int idDestino;

        @SerializedName("cantidad")
        private int cantidad;

        @SerializedName("fecha_salida")
        private String fechaSalida;

        public AddToCartRequest(int idDestino, int cantidad, String fechaSalida) {
            this.idDestino = idDestino;
            this.cantidad = cantidad;
            this.fechaSalida = fechaSalida;
        }
    }

    class UpdateQuantityRequest {
        @SerializedName("cantidad")
        private int cantidad;

        public UpdateQuantityRequest(int cantidad) {
            this.cantidad = cantidad;
        }
    }

    class UpdateDateRequest {
        @SerializedName("fecha_salida")
        private String fechaSalida;

        public UpdateDateRequest(String fechaSalida) {
            this.fechaSalida = fechaSalida;
        }
    }

    // CheckoutRequest ajustada al endpoint real que solo pide metodo_pago
    class CheckoutRequest {
        @SerializedName("metodo_pago")
        private String metodoPago;

        public CheckoutRequest(int metodoPago) {
            this.metodoPago = String.valueOf(metodoPago);
        }
    }

    // Si necesitas mantener CheckoutItem para otra cosa, la dejas, si no puedes borrarla
    class CheckoutItem {
        @SerializedName("id_destino")
        private int idDestino;

        @SerializedName("cantidad")
        private int cantidad;

        public CheckoutItem(int idDestino, int cantidad) {
            this.idDestino = idDestino;
            this.cantidad = cantidad;
        }
    }

// Termina CARRITO






    class UpdateProfileRequest {
        @SerializedName("address")
        private String direccion;

        @SerializedName("telephone")
        private String telefono;


        public UpdateProfileRequest(String direccion, String telefono) {
            this.direccion = direccion;
            this.telefono = telefono;
        }
    }
}