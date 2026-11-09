package com.proyecto.controlador;

import com.proyecto.estructuras.GestionCatalogo;
import com.proyecto.estructuras.GestionUsuarios;
import com.proyecto.modelo.Usuario;
import com.proyecto.modelo.Producto;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/** Datos compartidos por las pantallas durante la ejecucion de la aplicacion. */
public class ContextoApp {
    private final GestionCatalogo catalogo = new GestionCatalogo();
    private final GestionUsuarios gestionUsuarios = new GestionUsuarios();
    private final ArrayList<Producto> carrito = new ArrayList<>();
    private final Set<Integer> favoritos = new LinkedHashSet<>();
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

    public ArrayList<Producto> getCarrito() {
        return carrito;
    }

    public Set<Integer> getFavoritos() {
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
