package com.proyecto.vista;

import com.proyecto.estructuras.GestionCatalogo;
import com.proyecto.modelo.Producto;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Formulario independiente para que el administrador agregue prendas al catalogo. */
public class AgregarProductoView {
    private final GestionCatalogo catalogo;
    private final VBox contenido = new VBox(18);
    private final TextField nombre = crearCampo("Ejemplo: Chaqueta negra");
    private final TextArea descripcion = new TextArea();
    private final TextField precio = crearCampo("Ejemplo: 89900");
    private final TextField stock = crearCampo("Cantidad disponible");
    private final TextField imagen = crearCampo("Ejemplo: chaqueta-negra.png");
    private final Label mensaje = Estilos.subtitulo("");

    public AgregarProductoView(GestionCatalogo catalogo) {
        this.catalogo = catalogo;
        Label titulo = Estilos.titulo("Agregar producto");
        Label ayuda = Estilos.subtitulo("Completa los datos para incluir una prenda en el catalogo.");

        descripcion.setPromptText("Describe brevemente la prenda");
        descripcion.setWrapText(true);
        descripcion.setPrefRowCount(3);
        Estilos.aplicarCampo(descripcion);

        HBox filaPrecioStock = new HBox(14,
                grupo("Precio (COP)", precio), grupo("Stock inicial", stock));
        HBox.setHgrow(filaPrecioStock.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(filaPrecioStock.getChildren().get(1), Priority.ALWAYS);

        VBox tarjeta = new VBox(14,
                grupo("Nombre", nombre),
                grupo("Descripcion", descripcion),
                filaPrecioStock,
                grupo("Imagen (opcional)", imagen),
                Estilos.subtitulo("La imagen debe estar guardada en src/main/resources/imagenes."),
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

    public Node getContenido() {
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
        var boton = Estilos.botonPrimario("Guardar producto");
        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setOnAction(e -> agregarProducto());
        return boton;
    }

    private void agregarProducto() {
        try {
            String nombreProducto = nombre.getText().trim();
            double precioProducto = Double.parseDouble(precio.getText().trim());
            int stockProducto = Integer.parseInt(stock.getText().trim());
            if (nombreProducto.isEmpty() || precioProducto <= 0 || stockProducto < 0) {
                mensaje.setText("Escribe un nombre, un precio mayor que cero y un stock de cero o más.");
                return;
            }

            Producto producto = new Producto(catalogo.siguienteId(), nombreProducto,
                    descripcion.getText().trim(), precioProducto, stockProducto, imagen.getText().trim());
            catalogo.agregarProducto(producto);
            mensaje.setText("Producto agregado. Puedes verlo en Gestionar catalogo.");
            nombre.clear();
            descripcion.clear();
            precio.clear();
            stock.clear();
            imagen.clear();
        } catch (NumberFormatException ex) {
            mensaje.setText("El precio y el stock deben tener un formato numerico valido.");
        }
    }
}
