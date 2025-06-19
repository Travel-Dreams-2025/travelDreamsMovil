package com.example.traveldreamsapp.models;

public class MetodoPago {
    private int id;
    private String nombre;

    public MetodoPago(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}