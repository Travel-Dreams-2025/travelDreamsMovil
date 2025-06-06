package com.example.traveldreamsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.traveldreamsapp.models.RegisterRequest;
import com.example.traveldreamsapp.models.RegisterResponse;
import com.example.traveldreamsapp.network.ApiClient;
import com.example.traveldreamsapp.network.ApiService;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroActivity extends AppCompatActivity {

    // Constante con la URL completa
    private static final String BASE_URL = "https://dreamtravel.pythonanywhere.com/";
    private static final String TAG = "RegistroActivity";
    private static final String PREF_NAME = "registro_pref";

    // Views
    private EditText editTextName, editTextLastName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private CheckBox checkBoxPrivacy;
    private Button buttonRegister;
    private TextView textViewPrivacyPolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        initViews();
        loadFormData();
        setupListeners();
    }

    private void initViews() {
        editTextName = findViewById(R.id.editTextName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        checkBoxPrivacy = findViewById(R.id.checkBoxPrivacy);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewPrivacyPolicy = findViewById(R.id.textViewPrivacyPolicy);
    }

    private void setupListeners() {
        buttonRegister.setOnClickListener(v -> {
            if (checkBoxPrivacy.isChecked()) {
                registerUser();
            } else {
                showToast("Debes aceptar la política de privacidad");
            }
        });

        textViewPrivacyPolicy.setOnClickListener(v -> {
            saveFormData();
            startActivity(new Intent(this, PoliticaPrivacidad.class));
        });
    }

    // Método para crear servicio API temporal
    private ApiService createApiService() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ApiService.class);
    }

    private void registerUser() {
        String first_name = editTextName.getText().toString().trim();
        String last_name = editTextLastName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        String password2 = editTextConfirmPassword.getText().toString();

        if (!validateInputs(first_name, last_name, email, password, password2)) {
            return;
        }

        RegisterRequest request = new RegisterRequest(first_name, last_name, email, password, password2);
        Log.d(TAG, "Datos a registrar: " + request.toString());

        // Usamos el servicio temporal
        ApiService apiService = createApiService();
        Call<RegisterResponse> call = apiService.registerUser(request);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful()) {
                    handleRegistrationSuccess();
                } else {
                    handleRegistrationError(response);
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Log.e(TAG, "Error de conexión: " + t.getMessage());
                showToast("Error de conexión. Verifica tu internet");
            }
        });
    }

    private boolean validateInputs(String first_name, String last_name, String email,
                                   String password, String password2) {
        if (TextUtils.isEmpty(first_name) || TextUtils.isEmpty(last_name) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(password2)) {
            showToast("Todos los campos son obligatorios");
            return false;
        }

        if (first_name.length() < 3 || last_name.length() < 3) {
            showToast("Nombre y apellido deben tener al menos 3 letras");
            return false;
        }

        if (first_name.equals(last_name)) {
            showToast("Nombre y apellido no pueden ser iguales");
            return false;
        }

        if (!isNameValid(first_name) || !isNameValid(last_name)) {
            showToast("Solo se permiten letras, acentos y apóstrofes");
            return false;
        }

        if (!isEmailValid(email)) {
            return false;
        }

        String passwordErrors = getPasswordErrors(password);
        if (!passwordErrors.isEmpty()) {
            showToast(passwordErrors);
            return false;
        }

        if (!password.equals(password2)) {
            showToast("Las contraseñas no coinciden");
            return false;
        }

        return true;
    }

    private void handleRegistrationSuccess() {
        showToast("Registro exitoso");
        clearFormData();
        clearForm();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void handleRegistrationError(Response<RegisterResponse> response) {
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
            Log.e(TAG, "Error en registro - Código: " + response.code() + " - Body: " + errorBody);

            if (response.code() == 400 && errorBody.contains("email")) {
                showToast("El email ya está registrado");
            } else {
                showToast("Error en el registro. Código: " + response.code());
            }
        } catch (IOException e) {
            Log.e(TAG, "Error al leer respuesta", e);
            showToast("Error procesando la respuesta");
        }
    }

    private void saveFormData() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        preferences.edit()
                .putString("name", editTextName.getText().toString())
                .putString("lastName", editTextLastName.getText().toString())
                .putString("email", editTextEmail.getText().toString())
                .putString("password", editTextPassword.getText().toString())
                .putString("confirmPassword", editTextConfirmPassword.getText().toString())
                .apply();
    }

    private void loadFormData() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editTextName.setText(preferences.getString("name", ""));
        editTextLastName.setText(preferences.getString("lastName", ""));
        editTextEmail.setText(preferences.getString("email", ""));
        editTextPassword.setText(preferences.getString("password", ""));
        editTextConfirmPassword.setText(preferences.getString("confirmPassword", ""));
    }

    private void clearFormData() {
        getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit().clear().apply();
    }

    private void clearForm() {
        editTextName.setText("");
        editTextLastName.setText("");
        editTextEmail.setText("");
        editTextPassword.setText("");
        editTextConfirmPassword.setText("");
        checkBoxPrivacy.setChecked(false);
    }

    private boolean isNameValid(String name) {
        return Pattern.matches("^[a-zA-ZÀ-ÿ' ]+$", name);
    }

    private boolean isEmailValid(String email) {
        boolean isValid = Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", email);
        if (!isValid) showToast("Correo electrónico no válido");
        return isValid;
    }

    private String getPasswordErrors(String password) {
        StringBuilder errors = new StringBuilder();
        if (password.length() < 8) errors.append("• Mínimo 8 caracteres\n");
        if (!Pattern.compile("[A-Z]").matcher(password).find()) errors.append("• Al menos 1 mayúscula\n");
        if (!Pattern.compile("[!@#$%]").matcher(password).find()) errors.append("• Al menos 1 carácter especial (!@#$%)");
        return errors.toString();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}