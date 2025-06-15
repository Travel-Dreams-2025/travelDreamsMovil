package com.example.traveldreamsapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Carrito implements Parcelable {
    private int id_compra;
    private int id_destino;
    private String nombre_Destino;
    private String fecha_salida;
    private double precio_Destino;
    private String image;
    private int cantidad;
    private double subTotal;

    public Carrito() {
    }

    public Carrito(int id_compra, int id_destino, String nombre_Destino, String fecha_salida,
                   double precio_Destino, String image, int cantidad) {
        this.id_compra = id_compra;
        this.id_destino = id_destino;
        this.nombre_Destino = nombre_Destino;
        this.fecha_salida = fecha_salida;
        this.precio_Destino = precio_Destino;
        this.image = image;
        this.cantidad = cantidad;
        this.subTotal = precio_Destino * cantidad;
    }

    // Getters y Setters
    public int getId_compra() {
        return id_compra;
    }

    public void setId_compra(int id_compra) {
        this.id_compra = id_compra;
    }

    public int getId_destino() {
        return id_destino;
    }

    public void setId_destino(int id_destino) {
        this.id_destino = id_destino;
    }

    public String getNombre_Destino() {
        return nombre_Destino;
    }

    public void setNombre_Destino(String nombre_Destino) {
        this.nombre_Destino = nombre_Destino;
    }

    public String getFecha_salida() {
        return fecha_salida;
    }

    public void setFecha_salida(String fecha_salida) {
        this.fecha_salida = fecha_salida;
    }

    public double getPrecio_Destino() {
        return precio_Destino;
    }

    public void setPrecio_Destino(double precio_Destino) {
        this.precio_Destino = precio_Destino;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.subTotal = this.precio_Destino * cantidad;
    }

    public double getSubTotal() {
        return subTotal;
    }

    // Implementación de Parcelable
    protected Carrito(Parcel in) {
        id_compra = in.readInt();
        id_destino = in.readInt();
        nombre_Destino = in.readString();
        fecha_salida = in.readString();
        precio_Destino = in.readDouble();
        image = in.readString();
        cantidad = in.readInt();
        subTotal = in.readDouble();
    }

    public static final Creator<Carrito> CREATOR = new Creator<Carrito>() {
        @Override
        public Carrito createFromParcel(Parcel in) {
            return new Carrito(in);
        }

        @Override
        public Carrito[] newArray(int size) {
            return new Carrito[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id_compra);
        dest.writeInt(id_destino);
        dest.writeString(nombre_Destino);
        dest.writeString(fecha_salida);
        dest.writeDouble(precio_Destino);
        dest.writeString(image);
        dest.writeInt(cantidad);
        dest.writeDouble(subTotal);
    }
}
