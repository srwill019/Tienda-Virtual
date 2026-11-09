package com.proyecto.vista;

import com.proyecto.estructuras.GestionCatalogo;
import com.proyecto.modelo.Producto;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

/** Muestra las prendas y permite guardarlas en el carrito o en favoritos. */
public class CatalogoView {
    private final GestionCatalogo gestionCatalogo;
    private final boolean modoAdministrador;
    private final ArrayList<Producto> carrito;
    private final Set<Integer> favoritos;
    private final Label resumen = Estilos.subtitulo("Carrito: 0 | Favoritos: 0");
    private final VBox contenido = new VBox(14);
    private final GridPane productos = new GridPane();

    public CatalogoView(GestionCatalogo gestionCatalogo, ArrayList<Producto> carrito,
                        Set<Integer> favoritos, boolean modoAdministrador) {
        this.gestionCatalogo = gestionCatalogo;
        this.modoAdministrador = modoAdministrador;
        this.carrito = carrito;
        this.favoritos = favoritos;
        Label titulo = Estilos.titulo(modoAdministrador ? "Gestionar catalogo" : "Catalogo de ropa para hombre");
        Label descripcion = Estilos.subtitulo(modoAdministrador
                ? "Actualiza el stock o agrega productos al catalogo."
                : "Explora las prendas y agregalas al carrito o a favoritos.");
        HBox encabezado = new HBox(20, titulo);
        encabezado.setAlignment(Pos.CENTER);
        if (!modoAdministrador) {
            encabezado.getChildren().add(resumen);
        }
        descripcion.setTextAlignment(TextAlignment.LEFT);
        descripcion.setMaxWidth(Double.MAX_VALUE);
        productos.setHgap(16);
        productos.setVgap(16);
        productos.setAlignment(Pos.TOP_CENTER);

        ScrollPane desplazamiento = new ScrollPane(productos);
        desplazamiento.setFitToWidth(true);
        desplazamiento.setFitToHeight(true);
        desplazamiento.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        desplazamiento.setStyle("-fx-background: black; -fx-background-color: black;");
        contenido.setPadding(new Insets(24));
        contenido.setStyle(Estilos.FONDO_PANTALLA);
        contenido.getChildren().addAll(encabezado, descripcion);
        contenido.getChildren().add(desplazamiento);
        VBox.setVgrow(desplazamiento, Priority.ALWAYS);
        actualizarProductos();
        actualizarResumen();
    }

    public Node getContenido() {
        return contenido;
    }

    private VBox crearTarjeta(Producto producto) {
        Label nombre = new Label(producto.getNombre());
        nombre.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label precio = new Label(String.format("$%,.0f COP", producto.getPrecio()));
        precio.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
        Label detalle = new Label(producto.getDescripcion());
        detalle.setWrapText(true);
        detalle.setMinHeight(42);
        detalle.setStyle("-fx-text-fill: #cccccc;");

        Label estado = new Label();
        estado.setWrapText(true);
        estado.setStyle("-fx-text-fill: " + Estilos.AZUL_CLARO + ";");

        VBox tarjeta;
        if (modoAdministrador) {
            Label stockActual = Estilos.subtitulo("Stock actual: " + producto.getStock());
            TextField nuevoStock = new TextField(String.valueOf(producto.getStock()));
            nuevoStock.setPromptText("Nuevo stock");
            Estilos.aplicarCampo(nuevoStock);
            Button guardarStock = Estilos.botonPrimario("Actualizar stock");
            guardarStock.setMaxWidth(Double.MAX_VALUE);
            guardarStock.setOnAction(e -> {
                try {
                    int cantidad = Integer.parseInt(nuevoStock.getText().trim());
                    if (!gestionCatalogo.actualizarStock(producto.getId(), cantidad)) {
                        estado.setText("El stock debe ser un numero igual o mayor que cero.");
                    } else {
                        stockActual.setText("Stock actual: " + cantidad);
                        estado.setText("Stock actualizado.");
                    }
                } catch (NumberFormatException ex) {
                    estado.setText("Escribe un numero entero para el stock.");
                }
            });
            tarjeta = new VBox(10, crearImagen(producto), nombre, precio, detalle,
                    stockActual, nuevoStock, guardarStock, estado);
        } else {
            Button agregar = Estilos.botonPrimario("Agregar al carrito");
            agregar.setMaxWidth(Double.MAX_VALUE);
            agregar.setOnAction(e -> {
                carrito.add(producto);
                actualizarResumen();
                estado.setText("Agregado al carrito.");
            });

            Button favorito = Estilos.botonEnlace("Agregar a favoritos");
            favorito.setText(favoritos.contains(producto.getId()) ? "Quitar de favoritos" : "Agregar a favoritos");
            favorito.setMaxWidth(Double.MAX_VALUE);
            favorito.setOnAction(e -> {
                if (favoritos.add(producto.getId())) {
                    favorito.setText("Quitar de favoritos");
                    estado.setText("Agregado a favoritos.");
                } else {
                    favoritos.remove(producto.getId());
                    favorito.setText("Agregar a favoritos");
                    estado.setText("Quitado de favoritos.");
                }
                actualizarResumen();
            });
            tarjeta = new VBox(10, crearImagen(producto), nombre, precio, detalle, agregar, favorito, estado);
        }
        tarjeta.setPadding(new Insets(12));
        tarjeta.setPrefWidth(265);
        tarjeta.setStyle("-fx-background-color: #171717; -fx-background-radius: 8; "
                + "-fx-border-color: #333333; -fx-border-radius: 8;");
        return tarjeta;
    }

    private Node crearImagen(Producto producto) {
        if (producto.getImagenArchivo() == null || producto.getImagenArchivo().isBlank()) {
            Label sinImagen = new Label("Imagen no disponible");
            sinImagen.setStyle("-fx-text-fill: #444444;");
            return sinImagen;
        }
        URL recurso = getClass().getResource("/imagenes/" + producto.getImagenArchivo());
        if (recurso == null) {
            Label sinImagen = new Label("Imagen no disponible");
            sinImagen.setStyle("-fx-text-fill: #444444;");
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

    private void actualizarResumen() {
        resumen.setText("Carrito: " + carrito.size() + " | Favoritos: " + favoritos.size());
    }

    private void actualizarProductos() {
        productos.getChildren().clear();
        int indice = 0;
        for (Producto producto : gestionCatalogo.getProductos()) {
            productos.add(crearTarjeta(producto), indice % 4, indice / 4);
            indice++;
        }
    }
}
