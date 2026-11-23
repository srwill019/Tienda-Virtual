package com.proyecto.vista;

import com.proyecto.estructuras.GestionCatalogo;
import com.proyecto.estructuras.ListaSimpleEnlazada;
import com.proyecto.controlador.GestionCatalogoController;
import com.proyecto.controlador.GestionPedidos;
import com.proyecto.modelo.Producto;
import com.proyecto.modelo.ItemCarrito;
import com.proyecto.modelo.FiltroProductos;
import com.proyecto.modelo.FiltroProductos.Disponibilidad;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
import javafx.scene.input.MouseButton;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Muestra las prendas y permite guardarlas en el carrito o en favoritos. */
public class CatalogoView {
    private final GestionCatalogo gestionCatalogo;
    private final boolean modoAdministrador;
    private final ListaSimpleEnlazada<ItemCarrito> carrito;
    private final ListaSimpleEnlazada<Integer> favoritos;
    private final GestionCatalogoController controladorCatalogo;
    private final Runnable guardarPreferencias;
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

    public CatalogoView(GestionCatalogo gestionCatalogo, GestionPedidos gestionPedidos,
                        ListaSimpleEnlazada<ItemCarrito> carrito, ListaSimpleEnlazada<Integer> favoritos,
                        boolean modoAdministrador, Runnable guardarPreferencias) {
        this.gestionCatalogo = gestionCatalogo;
        this.modoAdministrador = modoAdministrador;
        this.carrito = carrito;
        this.favoritos = favoritos;
        this.guardarPreferencias = guardarPreferencias == null ? () -> { } : guardarPreferencias;
        this.controladorCatalogo = new GestionCatalogoController(
                gestionCatalogo, gestionPedidos, carrito, favoritos);
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
        precioMinimo.focusedProperty().addListener((observable, anterior, enfocado) -> {
            if (!enfocado) formatearPrecio(precioMinimo);
        });
        precioMaximo.focusedProperty().addListener((observable, anterior, enfocado) -> {
            if (!enfocado) formatearPrecio(precioMaximo);
        });
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
            Button modificar = Estilos.botonPrimario("Modificar");
            modificar.setMaxWidth(Double.MAX_VALUE);
            modificar.setOnAction(e -> abrirFormularioModificar(producto));
            Button eliminar = Estilos.botonPrimario("Eliminar");
            eliminar.setMaxWidth(Double.MAX_VALUE);
            eliminar.setOnAction(e -> confirmarEliminacion(producto));
            tarjeta = new VBox(10, crearImagen(producto), nombre, precio, detalle,
                    stockActual, nuevoStock, guardarStock, modificar, eliminar, estado);
        } else {
            tarjeta = new VBox(10, crearImagen(producto), nombre, precio);
            tarjeta.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) abrirDetalleProducto(producto);
            });
            tarjeta.setOnMouseEntered(e -> tarjeta.setStyle("-fx-background-color: #202020; "
                    + "-fx-background-radius: 8; -fx-border-color: " + Estilos.AZUL_ACENTO
                    + "; -fx-border-radius: 8; -fx-cursor: hand;"));
            tarjeta.setOnMouseExited(e -> tarjeta.setStyle("-fx-background-color: #171717; "
                    + "-fx-background-radius: 8; -fx-border-color: #333333; -fx-border-radius: 8;"));
        }
        tarjeta.setPadding(new Insets(12));
        tarjeta.setPrefWidth(265);
        tarjeta.setStyle("-fx-background-color: #171717; -fx-background-radius: 8; "
                + "-fx-border-color: #333333; -fx-border-radius: 8;");
        return tarjeta;
    }

    private Node crearImagen(Producto producto) {
        return crearImagen(producto, 235, 175);
    }

    private Node crearImagen(Producto producto, double ancho, double alto) {
        if (producto.getImagenArchivo() == null || producto.getImagenArchivo().isBlank()) {
            Label sinImagen = new Label("Imagen no disponible");
            sinImagen.setStyle("-fx-text-fill: #444444;");
            return sinImagen;
        }
        String urlImagen = urlImagen(producto);
        if (urlImagen == null) {
            Label sinImagen = new Label("Imagen no disponible");
            sinImagen.setStyle("-fx-text-fill: #444444;");
            return sinImagen;
        }

        ImageView imagen = new ImageView(new Image(urlImagen, true));
        imagen.setFitWidth(ancho);
        imagen.setFitHeight(alto);
        imagen.setPreserveRatio(true);
        StackPane marco = new StackPane(imagen);
        marco.setPrefSize(ancho, alto);
        marco.setStyle("-fx-background-color: #f4f4f4; -fx-background-radius: 5;");
        return marco;
    }

    private String urlImagen(Producto producto) {
        URL recurso = getClass().getResource("/imagenes/" + producto.getImagenArchivo());
        if (recurso != null) return recurso.toExternalForm();
        Path archivo = Path.of("src", "main", "resources", "imagenes", producto.getImagenArchivo());
        return Files.isRegularFile(archivo) ? archivo.toUri().toString() : null;
    }

    private void abrirDetalleProducto(Producto producto) {
        Stage ventana = new Stage();
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.setTitle(producto.getNombre());
        if (contenido.getScene() != null) ventana.initOwner(contenido.getScene().getWindow());

        Label nombre = Estilos.titulo(producto.getNombre());
        Label precio = new Label(String.format("$%,.0f COP", producto.getPrecio()));
        precio.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");
        Label descripcion = Estilos.subtitulo(producto.getDescripcion().isBlank()
                ? "Sin descripcion disponible." : producto.getDescripcion());
        descripcion.setStyle("-fx-font-size: 15px; -fx-text-fill: " + Estilos.AZUL_CLARO + ";");
        descripcion.setMaxWidth(Double.MAX_VALUE);
        Label stock = Estilos.subtitulo("Disponibles: " + producto.getStock());
        ComboBox<String> talla = new ComboBox<>(FXCollections.observableArrayList(tallasPara(producto)));
        talla.setValue(tallasPara(producto).get(0));
        talla.setPrefWidth(100);
        talla.setMaxWidth(120);
        talla.setStyle("-fx-control-inner-background: " + Estilos.AZUL_MENU + "; -fx-text-fill: white;");
        Label estado = Estilos.subtitulo("");

        Button agregar = Estilos.botonPrimario("Agregar al carrito");
        agregar.setMaxWidth(Double.MAX_VALUE);
        agregar.setOnAction(e -> {
            carrito.agregar(new ItemCarrito(producto, talla.getValue()));
            guardarPreferencias.run();
            actualizarResumen();
            estado.setText("Agregado al carrito en talla " + talla.getValue() + ".");
        });
        Button favorito = Estilos.botonPrimario(favoritos.contiene(producto.getId())
                ? "Quitar de favoritos" : "Agregar a favoritos");
        favorito.setMaxWidth(Double.MAX_VALUE);
        favorito.setOnAction(e -> {
            if (favoritos.agregarSiNoExiste(producto.getId())) {
                favorito.setText("Quitar de favoritos");
                mensaje.setText("Agregado a favoritos.");
            } else {
                favoritos.eliminar(producto.getId());
                favorito.setText("Agregar a favoritos");
                mensaje.setText("Quitado de favoritos.");
            }
            guardarPreferencias.run();
            actualizarResumen();
        });
        VBox datos = new VBox(16, nombre, precio, descripcion, stock,
                Estilos.subtitulo("Selecciona tu talla"), talla, agregar, favorito, estado);
        datos.setAlignment(Pos.CENTER_LEFT);
        HBox detalle = new HBox(24, crearImagen(producto, 440, 440), datos);
        detalle.setAlignment(Pos.CENTER);
        HBox.setHgrow(datos, Priority.ALWAYS);
        VBox pagina = new VBox(detalle);
        pagina.setPadding(new Insets(24));
        pagina.setStyle(Estilos.FONDO_PANTALLA);
        ventana.setScene(new Scene(pagina, 980, 560));
        ventana.showAndWait();
    }

    private void abrirFormularioModificar(Producto producto) {
        Stage ventana = new Stage();
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.setTitle("Modificar producto");
        if (contenido.getScene() != null) {
            ventana.initOwner(contenido.getScene().getWindow());
        }
        AgregarProductoView formulario = new AgregarProductoView(
                gestionCatalogo, producto, () -> {
                    mensaje.setText("Producto modificado correctamente.");
                    refrescar();
                });
        ventana.setScene(new Scene(formulario.getContenido(), 760, 650));
        ventana.showAndWait();
    }

    private void confirmarEliminacion(Producto producto) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar producto");
        confirmacion.setHeaderText("Eliminar " + producto.getNombre() + "?");
        confirmacion.setContentText("Esta accion quitara el producto del catalogo, carrito y favoritos.");
        confirmacion.showAndWait().filter(ButtonType.OK::equals).ifPresent(boton -> {
            String error = controladorCatalogo.eliminarProducto(producto);
            if (error != null) {
                mensaje.setText(error);
            } else {
                mensaje.setText("Producto eliminado correctamente.");
            }
            refrescar();
        });
    }

    private void actualizarResumen() {
        resumen.setText("Carrito: " + carrito.tamano() + " | Favoritos: " + favoritos.tamano());
    }

    private java.util.List<String> tallasPara(Producto producto) {
        String nombre = producto.getNombre().toLowerCase(Locale.ROOT);
        if (nombre.contains("jean") || nombre.contains("pantalon")) {
            return java.util.List.of("28", "30", "32", "34", "36");
        }
        if (nombre.contains("gorra")) return java.util.List.of("Única");
        return java.util.List.of("S", "M", "L", "XL");
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
        String valor = texto == null ? "" : texto.trim()
                .replaceAll("(?i)COP", "")
                .replace("$", "")
                .replaceAll("[\\s\\u00A0]", "");
        if (valor.isEmpty()) return null;
        try {
            if (!valor.matches("[+-]?\\d+(?:[.,]\\d+)*")) {
                throw new NumberFormatException();
            }
            String signo = valor.startsWith("-") || valor.startsWith("+") ? valor.substring(0, 1) : "";
            String cifras = signo.isEmpty() ? valor : valor.substring(1);
            boolean tienePunto = cifras.contains(".");
            boolean tieneComa = cifras.contains(",");
            String normalizado;
            if (tienePunto && tieneComa) {
                int ultimoSeparador = Math.max(cifras.lastIndexOf('.'), cifras.lastIndexOf(','));
                String parteEntera = cifras.substring(0, ultimoSeparador).replaceAll("[.,]", "");
                String decimales = cifras.substring(ultimoSeparador + 1);
                normalizado = signo + parteEntera + "." + decimales;
            } else if (tienePunto || tieneComa) {
                char separador = tienePunto ? '.' : ',';
                String[] partes = cifras.split("\\" + separador, -1);
                boolean separadoresDeMiles = partes.length > 1;
                for (int i = 1; i < partes.length; i++) {
                    separadoresDeMiles &= partes[i].length() == 3;
                }
                if (separadoresDeMiles) {
                    normalizado = signo + String.join("", partes);
                } else if (partes.length == 2) {
                    normalizado = signo + partes[0] + "." + partes[1];
                } else {
                    throw new NumberFormatException();
                }
            } else {
                normalizado = signo + cifras;
            }
            double precio = Double.parseDouble(normalizado);
            if (!Double.isFinite(precio)) {
                throw new IllegalArgumentException("El precio debe ser un numero finito.");
            }
            return precio;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Escribe un precio numerico valido.");
        }
    }

    private void formatearPrecio(TextField campo) {
        try {
            Double precio = leerPrecio(campo.getText());
            if (precio == null) return;
            DecimalFormatSymbols simbolos = new DecimalFormatSymbols(Locale.forLanguageTag("es-CO"));
            simbolos.setGroupingSeparator('.');
            simbolos.setDecimalSeparator(',');
            DecimalFormat formato = new DecimalFormat("#,##0.###", simbolos);
            campo.setText(formato.format(precio));
        } catch (IllegalArgumentException ignored) {
            // El campo conserva el valor invalido para que refrescar muestre su mensaje.
        }
    }

    private void agregarMensajeSinResultados(java.util.List<Producto> resultado) {
        if (resultado.isEmpty()) {
            Label sinResultados = Estilos.subtitulo("No se encontraron productos con esos criterios.");
            productos.add(sinResultados, 0, 0);
        }
    }
}
