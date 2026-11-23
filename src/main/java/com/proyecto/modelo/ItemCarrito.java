package com.proyecto.modelo;

import java.util.Objects;

/** Una unidad seleccionada con su talla concreta. */
public class ItemCarrito {
    private final Producto producto;
    private final String talla;

    public ItemCarrito(Producto producto, String talla) {
        if (producto == null || talla == null || talla.isBlank()) {
            throw new IllegalArgumentException("El producto y la talla son obligatorios.");
        }
        this.producto = producto;
        this.talla = talla;
    }

    public Producto getProducto() { return producto; }
    public String getTalla() { return talla; }

    @Override
    public boolean equals(Object otro) {
        if (!(otro instanceof ItemCarrito item)) return false;
        return producto.getId() == item.producto.getId() && talla.equals(item.talla);
    }

    @Override
    public int hashCode() { return Objects.hash(producto.getId(), talla); }
}
