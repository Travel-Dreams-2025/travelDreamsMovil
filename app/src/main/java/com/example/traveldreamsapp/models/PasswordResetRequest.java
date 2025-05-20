package com.example.traveldreamsapp.models;

public class PasswordResetRequest {
    private String email;

    public PasswordResetRequest(String email) {
        this.email = email;
    }

    // Getter y Setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
