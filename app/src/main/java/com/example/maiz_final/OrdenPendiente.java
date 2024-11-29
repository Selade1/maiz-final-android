package com.example.maiz_final;

import java.util.ArrayList;
import java.util.Map;

public class OrdenPendiente {
    private final String id;
    private final String cliente;
    private final String tipo;
    private final String tipoEntrega; // Sólo para pedidos
    private final ArrayList<Map<String, Object>> productos;
    private final String idCamion; // Sólo para envíos
    private final String fecha; // Sólo para envíos
    private String direccion; // Dirección del cliente
    private String telefono; // Teléfono del cliente
    private String correo; // Correo del cliente

    public OrdenPendiente(String id, String cliente, String tipo, String tipoEntrega,
                          ArrayList<Map<String, Object>> productos, String idCamion, String fecha,
                          String direccion, String telefono, String correo) {
        this.id = id;
        this.cliente = cliente;
        this.tipo = tipo;
        this.tipoEntrega = tipoEntrega;
        this.productos = productos;
        this.idCamion = idCamion;
        this.fecha = fecha;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correo = correo;
    }

    public String getId() {
        return id;
    }

    public String getCliente() {
        return cliente;
    }

    public String getTipo() {
        return tipo;
    }

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public ArrayList<Map<String, Object>> getProductos() {
        return productos;
    }

    public String getIdCamion() {
        return idCamion;
    }

    public String getFecha() {
        return fecha;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

}
