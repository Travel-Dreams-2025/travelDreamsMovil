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
import okhttp3.logging.HttpLoggingInterceptor;

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
    private String originalName, originalSurname, originalDni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perfil);

        // Inicializar vistas
        tvName = findViewById(R.id.tv_name);
        tvSurname = findViewById(R.id.tv_surname);
        tvDni = findViewById(R.id.tv_dni);
        etAddress = findViewById(R.id.et_address);
        etPhone = findViewById(R.id.et_phone);
        btnSaveData = findViewById(R.id.btn_save_data);

        // Recibir datos desde PerfilActivity
        Intent intent = getIntent();
        originalName = intent.getStringExtra("name");
        originalSurname = intent.getStringExtra("surname");
        originalDni = intent.getStringExtra("dni");
        String addressExtra = intent.getStringExtra("address");
        String phoneExtra = intent.getStringExtra("phone");

        // Establecer valores en los campos
        tvName.setText(originalName);
        tvSurname.setText(originalSurname);
        tvDni.setText(originalDni);
        etAddress.setText(addressExtra);
        etPhone.setText(phoneExtra);

        btnSaveData.setOnClickListener(v -> {
            String address = etAddress.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (address.isEmpty()) {
                etAddress.setError("La dirección es obligatoria");
                return;
            }

            actualizarPerfil(originalDni, address, phone);
        });
    }

    public interface ApiService {
        @PATCH("api/v1/profiles/me/update/")
        Call<Void> updateUserProfile(@Body UpdateProfileRequest body);
    }

    public static class UpdateProfileRequest {
        private String telephone;
        private String dni;
        private String address;

        public UpdateProfileRequest(String dni, String address, String telephone) {
            this.dni = dni;
            this.address = address;
            this.telephone = telephone;
        }
    }

    private void actualizarPerfil(String dni, String direccion, String telefono) {
        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "No estás autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configurar interceptor para logging
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(token))
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://dreamtravel.pythonanywhere.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        UpdateProfileRequest request = new UpdateProfileRequest(dni, direccion, telefono);

        Log.d("EditDataActivity", "Enviando actualización: dni=" + dni +
                ", dirección=" + direccion + ", teléfono=" + telefono);

        Call<Void> call = apiService.updateUserProfile(request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditDataActivity.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();

                    // Devolver TODOS los datos necesarios a PerfilActivity
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("name", originalName);
                    resultIntent.putExtra("surname", originalSurname);
                    resultIntent.putExtra("dni", originalDni);
                    resultIntent.putExtra("address", direccion);
                    resultIntent.putExtra("phone", telefono);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Error sin detalles";
                        Log.e("EditDataActivity", "Error al actualizar: " + response.code() + " - " + errorBody);
                        Toast.makeText(EditDataActivity.this,
                                "Error al actualizar: " + errorBody,
                                Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Log.e("EditDataActivity", "Error al procesar mensaje de error", e);
                        Toast.makeText(EditDataActivity.this,
                                "Error al procesar la respuesta",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("EditDataActivity", "Error de conexión", t);
                Toast.makeText(EditDataActivity.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
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
            Request.Builder builder = original.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json");
            return chain.proceed(builder.build());
        }
    }
}