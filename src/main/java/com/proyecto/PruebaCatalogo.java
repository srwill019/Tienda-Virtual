package com.proyecto;

import com.proyecto.estructuras.GestionCatalogo;
import com.proyecto.modelo.Producto;

public class PruebaCatalogo {
    public static void main(String[] args) {
        GestionCatalogo catalogo = new GestionCatalogo();

        catalogo.agregarProducto(new Producto(1, "Cuaderno", 8500, 20));
        catalogo.agregarProducto(new Producto(2, "Lápiz", 1500, 50));
        catalogo.agregarProducto(new Producto(3, "Mochila", 65000, 10));
        catalogo.agregarProducto(new Producto(4, "Regla", 3000, 25));
        catalogo.agregarProducto(new Producto(5, "Borrador", 1000, 40));
        catalogo.agregarProducto(new Producto(6, "Colores", 12000, 15));
        catalogo.agregarProducto(new Producto(7, "Marcador", 4500, 18));
        catalogo.agregarProducto(new Producto(8, "Tijeras", 7000, 12));

        for (Producto producto : catalogo.getProductos()) {
            System.out.println("--------------------");
            System.out.println(producto);
        }
        System.out.println("--------------------");
    }
}
