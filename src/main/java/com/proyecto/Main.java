package com.proyecto;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage ventana) {
        Label mensaje = new Label("JavaFX funciona. ¡Bienvenido a Tienda Virtual!");
        StackPane contenido = new StackPane(mensaje);

        Scene escena = new Scene(contenido, 600, 350);
        ventana.setTitle("Tienda Virtual");
        ventana.setScene(escena);
        ventana.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
