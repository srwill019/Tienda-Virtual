package com.proyecto.estructuras;

import com.proyecto.modelo.Producto;

import java.util.ArrayList;
import java.util.List;

public class GestionCatalogo {
    private final ArrayList<Producto> productos = new ArrayList<>();

    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    public List<Producto> getProductos() {
        return new ArrayList<>(productos);
    }
}
