package com.proyecto;

import com.proyecto.controlador.ContextoApp;
import com.proyecto.controlador.LoginController;
import com.proyecto.controlador.RegistroController;
import com.proyecto.vista.LoginView;
import com.proyecto.vista.PrincipalView;
import com.proyecto.vista.RegistroView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

/** Arranca la aplicacion y cambia entre login, registro y ventana principal. */
public class Main extends Application {
    private final ContextoApp contexto = new ContextoApp();
    private Stage ventana;
    private double anchoPantalla;
    private double altoPantalla;

    @Override
    public void start(Stage stage) {
        ventana = stage;
        ventana.setTitle("NEXUS");
        Rectangle2D limites = Screen.getPrimary().getVisualBounds();
        anchoPantalla = limites.getWidth();
        altoPantalla = limites.getHeight();
        mostrarLogin();
        ventana.show();
        maximizarVentana();
    }

    private void mostrarLogin() {
        contexto.cerrarSesion();
        LoginView vista = new LoginView();
        new LoginController(contexto, vista, this::mostrarPrincipal);
        vista.setAlPulsarRegistrarse(this::mostrarRegistro);
        cambiarPantalla(vista.getContenido());
    }

    private void mostrarRegistro() {
        RegistroView vista = new RegistroView();
        new RegistroController(contexto, vista, this::mostrarPrincipal, this::mostrarLogin);
        cambiarPantalla(vista.getContenido());
    }

    private void mostrarPrincipal() {
        PrincipalView vista = new PrincipalView(contexto, this::mostrarLogin);
        cambiarPantalla(vista.getContenido());
    }

    private void cambiarPantalla(Parent contenido) {
        ventana.setScene(new Scene(contenido, anchoPantalla, altoPantalla));
        maximizarVentana();
    }

    private void maximizarVentana() {
        ventana.setMaximized(true);
        Platform.runLater(() -> ventana.setMaximized(true));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
