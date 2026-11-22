package com.proyecto.vista;

import com.proyecto.estructuras.GestionCatalogo;
import com.proyecto.estructuras.ListaSimpleEnlazada;
import com.proyecto.modelo.Producto;
import com.proyecto.modelo.FiltroProductos;
import com.proyecto.modelo.FiltroProductos.Disponibilidad;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
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

/** Muestra las prendas y permite guardarlas en el carrito o en favoritos. */
public class CatalogoView {
    private final GestionCatalogo gestionCatalogo;
    private final boolean modoAdministrador;
    private final ListaSimpleEnlazada<Producto> carrito;
    private final ListaSimpleEnlazada<Integer> favoritos;
    private final Label resumen = Estilos.subtitulo("Carrito: 0 | Favoritos: 0");
    private final VBox contenido = new VBox(14);
    private final GridPane productos = new GridPane();
    private final TextField busqueda = new TextField();
    private final ComboBox<Disponibilidad> disponibilidad = new ComboBox<>(
            FXCollections.observableArrayList(Disponibilidad.values()));
    private final TextField precioMinimo = new TextField();
    private final TextField precioMaximo = new TextField();
    private final CheckBox soloFavoritos = new CheckBox("Solo favoritos");
    private final Label cantidadResultados = Estilos.subtitulo("");
    private final Label mensaje = Estilos.subtitulo("");

    public CatalogoView(GestionCatalogo gestionCatalogo, ListaSimpleEnlazada<Producto> carrito,
                        ListaSimpleEnlazada<Integer> favoritos, boolean modoAdministrador) {
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

        busqueda.setPromptText("Buscar por nombre o id");
        Estilos.aplicarCampo(busqueda);
        HBox panelBusqueda = new HBox(10, Estilos.subtitulo("Buscar"), busqueda);
        panelBusqueda.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(busqueda, Priority.ALWAYS);

        disponibilidad.setValue(Disponibilidad.TODOS);
        disponibilidad.setPromptText("Disponibilidad");
        disponibilidad.setStyle("-fx-control-inner-background: " + Estilos.AZUL_MENU + "; -fx-text-fill: white;");
        precioMinimo.setPromptText("Precio minimo");
        precioMaximo.setPromptText("Precio maximo");
        Estilos.aplicarCampo(precioMinimo);
        Estilos.aplicarCampo(precioMaximo);
        Button limpiarFiltros = Estilos.botonPrimario("Limpiar filtros");
        limpiarFiltros.setOnAction(e -> {
            disponibilidad.setValue(Disponibilidad.TODOS);
            precioMinimo.clear();
            precioMaximo.clear();
            soloFavoritos.setSelected(false);
            mensaje.setText("");
            refrescar();
        });
        HBox panelFiltros = new HBox(10, Estilos.subtitulo("Filtros"), disponibilidad,
                precioMinimo, precioMaximo);
        if (!modoAdministrador) {
            soloFavoritos.setStyle("-fx-text-fill: white;");
            panelFiltros.getChildren().add(soloFavoritos);
        }
        panelFiltros.getChildren().add(limpiarFiltros);
        panelFiltros.setAlignment(Pos.CENTER_LEFT);
        disponibilidad.valueProperty().addListener((observable, anterior, nuevo) -> refrescarDesdeControl());
        precioMinimo.textProperty().addListener((observable, anterior, nuevo) -> refrescarDesdeControl());
        precioMaximo.textProperty().addListener((observable, anterior, nuevo) -> refrescarDesdeControl());
        soloFavoritos.selectedProperty().addListener((observable, anterior, nuevo) -> refrescarDesdeControl());
        busqueda.textProperty().addListener((observable, anterior, nuevo) -> refrescarDesdeControl());

        ScrollPane desplazamiento = new ScrollPane(productos);
        desplazamiento.setFitToWidth(true);
        desplazamiento.setFitToHeight(true);
        desplazamiento.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        desplazamiento.setStyle("-fx-background: black; -fx-background-color: black;");
        contenido.setPadding(new Insets(24));
        contenido.setStyle(Estilos.FONDO_PANTALLA);
        contenido.getChildren().addAll(encabezado, descripcion, panelBusqueda, panelFiltros,
                cantidadResultados, mensaje);
        contenido.getChildren().add(desplazamiento);
        VBox.setVgrow(desplazamiento, Priority.ALWAYS);
        refrescar();
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
                        mensaje.setText("Stock actualizado.");
                        refrescar();
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
                carrito.agregar(producto);
                actualizarResumen();
                estado.setText("Agregado al carrito.");
            });

            Button favorito = Estilos.botonEnlace("Agregar a favoritos");
            favorito.setText(favoritos.contiene(producto.getId()) ? "Quitar de favoritos" : "Agregar a favoritos");
            favorito.setMaxWidth(Double.MAX_VALUE);
            favorito.setOnAction(e -> {
                if (favoritos.agregarSiNoExiste(producto.getId())) {
                    mensaje.setText("Agregado a favoritos.");
                } else {
                    favoritos.eliminar(producto.getId());
                    mensaje.setText("Quitado de favoritos.");
                }
                actualizarResumen();
                refrescar();
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
        resumen.setText("Carrito: " + carrito.tamano() + " | Favoritos: " + favoritos.tamano());
    }

    private void refrescarDesdeControl() {
        mensaje.setText("");
        refrescar();
    }

    private void refrescar() {
        productos.getChildren().clear();
        java.util.List<Producto> resultadoBusqueda = gestionCatalogo.buscar(busqueda.getText());
        Double minimo;
        Double maximo;
        try {
            minimo = leerPrecio(precioMinimo.getText());
            maximo = leerPrecio(precioMaximo.getText());
            if (minimo != null && maximo != null && minimo > maximo) {
                throw new IllegalArgumentException("El precio minimo no puede superar el precio maximo.");
            }
        } catch (IllegalArgumentException ex) {
            cantidadResultados.setText("Mostrando " + resultadoBusqueda.size() + " de " + resultadoBusqueda.size()
                    + " productos");
            mensaje.setText(ex.getMessage());
            agregarMensajeSinResultados(resultadoBusqueda);
            return;
        }

        FiltroProductos filtro = new FiltroProductos(disponibilidad.getValue(), minimo, maximo,
                !modoAdministrador && soloFavoritos.isSelected());
        java.util.List<Producto> resultado = filtro.aplicar(resultadoBusqueda, favoritos);
        cantidadResultados.setText("Mostrando " + resultado.size() + " de " + resultadoBusqueda.size()
                + " productos");
        if (resultado.isEmpty()) {
            agregarMensajeSinResultados(resultado);
            return;
        }
        int indice = 0;
        for (Producto producto : resultado) {
            productos.add(crearTarjeta(producto), indice % 4, indice / 4);
            indice++;
        }
    }

    private Double leerPrecio(String texto) {
        String valor = texto == null ? "" : texto.trim();
        if (valor.isEmpty()) return null;
        try {
            double precio = Double.parseDouble(valor);
            if (!Double.isFinite(precio)) {
                throw new IllegalArgumentException("El precio debe ser un numero finito.");
            }
            return precio;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Escribe un precio numerico valido.");
        }
    }

    private void agregarMensajeSinResultados(java.util.List<Producto> resultado) {
        if (resultado.isEmpty()) {
            Label sinResultados = Estilos.subtitulo("No se encontraron productos con esos criterios.");
            productos.add(sinResultados, 0, 0);
        }
    }
}
