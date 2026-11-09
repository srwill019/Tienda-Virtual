package com.proyecto.vista;

import com.proyecto.modelo.Rol;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Login separado en formularios de cliente y administrador. */
public class LoginView {
    private final VBox contenido = new VBox(14);

    private final TextField usuarioCliente = new TextField();
    private final PasswordField contrasenaCliente = new PasswordField();
    private final Label errorCliente = etiquetaError();
    private final Button ingresarCliente = Estilos.botonPrimario("Ingresar");
    private final VBox formularioCliente;

    private final TextField usuarioAdministrador = new TextField();
    private final PasswordField contrasenaAdministrador = new PasswordField();
    private final Label errorAdministrador = etiquetaError();
    private final Button ingresarAdministrador = Estilos.botonPrimario("Ingresar");
    private final VBox formularioAdministrador;

    private final Button pestanaCliente = Estilos.botonMenu("Cliente");
    private final Button pestanaAdministrador = Estilos.botonMenu("Administrador");
    private final Button botonRegistro = Estilos.botonEnlace("\u00bfNo tienes cuenta? Reg\u00edstrate");

    public LoginView() {
        prepararCampos(usuarioCliente, contrasenaCliente);
        prepararCampos(usuarioAdministrador, contrasenaAdministrador);

        ingresarCliente.setMaxWidth(Double.MAX_VALUE);
        ingresarAdministrador.setMaxWidth(Double.MAX_VALUE);

        formularioCliente = Estilos.tarjeta(
                Estilos.titulo("Acceso de clientes"),
                usuarioCliente,
                contrasenaCliente,
                ingresarCliente,
                errorCliente,
                botonRegistro,
                Estilos.subtitulo("Prueba: cliente / cliente123"));
        formularioAdministrador = Estilos.tarjeta(
                Estilos.titulo("Acceso de administradores"),
                usuarioAdministrador,
                contrasenaAdministrador,
                ingresarAdministrador,
                errorAdministrador,
                Estilos.subtitulo("Prueba: admin / admin123"));

        formularioCliente.setMaxWidth(370);
        formularioAdministrador.setMaxWidth(370);
        StackPane tarjetas = new StackPane(formularioCliente, formularioAdministrador);
        pestanaCliente.setMaxWidth(Double.MAX_VALUE);
        pestanaAdministrador.setMaxWidth(Double.MAX_VALUE);
        HBox pestañas = new HBox(10, pestanaCliente, pestanaAdministrador);
        HBox.setHgrow(pestanaCliente, Priority.ALWAYS);
        HBox.setHgrow(pestanaAdministrador, Priority.ALWAYS);
        VBox.setMargin(pestañas, new Insets(0, 0, 0, 0));

        pestanaCliente.setOnAction(e -> mostrarApartado(Rol.CLIENTE));
        pestanaAdministrador.setOnAction(e -> mostrarApartado(Rol.ADMINISTRADOR));
        mostrarApartado(Rol.CLIENTE);

        contenido.setAlignment(Pos.CENTER);
        contenido.setStyle(Estilos.FONDO_PANTALLA);
        contenido.getChildren().addAll(pestañas, tarjetas);
        contenido.setPrefWidth(480);
    }

    private void prepararCampos(TextField usuario, PasswordField contrasena) {
        usuario.setPromptText("Usuario");
        contrasena.setPromptText("Contrasena");
        Estilos.aplicarCampo(usuario);
        Estilos.aplicarCampo(contrasena);
    }

    private Label etiquetaError() {
        Label etiqueta = new Label();
        etiqueta.setStyle("-fx-text-fill: " + Estilos.ROJO_ERROR + ";");
        etiqueta.setWrapText(true);
        return etiqueta;
    }

    private void mostrarApartado(Rol rol) {
        boolean clienteActivo = rol == Rol.CLIENTE;
        formularioCliente.setVisible(clienteActivo);
        formularioCliente.setManaged(clienteActivo);
        formularioAdministrador.setVisible(!clienteActivo);
        formularioAdministrador.setManaged(!clienteActivo);

        pestanaCliente.setStyle(estiloPestana(clienteActivo));
        pestanaAdministrador.setStyle(estiloPestana(!clienteActivo));
        ingresarCliente.setDefaultButton(clienteActivo);
        ingresarAdministrador.setDefaultButton(!clienteActivo);

        usuarioCliente.clear();
        contrasenaCliente.clear();
        errorCliente.setText("");
        usuarioAdministrador.clear();
        contrasenaAdministrador.clear();
        errorAdministrador.setText("");
    }

    private String estiloPestana(boolean activa) {
        String color = activa ? Estilos.AZUL_ACENTO : Estilos.AZUL_MENU;
        return "-fx-background-color: " + color + "; -fx-text-fill: white; "
                + "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 6; "
                + "-fx-padding: 10 18 10 18; -fx-cursor: hand;";
    }

    public Parent getContenido() {
        return contenido;
    }

    public String getUsuarioIngresado(Rol rol) {
        return (rol == Rol.CLIENTE ? usuarioCliente : usuarioAdministrador).getText().trim();
    }

    public String getContrasenaIngresada(Rol rol) {
        return (rol == Rol.CLIENTE ? contrasenaCliente : contrasenaAdministrador).getText();
    }

    public void mostrarError(Rol rol, String mensaje) {
        (rol == Rol.CLIENTE ? errorCliente : errorAdministrador).setText(mensaje);
    }

    public void limpiarContrasena(Rol rol) {
        (rol == Rol.CLIENTE ? contrasenaCliente : contrasenaAdministrador).clear();
    }

    public void setAlPulsarIngresarCliente(Runnable accion) {
        ingresarCliente.setOnAction(e -> accion.run());
    }

    public void setAlPulsarIngresarAdministrador(Runnable accion) {
        ingresarAdministrador.setOnAction(e -> accion.run());
    }

    public void setAlPulsarRegistrarse(Runnable accion) {
        botonRegistro.setOnAction(e -> accion.run());
    }
}
