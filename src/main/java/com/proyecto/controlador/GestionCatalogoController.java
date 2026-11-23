package com.proyecto.controlador;

import com.proyecto.estructuras.GestionCatalogo;
import com.proyecto.estructuras.ListaSimpleEnlazada;
import com.proyecto.modelo.Producto;
import com.proyecto.modelo.ItemCarrito;

/** Coordina la eliminacion del catalogo con el carrito, favoritos y pedidos. */
public class GestionCatalogoController {
    private final GestionCatalogo catalogo;
    private final GestionPedidos gestionPedidos;
    private final ListaSimpleEnlazada<ItemCarrito> carrito;
    private final ListaSimpleEnlazada<Integer> favoritos;

    public GestionCatalogoController(GestionCatalogo catalogo, GestionPedidos gestionPedidos,
                                     ListaSimpleEnlazada<ItemCarrito> carrito,
                                     ListaSimpleEnlazada<Integer> favoritos) {
        this.catalogo = catalogo;
        this.gestionPedidos = gestionPedidos;
        this.carrito = carrito;
        this.favoritos = favoritos;
    }

    /** Devuelve un mensaje si el producto esta protegido por un pedido o no existe. */
    public String eliminarProducto(Producto producto) {
        if (gestionPedidos.productoEnPedidos(producto)) {
            return "No se puede eliminar: el producto esta en pedidos pendientes o en el historial.";
        }
        for (ItemCarrito item : carrito.obtenerCopia()) {
            if (item.getProducto() == producto) carrito.eliminar(item);
        }
        favoritos.eliminar(producto.getId());
        if (!catalogo.eliminar(producto.getId())) {
            return "No se encontro el producto en el catalogo.";
        }
        return null;
    }
}
