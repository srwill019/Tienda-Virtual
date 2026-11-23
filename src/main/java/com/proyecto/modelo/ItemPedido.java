package com.proyecto.modelo;

public class ItemPedido {
    private final Producto producto;
    private final int cantidad;
    private final double precioUnitario;
    private final String talla;

    public ItemPedido(Producto producto, int cantidad) {
        this(producto, cantidad, producto == null ? 0 : producto.getPrecio());
    }

    public ItemPedido(Producto producto, int cantidad, double precioUnitario) {
        this(producto, cantidad, precioUnitario, "No especificada");
    }

    public ItemPedido(Producto producto, int cantidad, double precioUnitario, String talla) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto es obligatorio.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        if (!Double.isFinite(precioUnitario) || precioUnitario <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor que cero.");
        }
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.talla = talla == null || talla.isBlank() ? "No especificada" : talla;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getSubtotal() {
        return precioUnitario * cantidad;
    }

    public double getPrecioUnitario() { return precioUnitario; }
    public String getTalla() { return talla; }
}
