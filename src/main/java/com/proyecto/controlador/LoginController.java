package com.proyecto.controlador;

import com.proyecto.modelo.Rol;
import com.proyecto.modelo.Usuario;
import com.proyecto.vista.LoginView;

/** Valida las credenciales y el rol del apartado de login seleccionado. */
public class LoginController {
    private final ContextoApp contexto;
    private final LoginView vista;
    private final Runnable alIngresar;

    public LoginController(ContextoApp contexto, LoginView vista, Runnable alIngresar) {
        this.contexto = contexto;
        this.vista = vista;
        this.alIngresar = alIngresar;
        vista.setAlPulsarIngresarCliente(() -> intentarIngresar(Rol.CLIENTE));
        vista.setAlPulsarIngresarAdministrador(() -> intentarIngresar(Rol.ADMINISTRADOR));
    }

    private void intentarIngresar(Rol apartado) {
        String nombre = vista.getUsuarioIngresado(apartado);
        String contrasena = vista.getContrasenaIngresada(apartado);
        if (nombre.isEmpty() || contrasena.isEmpty()) {
            vista.mostrarError(apartado, "Escribe el usuario y la contrasena.");
            return;
        }

        Usuario usuario = contexto.getGestionUsuarios().autenticar(nombre, contrasena);
        if (usuario == null) {
            vista.mostrarError(apartado, "Usuario o contrasena incorrectos.");
            vista.limpiarContrasena(apartado);
            return;
        }
        if (usuario.getRol() != apartado) {
            vista.mostrarError(apartado, "Este usuario no tiene acceso a este apartado.");
            return;
        }

        contexto.setUsuarioActual(usuario);
        alIngresar.run();
    }
}
