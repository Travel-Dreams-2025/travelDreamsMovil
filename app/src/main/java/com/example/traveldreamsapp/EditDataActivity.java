package com.example.traveldreamsapp;

import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.PATCH;

import java.io.IOException;

public class EditDataActivity extends AppCompatActivity {

    private TextView tvName, tvSurname, tvDni;
    private EditText etAddress, etPhone;
    private Button btnSaveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perfil);

        tvName = findViewById(R.id.tv_name);
        tvSurname = findViewById(R.id.tv_surname);
        tvDni = findViewById(R.id.tv_dni);
        etAddress = findViewById(R.id.et_address);
        etPhone = findViewById(R.id.et_phone);
        btnSaveData = findViewById(R.id.btn_save_data);

        // Recibir datos desde PerfilActivity
        Intent intent = getIntent();
        String nameExtra = intent.getStringExtra("name");
        String surnameExtra = intent.getStringExtra("surname");
        String addressExtra = intent.getStringExtra("address");
        String phoneExtra = intent.getStringExtra("phone");
        String dniExtra = intent.getStringExtra("dni");

        // Mostrar datos no editables
        tvName.setText(nameExtra);
        tvSurname.setText(surnameExtra);
        tvDni.setText(dniExtra);

        // Mostrar datos editables
        etAddress.setText(addressExtra);
        etPhone.setText(phoneExtra);

        btnSaveData.setOnClickListener(v -> {
            String address = etAddress.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (address.isEmpty()) {
                Toast.makeText(EditDataActivity.this, "La dirección es obligatoria", Toast.LENGTH_SHORT).show();
                return;
            }

            actualizarPerfil(address, phone, dniExtra);
        });
    }

    public static class AuthInterceptor implements Interceptor {
        private final String token;
        public AuthInterceptor(String token) {
            this.token = token;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder();
            if (token != null && !token.isEmpty()) {
                builder.header("Authorization", "Bearer " + token);
            }
            Request request = builder.build();
            return chain.proceed(request);
        }
    }

    public interface ApiService {
        @PATCH("api/v1/profiles/me/update/")
        Call<Void> updateUserProfile(@Body UpdateProfileRequest body);
    }

    public static class UpdateProfileRequest {
        private String address;
        private String telephone;
        private String dni;

        public UpdateProfileRequest(String address, String telephone, String dni) {
            this.address = address;
            this.telephone = telephone;
            this.dni = dni;
        }
    }

    private void actualizarPerfil(String address, String telephone, String dni) {
        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.getToken();
        Log.d("EditDataActivity", "Token leído de SharedPreferences: " + token);
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "No estás autenticado. Por favor inicia sesión.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(token))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://dreamtravel.pythonanywhere.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        UpdateProfileRequest request = new UpdateProfileRequest(address, telephone, dni);

        Call<Void> call = apiService.updateUserProfile(request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditDataActivity.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("name", tvName.getText().toString());
                    resultIntent.putExtra("surname", tvSurname.getText().toString());
                    resultIntent.putExtra("address", address);
                    resultIntent.putExtra("phone", telephone);
                    resultIntent.putExtra("dni", dni);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(EditDataActivity.this, "Error al actualizar perfil. Código: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("EditDataActivity", "Error en la respuesta: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditDataActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("EditDataActivity", "Error de conexión", t);
            }
        });
    }
}