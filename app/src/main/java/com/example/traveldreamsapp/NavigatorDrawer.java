package com.example.traveldreamsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.text.HtmlCompat;
import androidx.appcompat.app.AlertDialog;

import com.example.traveldreamsapp.databinding.ActivityNavigatorDrawerBinding;

public class NavigatorDrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavigatorDrawerBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavigatorDrawerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarNavigationDrawer.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_destinos, R.id.nav_perfil,
                R.id.nav_contacto, R.id.nav_nosotros, R.id.nav_cart)
                .setOpenableLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation_drawer);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation_drawer);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        DrawerLayout drawer = binding.drawerLayout;

        if (id == R.id.nav_cart) {
            navController.navigate(R.id.nav_cart);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_help) {
            Snackbar.make(binding.getRoot(), "Clic en Ayuda desde Drawer", Snackbar.LENGTH_LONG)
                    .setAction("Cerrar", view -> {})
                    .show();
        } else if (id == R.id.nav_about) {
            String htmlString = getString(R.string.about_software_info);
            CharSequence formattedText = HtmlCompat.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_LEGACY);

            new AlertDialog.Builder(this)
                    .setTitle("Acerca de Travel Dreams App")
                    .setMessage(formattedText)
                    .setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss())
                    .show();
        } else if (id == R.id.nav_privacy_policy) {
            startActivity(new Intent(this, PoliticaPrivacidad.class));
        } else if (id == R.id.nav_logout) {
            Snackbar.make(binding.getRoot(), "Cerrando Sesión desde Drawer...", Snackbar.LENGTH_LONG)
                    .setAction("Cerrar", view -> {})
                    .show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            boolean handledByNavigation = NavigationUI.onNavDestinationSelected(item, navController);
            if (handledByNavigation) {
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}