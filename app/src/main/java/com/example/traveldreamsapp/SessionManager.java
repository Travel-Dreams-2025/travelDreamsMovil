package com.example.traveldreamsapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.traveldreamsapp.repository.CarritoRepository;

public class SessionManager {

    private static final String PREF_NAME = "UserSession";
    private static final String KEY_TOKEN = "access_token";
    private SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        // NO crear CarritoRepository aquí para evitar ciclo infinito
    }

    public void saveToken(String token) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, "");
    }

    public boolean isLoggedIn() {
        String token = getToken();
        return token != null && !token.isEmpty();
    }

    public void clearSession() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        // Aquí podrías limpiar carrito local desde otro lugar, no desde acá
    }
}
