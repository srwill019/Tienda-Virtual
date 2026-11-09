package com.proyecto.modelo;

public class Usuario {
    private final String nombreUsuario;
    private final String contrasena;
    private final Rol rol;

    public Usuario(String nombreUsuario, String contrasena, Rol rol) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /** Se usa para guardar clientes en el archivo local de usuarios. */
    public String getContrasena() {
        return contrasena;
    }

    public Rol getRol() {
        return rol;
    }

    public boolean coincideCon(String nombre, String clave) {
        return nombreUsuario.equalsIgnoreCase(nombre) && contrasena.equals(clave);
    }

    public boolean esAdministrador() {
        return rol == Rol.ADMINISTRADOR;
    }
}
