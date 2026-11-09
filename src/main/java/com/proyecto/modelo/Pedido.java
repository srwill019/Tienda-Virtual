package com.proyecto.modelo;

public class Pedido {
    private final int id;
    private final Producto producto;

    public Pedido(int id, Producto producto) {
        this.id = id;
        this.producto = producto;
    }

    public int getId() {
        return id;
    }

    public Producto getProducto() {
        return producto;
    }

    @Override
    public String toString() {
        return "Pedido #" + id + " - " + producto.getNombre();
    }
}
