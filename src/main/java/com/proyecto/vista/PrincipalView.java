package com.proyecto.vista;

import com.proyecto.controlador.ContextoApp;
import com.proyecto.modelo.Usuario;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** Ventana principal con menu lateral y panel central. */
public class PrincipalView {
    private final BorderPane raiz = new BorderPane();
    private final VBox menu = new VBox(6);
    private final List<Button> botonesMenu = new ArrayList<>();

    public PrincipalView(ContextoApp contexto, Runnable alCerrarSesion) {
        Usuario usuario = contexto.getUsuarioActual();
        boolean admin = usuario.esAdministrador();

        Label marca = new Label("Tienda Virtual");
        marca.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label sesion = Estilos.subtitulo(usuario.getNombreUsuario() + " | " + usuario.getRol());
        Region espacioSuperior = new Region();
        menu.getChildren().addAll(marca, sesion, espacioSuperior);

        agregarOpcion("Inicio", () -> new InicioView(contexto).getContenido());
        agregarOpcion(admin ? "Gestionar catalogo" : "Catalogo",
                () -> panelPendiente("Catalogo"));
        agregarOpcion(admin ? "Pedidos pendientes" : "Hacer pedido",
                () -> panelPendiente("Pedidos"));
        agregarOpcion("Historial", () -> panelPendiente("Historial"));

        Region espacio = new Region();
        VBox.setVgrow(espacio, Priority.ALWAYS);
        Button cerrar = Estilos.botonMenu("Cerrar sesion");
        cerrar.setOnAction(e -> alCerrarSesion.run());
        menu.getChildren().addAll(espacio, cerrar);

        menu.setPadding(new Insets(20, 12, 20, 12));
        menu.setPrefWidth(220);
        menu.setStyle("-fx-background-color: " + Estilos.AZUL_MENU + ";");
        raiz.setLeft(menu);
        raiz.setStyle(Estilos.FONDO_PANTALLA);

        botonesMenu.get(0).fire();
    }

    public Parent getContenido() {
        return raiz;
    }

    private void agregarOpcion(String texto, Supplier<Node> creadorPantalla) {
        Button boton = Estilos.botonMenu(texto);
        boton.setOnAction(e -> {
            marcarActivo(boton);
            mostrar(creadorPantalla.get());
        });
        botonesMenu.add(boton);
        menu.getChildren().add(boton);
    }

    private void marcarActivo(Button activo) {
        for (Button boton : botonesMenu) {
            Estilos.marcarBotonMenu(boton, boton == activo);
        }
    }

    private void mostrar(Node pantalla) {
        BorderPane.setMargin(pantalla, new Insets(25));
        raiz.setCenter(pantalla);
    }

    private Node panelPendiente(String nombre) {
        VBox panel = new VBox(10,
                Estilos.titulo(nombre),
                Estilos.subtitulo("Esta seccion se construira en los siguientes bloques."));
        panel.setStyle(Estilos.FONDO_PANTALLA);
        return panel;
    }
}
