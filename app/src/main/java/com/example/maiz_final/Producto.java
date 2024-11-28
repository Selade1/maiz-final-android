package com.example.maiz_final;

import android.os.Parcel;
import android.os.Parcelable;

public class Producto implements Parcelable {
    private String nombre;
    private int precio;
    private int stock;

    public Producto(String nombre, int precio, int stock) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
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

    // Setters
    public void setStock(int stock) {
        this.stock = stock;
    }

    // Parcelable implementation
    protected Producto(Parcel in) {
        nombre = in.readString();
        precio = in.readInt();
        stock = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeInt(precio);
        dest.writeInt(stock);
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
