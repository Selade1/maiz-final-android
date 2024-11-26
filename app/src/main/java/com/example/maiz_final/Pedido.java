package com.example.maiz_final;

import java.util.List;
import java.util.Map;

public class Pedido {
    private String id;
    private String cliente;
    private String tipoEntrega;
    private List<Map<String, Object>> productos; // Cambiar HashMap por Map

    // Constructor
    public Pedido(String id, String cliente, String tipoEntrega, List<Map<String, Object>> productos) {
        this.id = id;
        this.cliente = cliente;
        this.tipoEntrega = tipoEntrega;
        this.productos = productos;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getCliente() {
        return cliente;
    }

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public List<Map<String, Object>> getProductos() {
        return productos;
    }

    // Setters (opcional, dependiendo de si necesitas modificar los datos después)
    public void setId(String id) {
        this.id = id;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public void setProductos(List<Map<String, Object>> productos) {
        this.productos = productos;
    }
}
