package com.example.traveldreamsapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.traveldreamsapp.network.ApiService;
import com.example.traveldreamsapp.network.LoginRequest;
import com.example.traveldreamsapp.network.LoginResponse;
import com.example.traveldreamsapp.ui.destinos.DestinosFragment;
import com.example.traveldreamsapp.ui.destinos.DestinosViewModel;
import com.google.android.material.navigation.NavigationView;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView forgotPassword;
    private boolean isPasswordVisible = false;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.getToken();
        Log.d("LoginActivity", "Token recuperado: " + token);

        if (token != null && !token.isEmpty()) {
            startActivity(new Intent(this, PerfilActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        // Configuración del Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar); // Asegúrate de tener un Toolbar en tu layout

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(item -> {
            // Manejar los clicks del menú aquí
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, PerfilActivity.class));
            } else if (id == R.id.nav_destinos) {
                startActivity(new Intent(this, NavigatorDrawer.class));
            }
            // Añade más items según tu menú

            drawerLayout.closeDrawers();
            return true;
        });

        // Configuración del login (tu código original)
        editTextEmail = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        forgotPassword = findViewById(R.id.forgotPassword);

        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RecoveryPasswordActivity.class);
            startActivity(intent);
        });

        TextView registerPrompt = findViewById(R.id.registerPrompt);
        registerPrompt.setOnClickListener(v -> {
            Intent registerIntent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(registerIntent);
        });

        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://dreamtravel.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Por favor, ingrese ambos campos", Toast.LENGTH_SHORT).show();
                return;
            }

            LoginRequest loginRequest = new LoginRequest(email, password);
            Call<LoginResponse> call = apiService.login(loginRequest);

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String token = response.body().getAccessToken();
                        sessionManager.saveToken(token);

                        Toast.makeText(LoginActivity.this, "Login exitoso, bienvenido!", Toast.LENGTH_SHORT).show();
                        Log.d("TOKEN_SAVE", "Token guardado: " + token);

                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(LoginActivity.this, PerfilActivity.class);
                            startActivity(intent);
                            finish();
                        }, 1500);
                    } else {
                        try {
                            ResponseBody errorBody = response.errorBody();
                            if (errorBody != null) {
                                String errorMsg = errorBody.string();
                                Log.e("LOGIN_ERROR", errorMsg);
                                Toast.makeText(LoginActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Log.e("LOGIN_ERROR", "No se pudo leer el cuerpo del error", e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("LOGIN_FAILURE", t.getMessage(), t);
                }
            });
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            editTextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        isPasswordVisible = !isPasswordVisible;
        editTextPassword.setSelection(editTextPassword.length());
    }
}