package com.proyecto.controlador;

import com.proyecto.estructuras.GestionCatalogo;
import com.proyecto.estructuras.GestionUsuarios;
import com.proyecto.estructuras.ListaSimpleEnlazada;
import com.proyecto.controlador.GestionPedidos;
import com.proyecto.modelo.Usuario;
import com.proyecto.modelo.Producto;


/** Datos compartidos por las pantallas durante la ejecucion de la aplicacion. */
public class ContextoApp {
    private final GestionCatalogo catalogo = new GestionCatalogo();
    private final GestionUsuarios gestionUsuarios = new GestionUsuarios();
    private final GestionPedidos gestionPedidos = new GestionPedidos();
    private final ListaSimpleEnlazada<Producto> carrito = new ListaSimpleEnlazada<>();
    private final ListaSimpleEnlazada<Integer> favoritos = new ListaSimpleEnlazada<>();
    private Usuario usuarioActual;

    public ContextoApp() {
        catalogo.cargarDatosPrueba();
        gestionUsuarios.cargarUsuarios();
    }

    public GestionCatalogo getCatalogo() {
        return catalogo;
    }

    public GestionUsuarios getGestionUsuarios() {
        return gestionUsuarios;
    }

    public GestionPedidos getGestionPedidos() {
        return gestionPedidos;
    }

    public ListaSimpleEnlazada<Producto> getCarrito() {
        return carrito;
    }

    public ListaSimpleEnlazada<Integer> getFavoritos() {
        return favoritos;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    public void cerrarSesion() {
        usuarioActual = null;
    }
}
