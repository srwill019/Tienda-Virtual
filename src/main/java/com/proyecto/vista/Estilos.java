package com.proyecto.vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.VBox;

/** Colores y estilos compartidos por las pantallas. */
public final class Estilos {
    public static final String AZUL_FONDO = "#000000";
    public static final String AZUL_MENU = "#111111";
    public static final String AZUL_PANEL = "#171717";
    public static final String AZUL_ACENTO = "#1976D2";
    public static final String AZUL_ACENTO_CLARO = "#42A5F5";
    public static final String AZUL_CLARO = "#90CAF9";
    public static final String ROJO_ERROR = "#EF9A9A";
    public static final String FONDO_PANTALLA = "-fx-background-color: " + AZUL_FONDO + ";";

    private Estilos() { }

    public static Label titulo(String texto) {
        Label etiqueta = new Label(texto);
        etiqueta.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        return etiqueta;
    }

    public static Label subtitulo(String texto) {
        Label etiqueta = new Label(texto);
        etiqueta.setStyle("-fx-font-size: 14px; -fx-text-fill: " + AZUL_CLARO + ";");
        etiqueta.setWrapText(true);
        return etiqueta;
    }

    public static Button botonPrimario(String texto) {
        Button boton = new Button(texto);
        String normal = estiloBoton(AZUL_ACENTO);
        String encima = estiloBoton(AZUL_ACENTO_CLARO);
        boton.setStyle(normal);
        boton.setOnMouseEntered(e -> boton.setStyle(encima));
        boton.setOnMouseExited(e -> boton.setStyle(normal));
        return boton;
    }

    public static Button botonMenu(String texto) {
        Button boton = new Button(texto);
        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setAlignment(Pos.CENTER_LEFT);
        marcarBotonMenu(boton, false);
        return boton;
    }

    public static void marcarBotonMenu(Button boton, boolean activo) {
        String fondo = activo ? AZUL_ACENTO : "transparent";
        boton.setStyle("-fx-background-color: " + fondo + "; -fx-text-fill: white; "
                + "-fx-font-size: 14px; -fx-padding: 10 14 10 14; "
                + "-fx-background-radius: 6; -fx-cursor: hand;");
    }

    public static Button botonEnlace(String texto) {
        Button boton = new Button(texto);
        boton.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AZUL_CLARO + "; "
                + "-fx-underline: true; -fx-cursor: hand;");
        return boton;
    }

    public static VBox tarjeta(Node... hijos) {
        VBox caja = new VBox(12, hijos);
        caja.setPadding(new Insets(20));
        caja.setStyle("-fx-background-color: " + AZUL_PANEL + "; -fx-background-radius: 10;");
        return caja;
    }

    public static void aplicarCampo(TextInputControl campo) {
        campo.setStyle("-fx-control-inner-background: " + AZUL_MENU + "; -fx-text-fill: white; "
                + "-fx-prompt-text-fill: #90A4AE; -fx-border-color: " + AZUL_ACENTO + "; "
                + "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8;");
    }

    private static String estiloBoton(String color) {
        return "-fx-background-color: " + color + "; -fx-text-fill: white; "
                + "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 6; "
                + "-fx-padding: 10 24 10 24; -fx-cursor: hand;";
    }
}
