package com.example.maiz_final;

public class Producto {
    private String nombre;
    private int precio;
    private int stock;

    // Constructor
    public Producto(String nombre, int precio, int stock) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    // Métodos getter
    public String getNombre() {
        return nombre; // Método correcto para obtener el nombre del producto
    }

    public int getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    // Métodos setter
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
