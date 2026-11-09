package com.proyecto.vista;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/** Formulario para crear una cuenta de cliente. */
public class RegistroView {
    private final VBox contenido = new VBox();
    private final TextField campoUsuario = new TextField();
    private final PasswordField campoContrasena = new PasswordField();
    private final PasswordField campoConfirmacion = new PasswordField();
    private final Button botonCrear = Estilos.botonPrimario("Crear cuenta");
    private final Button botonVolver = Estilos.botonEnlace("Volver al inicio de sesion");
    private final Label mensajeError = new Label();

    public RegistroView() {
        campoUsuario.setPromptText("Nuevo usuario (minimo 3 caracteres)");
        campoContrasena.setPromptText("Contrasena (minimo 4 caracteres)");
        campoConfirmacion.setPromptText("Repite la contrasena");
        Estilos.aplicarCampo(campoUsuario);
        Estilos.aplicarCampo(campoContrasena);
        Estilos.aplicarCampo(campoConfirmacion);

        mensajeError.setStyle("-fx-text-fill: " + Estilos.ROJO_ERROR + ";");
        mensajeError.setWrapText(true);
        botonCrear.setMaxWidth(Double.MAX_VALUE);
        botonCrear.setDefaultButton(true);

        VBox tarjeta = Estilos.tarjeta(
                Estilos.titulo("Crear cuenta"),
                Estilos.subtitulo("Registrate como cliente para hacer pedidos"),
                campoUsuario, campoContrasena, campoConfirmacion,
                mensajeError, botonCrear, botonVolver);
        tarjeta.setMaxWidth(360);
        contenido.setAlignment(Pos.CENTER);
        contenido.setStyle(Estilos.FONDO_PANTALLA);
        contenido.getChildren().add(tarjeta);
    }

    public Parent getContenido() {
        return contenido;
    }

    public String getUsuarioIngresado() {
        return campoUsuario.getText().trim();
    }

    public String getContrasenaIngresada() {
        return campoContrasena.getText();
    }

    public String getConfirmacionIngresada() {
        return campoConfirmacion.getText();
    }

    public void mostrarError(String mensaje) {
        mensajeError.setText(mensaje);
    }

    public void setAlPulsarCrear(Runnable accion) {
        botonCrear.setOnAction(e -> accion.run());
    }

    public void setAlPulsarVolver(Runnable accion) {
        botonVolver.setOnAction(e -> accion.run());
    }
}
