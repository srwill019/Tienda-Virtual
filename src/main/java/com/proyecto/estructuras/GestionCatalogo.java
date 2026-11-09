package com.proyecto.estructuras;

import com.proyecto.modelo.Producto;

import java.util.ArrayList;
import java.util.List;

public class GestionCatalogo {
    private final ArrayList<Producto> productos = new ArrayList<>();

    public void cargarDatosPrueba() {
        if (!productos.isEmpty()) {
            return;
        }
        productos.add(new Producto(1, "Camiseta basica", 29900, 12));
        productos.add(new Producto(2, "Jean clasico", 79900, 8));
        productos.add(new Producto(3, "Chaqueta denim", 119900, 5));
        productos.add(new Producto(4, "Vestido floral", 89900, 7));
        productos.add(new Producto(5, "Buzo oversize", 69900, 10));
        productos.add(new Producto(6, "Falda plisada", 59900, 6));
        productos.add(new Producto(7, "Camisa blanca", 64900, 9));
        productos.add(new Producto(8, "Pantalon cargo", 94900, 4));
    }

    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    public List<Producto> getProductos() {
        return new ArrayList<>(productos);
    }

    public int total() {
        return productos.size();
    }
}
