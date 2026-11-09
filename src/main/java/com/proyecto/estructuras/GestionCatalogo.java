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
        productos.add(new Producto(1, "Buzo verde",
                "Buzo verde de manga larga, comodo para un estilo casual en dias frescos.",
                69900, 10, "buzo-verde.png"));
        productos.add(new Producto(2, "Camiseta azul oscuro",
                "Camiseta de manga corta y cuello redondo, facil de combinar.",
                29900, 12, "camiseta-azul-oscuro.png"));
        productos.add(new Producto(3, "Camiseta roja",
                "Camiseta roja de manga corta que aporta color a tu estilo diario.",
                29900, 12, "camiseta-roja.png"));
        productos.add(new Producto(4, "Camiseta negra",
                "Camiseta negra de corte clasico, versatil para distintos looks.",
                29900, 12, "camiseta-negra.png"));
        productos.add(new Producto(5, "Camiseta blanca",
                "Camiseta blanca de corte clasico, una prenda esencial para cada dia.",
                29900, 12, "camiseta-blanca.png"));
        productos.add(new Producto(6, "Jean holgado azul",
                "Jean azul de corte holgado y estilo relajado para mayor comodidad.",
                79900, 8, "jean-holgado-azul.png"));
        productos.add(new Producto(7, "Jean holgado negro",
                "Jean de tono oscuro y corte holgado, facil de combinar con camisetas.",
                79900, 8, "jean-holgado-negro.png"));
        productos.add(new Producto(8, "Gorra de beisbol",
                "Gorra negra con visera plana y bordado decorativo para un look urbano.",
                49900, 10, "gorra-beisbol.png"));
    }

    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    public int siguienteId() {
        return productos.stream().mapToInt(Producto::getId).max().orElse(0) + 1;
    }

    public boolean actualizarStock(int id, int nuevoStock) {
        if (nuevoStock < 0) {
            return false;
        }
        for (Producto producto : productos) {
            if (producto.getId() == id) {
                producto.setStock(nuevoStock);
                return true;
            }
        }
        return false;
    }

    public List<Producto> getProductos() {
        return new ArrayList<>(productos);
    }

    public int total() {
        return productos.size();
    }
}
