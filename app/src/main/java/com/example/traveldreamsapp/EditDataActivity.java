package com.example.traveldreamsapp;

import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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
import retrofit2.http.PUT;

import java.io.IOException;

public class EditDataActivity extends AppCompatActivity {

    private EditText etName, etSurname, etAddress, etPhone;
    private Button btnSaveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perfil);

        etName = findViewById(R.id.et_name);
        etSurname = findViewById(R.id.et_surname);
        etAddress = findViewById(R.id.et_address);
        etPhone = findViewById(R.id.et_phone);
        btnSaveData = findViewById(R.id.btn_save_data);

        // Recibir datos desde PerfilActivity
        Intent intent = getIntent();
        String nameExtra = intent.getStringExtra("name");
        String surnameExtra = intent.getStringExtra("surname");
        String addressExtra = intent.getStringExtra("address");
        String phoneExtra = intent.getStringExtra("phone");

        etName.setText(nameExtra);
        etSurname.setText(surnameExtra);
        etAddress.setText(addressExtra);
        etPhone.setText(phoneExtra);

        // Borrar texto al enfocar si coincide con el original
        setClearOnFocus(etName, nameExtra);
        setClearOnFocus(etSurname, surnameExtra);
        setClearOnFocus(etAddress, addressExtra);
        setClearOnFocus(etPhone, phoneExtra);

        btnSaveData.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String surname = etSurname.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (name.isEmpty() || surname.isEmpty() || address.isEmpty()) {
                Toast.makeText(EditDataActivity.this, "Nombre, Apellido y dirección son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            actualizarPerfil(name, surname, address, phone);
        });
    }

    private void setClearOnFocus(EditText editText, String originalValue) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && editText.getText().toString().equals(originalValue)) {
                editText.setText("");
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
            Request.Builder builder = original.newBuilder();
            if (token != null && !token.isEmpty()) {
                builder.header("Authorization", "Bearer " + token);
            }
            Request request = builder.build();
            return chain.proceed(request);
        }
    }

    public interface ApiService {
        @PUT("api/v1/user/profile/")
        Call<Void> updateUserProfile(@Body UpdateProfileRequest body);
    }

    public static class UpdateProfileRequest {
        private String nombre;
        private String apellido;
        private String direccion;
        private String telefono;

        public UpdateProfileRequest(String nombre, String apellido, String direccion, String telefono) {
            this.nombre = nombre;
            this.apellido = apellido;
            this.direccion = direccion;
            this.telefono = telefono;
        }
    }

    private void actualizarPerfil(String nombre, String apellido, String direccion, String telefono) {
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

        UpdateProfileRequest request = new UpdateProfileRequest(nombre, apellido, direccion, telefono);

        Call<Void> call = apiService.updateUserProfile(request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditDataActivity.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("name", nombre);
                    resultIntent.putExtra("surname", apellido);
                    resultIntent.putExtra("address", direccion);
                    resultIntent.putExtra("phone", telefono);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(EditDataActivity.this, "Error al actualizar perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditDataActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
