package com.example.traveldreamsapp.network;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("nombre_usuario")
    private String username;

    @SerializedName("apellido_usuario")
    private String surname;

    @SerializedName("mail")
    private String email;

    @SerializedName("telefono")
    private String phone;

    // Constructor con parámetros
    public User(String username, String surname, String email, String phone) {
        this.username = username;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
