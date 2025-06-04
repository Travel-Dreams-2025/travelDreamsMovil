package com.example.traveldreamsapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Destinos implements Parcelable {
    private int id_destino;
    private String nombre_Destino;
    private String descripcion;
    private double precio_Destino;
    private String image;
    private String fecha_salida;
    private int cantidad_Disponible;

    public Destinos() {
    }

    public Destinos(int id_destino, String nombre_Destino, String descripcion, double precio_Destino, String image, String fechaSalida, int cupos) {
        this.id_destino = id_destino;
        this.nombre_Destino = nombre_Destino;
        this.descripcion = descripcion;
        this.precio_Destino = precio_Destino;
        this.image = image;
        this.fecha_salida = fechaSalida;
        this.cantidad_Disponible = cupos;
    }

    // Getters
    public int getId_destino() {
        return id_destino;
    }

    public String getNombre_Destino() {
        return nombre_Destino;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecio_Destino() {
        return precio_Destino;
    }

    public String getImage() {
        return image;
    }

    public String getFecha_salida() {
        return fecha_salida;
    }

    public int getCantidad_Disponible() {
        return cantidad_Disponible;
    }

    // Setters (si son necesarios)
    public void setId_destino(int id_destino) {
        this.id_destino = id_destino;
    }

    public void setNombre_Destino(String nombre_Destino) {
        this.nombre_Destino = nombre_Destino;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setPrecio_Destino(double precio_Destino) {
        this.precio_Destino = precio_Destino;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setFecha_salida(String fecha_salida) {
        this.fecha_salida = fecha_salida;
    }

    public void setCantidad_Disponible(int cantidad_Disponible) {
        this.cantidad_Disponible = cantidad_Disponible;
    }


    // Implementación de Parcelable
    protected Destinos(Parcel in) {
        id_destino = in.readInt();
        nombre_Destino = in.readString();
        descripcion = in.readString();
        precio_Destino = in.readDouble();
        image = in.readString();
        fecha_salida = in.readString();
        cantidad_Disponible = in.readInt();
    }

    public static final Creator<Destinos> CREATOR = new Creator<Destinos>() {
        @Override
        public Destinos createFromParcel(Parcel in) {
            return new Destinos(in);
        }

        @Override
        public Destinos[] newArray(int size) {
            return new Destinos[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id_destino);
        dest.writeString(nombre_Destino);
        dest.writeString(descripcion);
        dest.writeDouble(precio_Destino);
        dest.writeString(image);
        dest.writeString(fecha_salida);
        dest.writeInt(cantidad_Disponible);
    }
}