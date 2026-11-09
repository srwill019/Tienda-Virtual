package com.proyecto.controlador;

import com.proyecto.modelo.Usuario;
import com.proyecto.vista.RegistroView;

/** Valida el formulario y registra cuentas con rol de cliente. */
public class RegistroController {
    private final ContextoApp contexto;
    private final RegistroView vista;
    private final Runnable alRegistrar;

    public RegistroController(ContextoApp contexto, RegistroView vista,
                              Runnable alRegistrar, Runnable alVolver) {
        this.contexto = contexto;
        this.vista = vista;
        this.alRegistrar = alRegistrar;
        vista.setAlPulsarCrear(this::intentarRegistrar);
        vista.setAlPulsarVolver(alVolver);
    }

    private void intentarRegistrar() {
        String nombre = vista.getUsuarioIngresado();
        String contrasena = vista.getContrasenaIngresada();
        String confirmacion = vista.getConfirmacionIngresada();

        if (nombre.isEmpty() || contrasena.isEmpty() || confirmacion.isEmpty()) {
            vista.mostrarError("Completa todos los campos.");
            return;
        }
        if (!contrasena.equals(confirmacion)) {
            vista.mostrarError("Las contrasenas no coinciden.");
            return;
        }

        try {
            Usuario nuevo = contexto.getGestionUsuarios().registrarCliente(nombre, contrasena);
            contexto.setUsuarioActual(nuevo);
            alRegistrar.run();
        } catch (IllegalArgumentException | IllegalStateException e) {
            vista.mostrarError(e.getMessage());
        }
    }
}
