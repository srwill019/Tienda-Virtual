package com.proyecto.controlador;

import com.proyecto.estructuras.GestionCatalogo;
import com.proyecto.estructuras.GestionUsuarios;
import com.proyecto.modelo.Usuario;

/** Datos compartidos por las pantallas durante la ejecucion de la aplicacion. */
public class ContextoApp {
    private final GestionCatalogo catalogo = new GestionCatalogo();
    private final GestionUsuarios gestionUsuarios = new GestionUsuarios();
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
