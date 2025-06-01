package com.example.traveldreamsapp.network;

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

    @POST("password-reset/")
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