package com.proyecto.vista;

import com.proyecto.estructuras.GestionCatalogo;
import com.proyecto.modelo.Producto;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Formulario independiente para que el administrador agregue prendas al catalogo. */
public class AgregarProductoView {
    private final GestionCatalogo catalogo;
    private final Producto productoEdicion;
    private final Runnable alGuardar;
    private final VBox contenido = new VBox(18);
    private final TextField nombre = crearCampo("Ejemplo: Chaqueta negra");
    private final TextArea descripcion = new TextArea();
    private final TextField precio = crearCampo("Ejemplo: 89900");
    private final TextField stock = crearCampo("Cantidad disponible");
    private final TextField imagen = crearCampo("Ejemplo: chaqueta-negra.png");
    private final Label mensaje = Estilos.subtitulo("");

    public AgregarProductoView(GestionCatalogo catalogo) {
        this(catalogo, null, null);
    }

    public AgregarProductoView(GestionCatalogo catalogo, Producto productoEdicion, Runnable alGuardar) {
        this.catalogo = catalogo;
        this.productoEdicion = productoEdicion;
        this.alGuardar = alGuardar;
        boolean editando = productoEdicion != null;
        if (editando) {
            nombre.setText(productoEdicion.getNombre());
            descripcion.setText(productoEdicion.getDescripcion());
            precio.setText(String.valueOf(productoEdicion.getPrecio()));
            stock.setText(String.valueOf(productoEdicion.getStock()));
            imagen.setText(productoEdicion.getImagenArchivo());
        }
        Label titulo = Estilos.titulo(editando ? "Modificar producto" : "Agregar producto");
        Label ayuda = Estilos.subtitulo(editando
                ? "Actualiza los datos de la prenda seleccionada."
                : "Completa los datos para incluir una prenda en el catalogo.");

        descripcion.setPromptText("Describe brevemente la prenda");
        descripcion.setWrapText(true);
        descripcion.setPrefRowCount(3);
        Estilos.aplicarCampo(descripcion);

        HBox filaPrecioStock = new HBox(14,
                grupo("Precio (COP)", precio), grupo("Stock inicial", stock));
        HBox.setHgrow(filaPrecioStock.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(filaPrecioStock.getChildren().get(1), Priority.ALWAYS);

        Button elegirImagen = Estilos.botonPrimario("Elegir archivo");
        elegirImagen.setOnAction(e -> seleccionarImagen());
        HBox filaImagen = new HBox(10, imagen, elegirImagen);
        HBox.setHgrow(imagen, Priority.ALWAYS);

        VBox tarjeta = new VBox(14,
                grupo("Nombre", nombre),
                grupo("Descripcion", descripcion),
                filaPrecioStock,
                grupo("Imagen (opcional)", filaImagen),
                Estilos.subtitulo("El archivo elegido se copia a src/main/resources/imagenes."),
                crearBotonAgregar(), mensaje);
        tarjeta.setPadding(new Insets(24));
        tarjeta.setMaxWidth(600);
        tarjeta.setStyle("-fx-background-color: #171717; -fx-background-radius: 12; "
                + "-fx-border-color: #333333; -fx-border-radius: 12;");

        contenido.setAlignment(Pos.TOP_CENTER);
        contenido.setPadding(new Insets(24));
        contenido.setStyle(Estilos.FONDO_PANTALLA);
        contenido.getChildren().addAll(titulo, ayuda, tarjeta);
    }

    public Parent getContenido() {
        return contenido;
    }

    private VBox grupo(String titulo, Node campo) {
        Label etiqueta = Estilos.subtitulo(titulo);
        VBox grupo = new VBox(6, etiqueta, campo);
        VBox.setVgrow(campo, Priority.NEVER);
        return grupo;
    }

    private TextField crearCampo(String indicacion) {
        TextField campo = new TextField();
        campo.setPromptText(indicacion);
        Estilos.aplicarCampo(campo);
        return campo;
    }

    private javafx.scene.control.Button crearBotonAgregar() {
        var boton = Estilos.botonPrimario(productoEdicion == null ? "Guardar producto" : "Guardar cambios");
        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setOnAction(e -> guardarProducto());
        return boton;
    }

    private void seleccionarImagen() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Seleccionar foto del producto");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Imagenes", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        Stage ventana = contenido.getScene() != null
                && contenido.getScene().getWindow() instanceof Stage stage ? stage : null;
        var archivo = selector.showOpenDialog(ventana);
        if (archivo == null) return;
        try {
            Path carpetaImagenes = Path.of("src", "main", "resources", "imagenes");
            Files.createDirectories(carpetaImagenes);
            Path origen = archivo.toPath();
            String nombre = origen.getFileName().toString();
            Path destino = carpetaImagenes.resolve(nombre);
            int numero = 2;
            while (Files.exists(destino) && !Files.isSameFile(origen, destino)) {
                String nombreBase = nombre;
                String extension = "";
                int punto = nombre.lastIndexOf('.');
                if (punto >= 0) {
                    nombreBase = nombre.substring(0, punto);
                    extension = nombre.substring(punto);
                }
                destino = carpetaImagenes.resolve(nombreBase + "-" + numero++ + extension);
            }
            boolean mismoArchivo = Files.exists(destino) && Files.isSameFile(origen, destino);
            if (!mismoArchivo) {
                Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            }
            imagen.setText(destino.getFileName().toString());
            mensaje.setText("Foto seleccionada: " + destino.getFileName());
        } catch (IOException ex) {
            mensaje.setText("No se pudo copiar la foto: " + ex.getMessage());
        }
    }

    private void guardarProducto() {
        try {
            String nombreProducto = nombre.getText().trim();
            double precioProducto = Double.parseDouble(precio.getText().trim());
            int stockProducto = Integer.parseInt(stock.getText().trim());
            if (nombreProducto.isEmpty() || !Double.isFinite(precioProducto)
                    || precioProducto <= 0 || stockProducto < 0) {
                mensaje.setText("Escribe un nombre, un precio mayor que cero y un stock de cero o más.");
                return;
            }

            if (productoEdicion != null) {
                if (!catalogo.modificar(productoEdicion.getId(), nombreProducto, descripcion.getText().trim(),
                        precioProducto, stockProducto, imagen.getText().trim())) {
                    mensaje.setText("No se encontro el producto para modificar.");
                    return;
                }
                mensaje.setText("Producto modificado correctamente.");
                if (alGuardar != null) alGuardar.run();
                if (contenido.getScene() != null && contenido.getScene().getWindow() instanceof Stage ventana) {
                    ventana.close();
                }
            } else {
                Producto producto = new Producto(catalogo.siguienteId(), nombreProducto,
                        descripcion.getText().trim(), precioProducto, stockProducto, imagen.getText().trim());
                catalogo.agregarProducto(producto);
                mensaje.setText("Producto agregado. Puedes verlo en Gestionar catalogo.");
                nombre.clear();
                descripcion.clear();
                precio.clear();
                stock.clear();
                imagen.clear();
            }
        } catch (NumberFormatException ex) {
            mensaje.setText("El precio y el stock deben tener un formato numerico valido.");
        } catch (IllegalArgumentException ex) {
            mensaje.setText(ex.getMessage());
        }
    }
}
