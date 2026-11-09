package com.proyecto.vista;

import com.proyecto.controlador.ContextoApp;
import com.proyecto.modelo.Producto;
import com.proyecto.modelo.Usuario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
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

        Label marca = new Label("NEXUS");
        marca.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label sesion = Estilos.subtitulo(usuario.getNombreUsuario() + " | " + usuario.getRol());
        Region espacioSuperior = new Region();
        menu.getChildren().addAll(marca, sesion, espacioSuperior);

        agregarOpcion(admin ? "Gestionar catalogo" : "Catalogo",
                () -> new CatalogoView(contexto.getCatalogo(), contexto.getCarrito(), contexto.getFavoritos(), admin)
                        .getContenido());
        if (admin) {
            agregarOpcion("Agregar producto",
                    () -> new AgregarProductoView(contexto.getCatalogo()).getContenido());
        } else {
            agregarOpcion("Carrito", () -> panelProductos(contexto, false));
            agregarOpcion("Favoritos", () -> panelProductos(contexto, true));
        }
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

    private Node panelProductos(ContextoApp contexto, boolean mostrarFavoritos) {
        String nombre = mostrarFavoritos ? "Favoritos" : "Carrito";
        VBox panel = new VBox(12, Estilos.titulo(nombre));
        panel.setPadding(new Insets(20));
        panel.setStyle(Estilos.FONDO_PANTALLA);

        List<Producto> productos = mostrarFavoritos
                ? contexto.getCatalogo().getProductos().stream()
                    .filter(p -> contexto.getFavoritos().contains(p.getId())).toList()
                : new ArrayList<>(contexto.getCarrito());

        if (productos.isEmpty()) {
            panel.getChildren().add(Estilos.subtitulo(mostrarFavoritos
                    ? "Todavia no tienes productos favoritos." : "El carrito esta vacio."));
            return panel;
        }

        GridPane tarjetas = new GridPane();
        tarjetas.setHgap(16);
        tarjetas.setVgap(16);
        tarjetas.setAlignment(Pos.TOP_LEFT);
        int indice = 0;
        for (Producto producto : productos) {
            tarjetas.add(crearTarjetaProducto(contexto, producto, mostrarFavoritos), indice % 4, indice / 4);
            indice++;
        }

        ScrollPane desplazamiento = new ScrollPane(tarjetas);
        desplazamiento.setFitToWidth(true);
        desplazamiento.setStyle("-fx-background: black; -fx-background-color: black;");
        panel.getChildren().add(desplazamiento);
        VBox.setVgrow(desplazamiento, Priority.ALWAYS);
        return panel;
    }

    private VBox crearTarjetaProducto(ContextoApp contexto, Producto producto, boolean mostrarFavoritos) {
        Label nombre = new Label(producto.getNombre());
        nombre.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label precio = new Label(String.format("$%,.0f COP", producto.getPrecio()));
        precio.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
        Label descripcion = new Label(producto.getDescripcion());
        descripcion.setWrapText(true);
        descripcion.setMinHeight(42);
        descripcion.setStyle("-fx-text-fill: #cccccc;");

        Button quitar = Estilos.botonPrimario(mostrarFavoritos
                ? "Quitar de favoritos" : "Quitar del carrito");
        quitar.setMaxWidth(Double.MAX_VALUE);
        quitar.setOnAction(e -> {
            if (mostrarFavoritos) {
                contexto.getFavoritos().remove(producto.getId());
            } else {
                contexto.getCarrito().remove(producto);
            }
            mostrar(panelProductos(contexto, mostrarFavoritos));
        });

        VBox tarjeta = new VBox(10, crearImagenProducto(producto), nombre, precio, descripcion, quitar);
        tarjeta.setPadding(new Insets(12));
        tarjeta.setPrefWidth(265);
        tarjeta.setStyle("-fx-background-color: #171717; -fx-background-radius: 8; "
                + "-fx-border-color: #333333; -fx-border-radius: 8;");
        return tarjeta;
    }

    private Node crearImagenProducto(Producto producto) {
        if (producto.getImagenArchivo() == null || producto.getImagenArchivo().isBlank()) {
            Label sinImagen = new Label("Imagen no disponible");
            sinImagen.setStyle("-fx-text-fill: #444444;");
            return sinImagen;
        }
        var recurso = getClass().getResource("/imagenes/" + producto.getImagenArchivo());
        if (recurso == null) {
            Label sinImagen = new Label("Imagen no disponible");
            sinImagen.setStyle("-fx-text-fill: #cccccc;");
            return sinImagen;
        }
        ImageView imagen = new ImageView(new Image(recurso.toExternalForm()));
        imagen.setFitWidth(235);
        imagen.setFitHeight(175);
        imagen.setPreserveRatio(true);
        StackPane marco = new StackPane(imagen);
        marco.setPrefSize(235, 175);
        marco.setStyle("-fx-background-color: #f4f4f4; -fx-background-radius: 5;");
        return marco;
    }
}
