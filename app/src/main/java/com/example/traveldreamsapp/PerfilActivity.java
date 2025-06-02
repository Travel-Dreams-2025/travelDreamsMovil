package com.example.traveldreamsapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.traveldreamsapp.network.RetrofitClient;
import com.example.traveldreamsapp.network.ApiService;
import com.example.traveldreamsapp.models.UserProfileResponse;

public class PerfilActivity extends AppCompatActivity {

    private TextView tvName, tvSurname, tvEmail, tvPhone, tvAddress, tvDni;
    private Button btnEditData, btnLogout, btnEditPassword;
    private ShapeableImageView profileImage;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;

    private static final int PICK_IMAGE = 100;
    private static final int REQUEST_PERMISSION_READ_STORAGE = 1;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        sessionManager = new SessionManager(this);

        // Configuración del Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_perfil) {
                // Ya estamos en el perfil, solo cerramos el drawer
            } else if (id == R.id.nav_destinos) {
                startActivity(new Intent(this, NavigatorDrawer.class));
            } else if (id == R.id.nav_logout) {
                logout();
            }
            // Añade más items según tu menú

            drawerLayout.closeDrawers();
            return true;
        });

        // Inicializar vistas
        tvName = findViewById(R.id.tv_name);
        tvSurname = findViewById(R.id.tv_full_name);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        tvAddress = findViewById(R.id.tv_address);
        tvDni = findViewById(R.id.tv_dni);

        btnEditData = findViewById(R.id.btn_edit_data);
        btnLogout = findViewById(R.id.buttonLogout);
        btnEditPassword = findViewById(R.id.btn_edit_password);
        profileImage = findViewById(R.id.profile_image);

        // Cargar imagen de perfil guardada
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String imageUriString = sharedPreferences.getString("profileImageUri", null);
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            profileImage.setImageURI(imageUri);
        }

        // Listeners
        profileImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(PerfilActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PerfilActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_STORAGE);
            } else {
                openGallery();
            }
        });

        btnEditData.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, EditDataActivity.class);
            intent.putExtra("name", tvName.getText().toString());
            intent.putExtra("surname", tvSurname.getText().toString());
            intent.putExtra("email", tvEmail.getText().toString());
            intent.putExtra("phone", tvPhone.getText().toString());
            intent.putExtra("address", tvAddress.getText().toString());
            intent.putExtra("dni", tvDni.getText().toString());
            startActivityForResult(intent, 1);
        });

        btnEditPassword.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, ChangePasswordActivity.class);
            intent.putExtra("email", tvEmail.getText().toString());
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> logout());

        loadUserProfileFromBackend();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    private void loadUserProfileFromBackend() {
        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Token inválido o sesión no iniciada", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getRetrofitInstance(token).create(ApiService.class);

        Call<UserProfileResponse> call = apiService.getUserProfile("Bearer " + token);
        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse user = response.body();

                    runOnUiThread(() -> {
                        tvName.setText(user.getFirstName());
                        tvSurname.setText(user.getLastName());
                        tvEmail.setText(user.getEmail());
                        tvPhone.setText(user.getTelephone());
                        tvAddress.setText(user.getAddress());
                        tvDni.setText(user.getDni());
                    });
                } else {
                    Toast.makeText(PerfilActivity.this, "Error al cargar perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Toast.makeText(PerfilActivity.this, "Error en la red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            profileImage.setImageURI(selectedImage);

            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("profileImageUri", selectedImage.toString());
            editor.apply();
        }

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            tvName.setText(data.getStringExtra("name"));
            tvSurname.setText(data.getStringExtra("surname"));
            tvPhone.setText(data.getStringExtra("phone"));
            tvAddress.setText(data.getStringExtra("address"));
            tvDni.setText(data.getStringExtra("dni"));

            updateUserProfile(data.getStringExtra("address"),
                    data.getStringExtra("phone"),
                    data.getStringExtra("dni"));
        }
    }

    private void updateUserProfile(String address, String telephone, String dni) {
        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Token inválido o sesión no iniciada", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getRetrofitInstance(token).create(ApiService.class);
        ApiService.UpdateProfileRequest updateRequest = new ApiService.UpdateProfileRequest(address, telephone);

        Call<UserProfileResponse> call = apiService.updateUserProfile("Bearer " + token, updateRequest);
        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(PerfilActivity.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PerfilActivity.this, "Error al actualizar perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Toast.makeText(PerfilActivity.this, "Error en la red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permiso denegado para leer el almacenamiento externo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void logout() {
        sessionManager.clearSession();

        Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navView)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}