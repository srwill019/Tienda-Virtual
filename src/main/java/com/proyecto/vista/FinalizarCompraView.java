package com.proyecto.vista;

import com.proyecto.controlador.ContextoApp;
import com.proyecto.modelo.DatosCompra;
import com.proyecto.modelo.MetodoPago;
import com.proyecto.modelo.Pedido;
import com.proyecto.modelo.Producto;
import com.proyecto.modelo.ItemCarrito;
import com.proyecto.modelo.ItemPedido;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.URL;
import java.time.format.DateTimeFormatter;

/** Resumen y formulario para confirmar una compra simulada. */
public class FinalizarCompraView {
    private final ContextoApp contexto;
    private final VBox contenido = new VBox(14);
    private final TextField nombre = campo("Nombre completo");
    private final TextField documento = campo("Documento");
    private final TextField telefono = campo("Telefono");
    private final TextField correo = campo("Correo");
    private final TextField direccion = campo("Direccion");
    private final TextField ciudad = campo("Ciudad");
    private final ComboBox<MetodoPago> metodoPago = new ComboBox<>(
            FXCollections.observableArrayList(MetodoPago.values()));
    private final Label error = new Label();

    public FinalizarCompraView(ContextoApp contexto) {
        this.contexto = contexto;
        contenido.setPadding(new Insets(24));
        contenido.setAlignment(Pos.TOP_CENTER);
        contenido.setStyle(Estilos.FONDO_PANTALLA);
        contenido.getChildren().add(Estilos.titulo("Finalizar compra"));
        if (contexto.getCarrito().estaVacia()) {
            contenido.getChildren().add(Estilos.subtitulo("El carrito esta vacio."));
            return;
        }

        contenido.getChildren().add(resumenCarrito());
        metodoPago.setValue(MetodoPago.CONTRA_ENTREGA);
        metodoPago.setStyle("-fx-control-inner-background: " + Estilos.AZUL_MENU + "; -fx-text-fill: white;");
        error.setStyle("-fx-text-fill: " + Estilos.ROJO_ERROR + ";");
        error.setWrapText(true);
        VBox selectorPago = new VBox(6, Estilos.subtitulo("Metodo de pago"), metodoPago);
        metodoPago.setMaxWidth(Double.MAX_VALUE);
        VBox formulario = new VBox(8,
                Estilos.subtitulo("Datos de compra y envio"), nombre, documento, telefono,
                correo, direccion, ciudad, selectorPago, error);
        formulario.setPadding(new Insets(18));
        formulario.setMaxWidth(620);
        formulario.setAlignment(Pos.CENTER_LEFT);
        formulario.setStyle("-fx-background-color: #171717; -fx-background-radius: 10;");
        Button confirmar = Estilos.botonPrimario("Confirmar compra");
        confirmar.setMaxWidth(620);
        confirmar.setOnAction(e -> confirmarCompra());
        VBox.setMargin(confirmar, new Insets(0, 0, 16, 0));
        contenido.getChildren().addAll(formulario, confirmar);
    }

    public Node getContenido() {
        ScrollPane desplazamiento = new ScrollPane(contenido);
        desplazamiento.setFitToWidth(true);
        desplazamiento.setStyle("-fx-background: black; -fx-background-color: black;");
        return desplazamiento;
    }

    private VBox resumenCarrito() {
        VBox resumen = new VBox(8);
        resumen.setPadding(new Insets(18));
        resumen.setMaxWidth(620);
        resumen.setStyle("-fx-background-color: #171717; -fx-background-radius: 10;");
        resumen.getChildren().add(Estilos.subtitulo("Resumen del carrito"));
        List<ItemCarrito> agrupados = new ArrayList<>();
        List<Integer> cantidades = new ArrayList<>();
        for (ItemCarrito item : contexto.getCarrito().obtenerCopia()) {
            int indice = agrupados.indexOf(item);
            if (indice < 0) {
                agrupados.add(item);
                cantidades.add(1);
            } else {
                cantidades.set(indice, cantidades.get(indice) + 1);
            }
        }
        double total = 0;
        for (int i = 0; i < agrupados.size(); i++) {
            ItemCarrito item = agrupados.get(i);
            Producto producto = item.getProducto();
            int cantidad = cantidades.get(i);
            double subtotal = producto.getPrecio() * cantidad;
            total += subtotal;
            HBox filaProducto = new HBox(14, crearImagen(producto), Estilos.subtitulo(
                    producto.getNombre() + " | Talla: " + item.getTalla() + " x " + cantidad
                            + " | Subtotal: " + String.format("$%,.0f COP", subtotal)));
            filaProducto.setAlignment(Pos.CENTER_LEFT);
            resumen.getChildren().add(filaProducto);
        }
        resumen.getChildren().add(Estilos.subtitulo("Total: " + String.format("$%,.0f COP", total)));
        return resumen;
    }

