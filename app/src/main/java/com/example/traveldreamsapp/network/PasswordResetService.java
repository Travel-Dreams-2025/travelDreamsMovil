package com.example.traveldreamsapp.network;

import com.example.traveldreamsapp.models.PasswordResetRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PasswordResetService {
    @POST("accounts/password-reset/")
    Call<Void> sendPasswordResetEmail(@Body PasswordResetRequest request);
}
