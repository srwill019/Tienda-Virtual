package com.proyecto;

import com.proyecto.controlador.ContextoApp;
import com.proyecto.controlador.LoginController;
import com.proyecto.controlador.RegistroController;
import com.proyecto.vista.LoginView;
import com.proyecto.vista.PrincipalView;
import com.proyecto.vista.RegistroView;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Arranca la aplicacion y cambia entre login, registro y ventana principal. */
public class Main extends Application {
    private final ContextoApp contexto = new ContextoApp();
    private Stage ventana;

    @Override
    public void start(Stage stage) {
        ventana = stage;
        ventana.setTitle("Tienda Virtual");
        mostrarLogin();
        ventana.show();
    }

    private void mostrarLogin() {
        contexto.cerrarSesion();
        LoginView vista = new LoginView();
        new LoginController(contexto, vista, this::mostrarPrincipal);
        vista.setAlPulsarRegistrarse(this::mostrarRegistro);
        cambiarPantalla(vista.getContenido(), 480, 620);
    }

    private void mostrarRegistro() {
        RegistroView vista = new RegistroView();
        new RegistroController(contexto, vista, this::mostrarPrincipal, this::mostrarLogin);
        cambiarPantalla(vista.getContenido(), 480, 620);
    }

    private void mostrarPrincipal() {
        PrincipalView vista = new PrincipalView(contexto, this::mostrarLogin);
        cambiarPantalla(vista.getContenido(), 1000, 650);
    }

    private void cambiarPantalla(Parent contenido, double ancho, double alto) {
        ventana.setScene(new Scene(contenido, ancho, alto));
        ventana.sizeToScene();
        ventana.centerOnScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
