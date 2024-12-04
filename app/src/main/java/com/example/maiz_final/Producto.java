package com.example.maiz_final;

import android.os.Parcel;
import android.os.Parcelable;

public class Producto implements Parcelable {
    private String nombre;
    private int precio;
    private int stock;
    private String imageUrl; // Nuevo campo para la URL de la imagen

    // Constructor actualizado
    public Producto(String nombre, int precio, int stock, String imageUrl) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.imageUrl = imageUrl;
    }

    // Constructor Parcelable
    protected Producto(Parcel in) {
        nombre = in.readString();
        precio = in.readInt();
        stock = in.readInt();
        imageUrl = in.readString(); // Leer la URL de la imagen
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Setters
    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Parcelable implementation
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeInt(precio);
        dest.writeInt(stock);
        dest.writeString(imageUrl); // Escribir la URL de la imagen
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Producto> CREATOR = new Creator<Producto>() {
        @Override
        public Producto createFromParcel(Parcel in) {
            return new Producto(in);
        }

        @Override
        public Producto[] newArray(int size) {
            return new Producto[size];
        }
    };
}
