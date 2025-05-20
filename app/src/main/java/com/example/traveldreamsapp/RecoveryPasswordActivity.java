package com.example.traveldreamsapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.traveldreamsapp.network.ApiService;
import com.example.traveldreamsapp.network.RetrofitClient;
import com.example.traveldreamsapp.models.PasswordResetRequest;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecoveryPasswordActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_password);

        editTextEmail = findViewById(R.id.editTextEmail);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese su correo electrónico", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Llamada a la API
            sendPasswordResetEmail(email);
        });
    }

    private void sendPasswordResetEmail(String email) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        PasswordResetRequest request = new PasswordResetRequest(email);

        apiService.sendPasswordResetEmail(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RecoveryPasswordActivity.this, "Se ha enviado un correo para restablecer la contraseña", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(RecoveryPasswordActivity.this, "Error al enviar el correo. Verifique el email ingresado.", Toast.LENGTH_LONG).show();
                    Log.e("RecoveryError", "Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RecoveryPasswordActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
