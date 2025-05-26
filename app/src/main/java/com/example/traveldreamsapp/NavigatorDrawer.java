package com.example.traveldreamsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat; // Necesario para cerrar el drawer

import com.example.traveldreamsapp.databinding.ActivityNavigatorDrawerBinding;

// La clase implementa el listener para manejar clics en el Navigation Drawer
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
                R.id.nav_home, R.id.nav_destinos, R.id.nav_perfil, R.id.nav_contacto, R.id.nav_nosotros)
                .setOpenableLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation_drawer);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Asigna el listener a la NavigationView
        navigationView.setNavigationItemSelectedListener(this);
    }

    // Este método ya no infla el menú de 3 puntos
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    // Maneja ítems de la Toolbar, si los hubiera
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation_drawer);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // Maneja los clics de los ítems del Navigation Drawer
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Lógica para ítems que abren Activities o tienen acción específica
        if (id == R.id.nav_cart) {
            startActivity(new Intent(this, DashboardCompras.class));
        } else if (id == R.id.nav_help) {
            Snackbar.make(binding.getRoot(), "Clic en Ayuda desde Drawer", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (id == R.id.nav_about) {
            Snackbar.make(binding.getRoot(), "Clic en About desde Drawer", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (id == R.id.nav_privacy_policy) {
            startActivity(new Intent(this, PoliticaPrivacidad.class));
        } else if (id == R.id.nav_logout) {
            Snackbar.make(binding.getRoot(), "Cerrando Sesión desde Drawer...", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        // Si el ítem no fue manejado arriba, lo delega al Navigation Component
        else {
            boolean handledByNavigation = NavigationUI.onNavDestinationSelected(item, navController);
            if (handledByNavigation) {
                DrawerLayout drawer = binding.drawerLayout;
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        }

        // Cierra el drawer después de un clic en cualquier ítem
        DrawerLayout drawer = binding.drawerLayout;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}