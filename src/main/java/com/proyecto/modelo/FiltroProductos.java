package com.proyecto.modelo;

import com.proyecto.estructuras.ListaSimpleEnlazada;

import java.util.List;

/** Criterios opcionales para reducir una lista de productos. */
public class FiltroProductos {
    public enum Disponibilidad { TODOS, DISPONIBLES, AGOTADOS }

    private final Disponibilidad disponibilidad;
    private final Double precioMinimo;
    private final Double precioMaximo;
    private final boolean soloFavoritos;

    public FiltroProductos(Disponibilidad disponibilidad, Double precioMinimo, Double precioMaximo,
                           boolean soloFavoritos) {
        this.disponibilidad = disponibilidad == null ? Disponibilidad.TODOS : disponibilidad;
        this.precioMinimo = precioMinimo;
        this.precioMaximo = precioMaximo;
        this.soloFavoritos = soloFavoritos;
    }

    public List<Producto> aplicar(List<Producto> productos, ListaSimpleEnlazada<Integer> favoritos) {
        return productos.stream().filter(producto -> {
            boolean disponibilidadValida = switch (disponibilidad) {
                case TODOS -> true;
                case DISPONIBLES -> producto.getStock() > 0;
                case AGOTADOS -> producto.getStock() == 0;
            };
            boolean precioValido = (precioMinimo == null || producto.getPrecio() >= precioMinimo)
                    && (precioMaximo == null || producto.getPrecio() <= precioMaximo);
            boolean favoritoValido = !soloFavoritos
                    || (favoritos != null && favoritos.contiene(producto.getId()));
            return disponibilidadValida && precioValido && favoritoValido;
        }).toList();
    }
}