    private Node crearImagen(Producto producto) {
        return crearImagen(producto, 100, 76);
    }

    private Node crearImagen(Producto producto, double ancho, double alto) {
        String urlImagen = null;
        if (producto.getImagenArchivo() != null && !producto.getImagenArchivo().isBlank()) {
            URL recurso = getClass().getResource("/imagenes/" + producto.getImagenArchivo());
            if (recurso != null) urlImagen = recurso.toExternalForm();
            else {
                Path archivo = Path.of("src", "main", "resources", "imagenes", producto.getImagenArchivo());
                if (Files.isRegularFile(archivo)) urlImagen = archivo.toUri().toString();
            }
        }
        if (urlImagen == null) {
            Label sinImagen = new Label("Sin foto");
            sinImagen.setStyle("-fx-text-fill: #cccccc;");
            StackPane marco = new StackPane(sinImagen);
            marco.setPrefSize(ancho, alto);
            marco.setStyle("-fx-background-color: #292929; -fx-background-radius: 5;");
            return marco;
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

    private void confirmarCompra() {
        try {
            DatosCompra datos = new DatosCompra(nombre.getText(), documento.getText(), telefono.getText(),
                    correo.getText(), direccion.getText(), ciudad.getText(), metodoPago.getValue());
            Pedido pedido = contexto.getGestionPedidos().crearPedido(
                    contexto.getUsuarioActual(), datos, contexto.getCarrito());
            contexto.guardarDatosUsuarioActual();
            contenido.getChildren().clear();
            contenido.setAlignment(Pos.TOP_CENTER);
            contenido.getChildren().addAll(Estilos.titulo("Compra realizada"), reciboCompra(pedido),
                    Estilos.subtitulo("Tu compra quedó registrada. Puedes consultar su estado en Mis compras."));
        } catch (IllegalArgumentException ex) {
            error.setText(ex.getMessage());
        }
    }

    private VBox reciboCompra(Pedido pedido) {
        DatosCompra datos = pedido.getDatosCompra();
        VBox recibo = new VBox(10);
        recibo.setPadding(new Insets(20));
        recibo.setMaxWidth(900);
        recibo.setStyle("-fx-background-color: #171717; -fx-background-radius: 10;"
                + " -fx-border-color: #333333; -fx-border-radius: 10;");
        Label encabezado = new Label("Pedido #" + pedido.getId() + "  |  " + pedido.getEstado()
                + "  |  " + pedido.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        encabezado.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        recibo.getChildren().addAll(encabezado,
                filaDato("Nombre", datos.getNombreCompleto()),
                filaDato("Documento", datos.getDocumento()),
                filaDato("Teléfono", datos.getTelefono()),
                filaDato("Correo", datos.getCorreo()),
                filaDato("Dirección", datos.getDireccion()),
                filaDato("Ciudad", datos.getCiudad()),
                filaDato("Método de pago", datos.getMetodoPago().toString()),
                Estilos.subtitulo("Productos"));

        for (ItemPedido item : pedido.getItems().obtenerCopia()) {
            Producto producto = item.getProducto();
            Label nombreProducto = new Label(producto.getNombre());
            nombreProducto.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: white;");
            VBox datosProducto = new VBox(6, nombreProducto,
                    filaDato("Talla", item.getTalla()),
                    filaDato("Cantidad", String.valueOf(item.getCantidad())),
                    filaDato("Subtotal", String.format("$%,.0f COP", item.getSubtotal())));
            HBox filaProducto = new HBox(16, crearImagen(producto, 150, 115), datosProducto);
            filaProducto.setAlignment(Pos.CENTER_LEFT);
            filaProducto.setPadding(new Insets(10));
            filaProducto.setStyle("-fx-background-color: #202020; -fx-background-radius: 8;");
            recibo.getChildren().add(filaProducto);
        }

        Label total = new Label("Total pagado: " + String.format("$%,.0f COP", pedido.getTotal()));
        total.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        recibo.getChildren().add(total);
        return recibo;
    }

    private HBox filaDato(String etiqueta, String valor) {
        Label titulo = new Label(etiqueta + ":");
        titulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label detalle = new Label(valor);
        detalle.setWrapText(true);
        detalle.setStyle("-fx-font-size: 14px; -fx-text-fill: " + Estilos.AZUL_CLARO + ";");
        HBox fila = new HBox(6, titulo, detalle);
        fila.setAlignment(Pos.CENTER_LEFT);
        return fila;
    }

    private static TextField campo(String indicacion) {
        TextField campo = new TextField();
        campo.setPromptText(indicacion);
        Estilos.aplicarCampo(campo);
        return campo;
    }
}
