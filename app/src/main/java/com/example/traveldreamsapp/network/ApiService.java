package com.example.traveldreamsapp.network;
import com.google.gson.annotations.SerializedName;


import com.example.traveldreamsapp.models.Destinos;
import com.example.traveldreamsapp.models.RegisterRequest;
import com.example.traveldreamsapp.models.RegisterResponse;
import com.example.traveldreamsapp.models.PasswordResetRequest;


import java.util.List;
import retrofit2.http.Header;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    // Endpoint para obtener todos los destinos
    @GET("destinos/") // Ruta actualizada [cite: 4]
    Call<List<Destinos>> getDestinos();

    // Endpoint para obtener un destino por ID
    @GET("destinos/{id_destino}") // Ruta actualizada [cite: 4]
    Call<Destinos> getDestinoById(@Path("id_destino") int id);

    @POST("api/v1/login/")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register/") // Endpoint de registro actualizado [cite: 1]
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("password-reset/")
    Call<Void> sendPasswordResetEmail(@Body PasswordResetRequest request);

    @GET("api/v1/usuarios/")
    Call<List<User>> getUsuarios();

    @GET("api/v1/user/profile/")
    Call<User> getUserProfile(@Header("Authorization") String authHeader);
}
