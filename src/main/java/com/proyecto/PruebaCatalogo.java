package com.proyecto;

import com.proyecto.estructuras.GestionCatalogo;
import com.proyecto.modelo.Producto;

public class PruebaCatalogo {
    public static void main(String[] args) {
        GestionCatalogo catalogo = new GestionCatalogo();
        catalogo.cargarDatosPrueba();

        for (Producto producto : catalogo.getProductos()) {
            System.out.println("--------------------");
            System.out.println(producto);
        }
        System.out.println("--------------------");
    }
}
