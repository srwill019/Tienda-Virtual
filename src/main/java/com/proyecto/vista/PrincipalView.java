package com.proyecto.vista;

import com.proyecto.controlador.ContextoApp;
import com.proyecto.estructuras.PilaEnlazada;
import com.proyecto.modelo.Producto;
import com.proyecto.modelo.Pedido;
import com.proyecto.modelo.Operacion;
import com.proyecto.modelo.ItemPedido;
import com.proyecto.modelo.DatosCompra;
import com.proyecto.modelo.Usuario;
import com.proyecto.modelo.ItemCarrito;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.URL;
import java.time.format.DateTimeFormatter;

/** Ventana principal con menu lateral y panel central. */
public class PrincipalView {
    private final BorderPane raiz = new BorderPane();
    private final VBox menu = new VBox(6);
    private final List<Button> botonesMenu = new ArrayList<>();
    private final PilaEnlazada<Node> pantallasAnteriores = new PilaEnlazada<>();
    private final PilaEnlazada<Button> opcionesAnteriores = new PilaEnlazada<>();
    private final Button volver = Estilos.botonEnlace("←");
    private Button opcionActiva;

    public PrincipalView(ContextoApp contexto, Runnable alCerrarSesion) {
        Usuario usuario = contexto.getUsuarioActual();
        boolean admin = usuario.esAdministrador();

        Label marca = new Label("NEXUS");
        marca.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label sesion = Estilos.subtitulo(usuario.getNombreUsuario() + " | " + usuario.getRol());
        Region espacioSuperior = new Region();
        menu.getChildren().addAll(marca, sesion, espacioSuperior);

        agregarOpcion(admin ? "Gestionar catalogo" : "Catalogo",
                () -> new CatalogoView(contexto.getCatalogo(), contexto.getGestionPedidos(), contexto.getCarrito(),
                        contexto.getFavoritos(), admin, contexto::guardarDatosUsuarioActual)
                        .getContenido());
        if (admin) {
            agregarOpcion("Agregar producto",
                    () -> new AgregarProductoView(contexto.getCatalogo()).getContenido());
            agregarOpcion("Historial", () -> panelHistorial(contexto));
        } else {
            agregarOpcion("Carrito", () -> panelProductos(contexto, false));
            agregarOpcion("Favoritos", () -> panelProductos(contexto, true));
            agregarOpcion("Mis compras", () -> panelMisCompras(contexto));
        }
        if (admin) {
            agregarOpcion("Pedidos pendientes", () -> panelPedidos(contexto));
        }

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

        HBox barraNavegacion = new HBox(volver);
        barraNavegacion.setPadding(new Insets(12, 25, 0, 25));
        raiz.setTop(barraNavegacion);
        volver.setMinSize(34, 32);
        volver.setStyle("-fx-background-color: " + Estilos.AZUL_ACENTO
                + "; -fx-text-fill: white; -fx-font-size: 21px; -fx-font-weight: bold;"
                + " -fx-padding: 0 6 2 6; -fx-background-radius: 6; -fx-cursor: hand;");
        volver.setDisable(true);
        volver.setOnAction(e -> volverPantallaAnterior());

        botonesMenu.get(0).fire();
    }

    public Parent getContenido() {
        return raiz;
    }

    private void agregarOpcion(String texto, Supplier<Node> creadorPantalla) {
        Button boton = Estilos.botonMenu(texto);
        boton.setOnAction(e -> {
            if (boton != opcionActiva && raiz.getCenter() != null && opcionActiva != null) {
                pantallasAnteriores.apilar(raiz.getCenter());
                opcionesAnteriores.apilar(opcionActiva);
                volver.setDisable(false);
            }
            marcarActivo(boton);
            opcionActiva = boton;
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

    private void volverPantallaAnterior() {
        if (pantallasAnteriores.estaVacia()) {
            volver.setDisable(true);
            return;
        }
        mostrar(pantallasAnteriores.desapilar());
        opcionActiva = opcionesAnteriores.desapilar();
        marcarActivo(opcionActiva);
        volver.setDisable(pantallasAnteriores.estaVacia());
    }

    private Node panelPedidos(ContextoApp contexto) {
        VBox panel = new VBox(10, Estilos.titulo("Pedidos pendientes"));
        panel.setPadding(new Insets(20));
        panel.setStyle(Estilos.FONDO_PANTALLA);
        List<Pedido> pedidos = contexto.getGestionPedidos().getPedidosPendientes();
        Label mensaje = Estilos.subtitulo("");
        Button atender = Estilos.botonPrimario("Atender siguiente");
        atender.setDisable(pedidos.isEmpty());
        atender.setOnAction(e -> {
            try {
                Pedido procesado = contexto.getGestionPedidos().procesarSiguiente();
                if (procesado == null) {
                    mensaje.setText("No hay pedidos pendientes.");
                } else {
                    mostrar(panelPedidos(contexto));
                }
            } catch (IllegalStateException ex) {
                mensaje.setText(ex.getMessage());
            }
        });
        panel.getChildren().addAll(atender, mensaje);
        if (pedidos.isEmpty()) {
            panel.getChildren().add(Estilos.subtitulo("No hay pedidos pendientes."));
            return panel;
        }
        panel.getChildren().add(Estilos.subtitulo("Los pedidos se atienden en orden de llegada."));
        for (Pedido pedido : pedidos) {
            panel.getChildren().add(detallePedido(pedido));
        }
        return panel;
    }

    private Node panelHistorial(ContextoApp contexto) {
        VBox panel = new VBox(10, Estilos.titulo("Historial"));
        panel.setPadding(new Insets(20));
        panel.setStyle(Estilos.FONDO_PANTALLA);
        List<Operacion> operaciones = contexto.getGestionPedidos().getHistorial();
        Button deshacer = Estilos.botonPrimario("Deshacer ultima operacion");
        deshacer.setDisable(operaciones.isEmpty());
        deshacer.setOnAction(e -> {
            if (contexto.getGestionPedidos().deshacerUltimoProceso() != null) {
                mostrar(panelHistorial(contexto));
            }
        });
        panel.getChildren().add(deshacer);
        if (operaciones.isEmpty()) {
            panel.getChildren().add(Estilos.subtitulo("No hay operaciones para mostrar."));
            return panel;
        }
        for (Operacion operacion : operaciones) {
            panel.getChildren().add(detallePedido(operacion.getPedido()));
        }
        return panel;
    }

    private Node panelMisCompras(ContextoApp contexto) {
        VBox panel = new VBox(10, Estilos.titulo("Mis compras"));
        panel.setPadding(new Insets(20));
        panel.setStyle(Estilos.FONDO_PANTALLA);
        List<Pedido> pedidos = contexto.getGestionPedidos()
                .getPedidosDeUsuario(contexto.getUsuarioActual().getNombreUsuario());
        if (pedidos.isEmpty()) {
            panel.getChildren().add(Estilos.subtitulo("Todavia no has realizado compras."));
            return panel;
        }
        VBox listaPedidos = new VBox(14);
        listaPedidos.setPadding(new Insets(2, 4, 12, 2));
        for (int i = pedidos.size() - 1; i >= 0; i--) {
            listaPedidos.getChildren().add(detallePedido(pedidos.get(i)));
        }
        ScrollPane desplazamiento = new ScrollPane(listaPedidos);
        desplazamiento.setFitToWidth(true);
        desplazamiento.setStyle("-fx-background: black; -fx-background-color: black;");
        panel.getChildren().add(desplazamiento);
        VBox.setVgrow(desplazamiento, Priority.ALWAYS);
        return panel;
    }

    private Node detallePedido(Pedido pedido) {
        DatosCompra datos = pedido.getDatosCompra();
        VBox detalle = new VBox(5);
        detalle.setSpacing(10);
        detalle.setPadding(new Insets(20));
        detalle.setMaxWidth(900);
        detalle.setStyle("-fx-background-color: #171717; -fx-background-radius: 10;"
                + " -fx-border-color: #333333; -fx-border-radius: 10;");
        Label encabezado = new Label(pedido + "  |  Estado: " + pedido.getEstado() + "  |  "
                + pedido.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        encabezado.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        detalle.getChildren().addAll(encabezado,
                filaDato("Cliente", datos.getNombreCompleto()),
                filaDato("Usuario", pedido.getNombreUsuario()),
                filaDato("Documento", datos.getDocumento()),
                filaDato("Teléfono", datos.getTelefono()),
                filaDato("Correo", datos.getCorreo()),
                filaDato("Dirección", datos.getDireccion()),
                filaDato("Ciudad", datos.getCiudad()),
                filaDato("Método de pago", datos.getMetodoPago().toString()),
                Estilos.subtitulo("Productos"));
        for (ItemPedido item : pedido.getItems().obtenerCopia()) {
            Label nombreProducto = new Label(item.getProducto().getNombre());
            nombreProducto.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: white;");
            VBox datosProducto = new VBox(6, nombreProducto,
                    filaDato("Talla", item.getTalla()),
                    filaDato("Cantidad", String.valueOf(item.getCantidad())),
                    filaDato("Subtotal", String.format("$%,.0f COP", item.getSubtotal())));
            HBox filaProducto = new HBox(16, crearImagenProducto(item.getProducto(), 150, 115), datosProducto);
            filaProducto.setAlignment(Pos.CENTER_LEFT);
            filaProducto.setPadding(new Insets(10));
            filaProducto.setStyle("-fx-background-color: #202020; -fx-background-radius: 8;");
            detalle.getChildren().add(filaProducto);
        }
        Label total = new Label("Total: " + String.format("$%,.0f COP", pedido.getTotal()));
        total.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        detalle.getChildren().add(total);
        return detalle;
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

    private Node panelProductos(ContextoApp contexto, boolean mostrarFavoritos) {
        String nombre = mostrarFavoritos ? "Favoritos" : "Carrito";
        VBox panel = new VBox(12, Estilos.titulo(nombre));
        panel.setPadding(new Insets(20));
        panel.setStyle(Estilos.FONDO_PANTALLA);

        List<Producto> favoritos = mostrarFavoritos
                ? contexto.getCatalogo().getProductos().stream()
                    .filter(p -> contexto.getFavoritos().contiene(p.getId())).toList()
                : List.of();
        List<ItemCarrito> articulos = mostrarFavoritos ? List.of() : contexto.getCarrito().obtenerCopia();
        if ((mostrarFavoritos && favoritos.isEmpty()) || (!mostrarFavoritos && articulos.isEmpty())) {
            panel.getChildren().add(Estilos.subtitulo(mostrarFavoritos
                    ? "Todavia no tienes productos favoritos." : "El carrito esta vacio."));
            if (!mostrarFavoritos) {
                Button continuarCompra = Estilos.botonPrimario("Continuar al proceso de compra");
                continuarCompra.setOnAction(e -> mostrar(new FinalizarCompraView(contexto).getContenido()));
                panel.getChildren().add(continuarCompra);
            }
            return panel;
        }

        GridPane tarjetas = new GridPane();
        tarjetas.setHgap(16);
        tarjetas.setVgap(16);
        tarjetas.setAlignment(Pos.TOP_LEFT);
        int indice = 0;
        if (mostrarFavoritos) {
            for (Producto producto : favoritos) {
                tarjetas.add(crearTarjetaProducto(contexto, producto), indice % 4, indice / 4);
                indice++;
            }
        } else {
            for (ItemCarrito item : articulos) {
                tarjetas.add(crearTarjetaCarrito(contexto, item), indice % 4, indice / 4);
                indice++;
            }
        }

        ScrollPane desplazamiento = new ScrollPane(tarjetas);
        desplazamiento.setFitToWidth(true);
        desplazamiento.setStyle("-fx-background: black; -fx-background-color: black;");
        panel.getChildren().add(desplazamiento);
        VBox.setVgrow(desplazamiento, Priority.ALWAYS);
        if (!mostrarFavoritos) {
            Button continuarCompra = Estilos.botonPrimario("Continuar al proceso de compra");
            continuarCompra.setOnAction(e -> mostrar(new FinalizarCompraView(contexto).getContenido()));
            panel.getChildren().add(continuarCompra);
        }
        return panel;
    }

    private VBox crearTarjetaProducto(ContextoApp contexto, Producto producto) {
        Label nombre = new Label(producto.getNombre());
        nombre.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label precio = new Label(String.format("$%,.0f COP", producto.getPrecio()));
        precio.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
        Label descripcion = new Label(producto.getDescripcion());
        descripcion.setWrapText(true);
        descripcion.setMinHeight(42);
        descripcion.setStyle("-fx-text-fill: #cccccc;");

        Button quitar = Estilos.botonPrimario("Quitar de favoritos");
        quitar.setMaxWidth(Double.MAX_VALUE);
        quitar.setOnAction(e -> {
            contexto.getFavoritos().eliminar(producto.getId());
            contexto.guardarDatosUsuarioActual();
            mostrar(panelProductos(contexto, true));
        });

        VBox tarjeta = new VBox(10, crearImagenProducto(producto), nombre, precio, descripcion, quitar);
        tarjeta.setPadding(new Insets(12));
        tarjeta.setPrefWidth(265);
        tarjeta.setStyle("-fx-background-color: #171717; -fx-background-radius: 8; "
                + "-fx-border-color: #333333; -fx-border-radius: 8;");
        return tarjeta;
    }

    private VBox crearTarjetaCarrito(ContextoApp contexto, ItemCarrito item) {
        Producto producto = item.getProducto();
        Label nombre = new Label(producto.getNombre());
        nombre.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label talla = Estilos.subtitulo("Talla: " + item.getTalla());
        Label precio = Estilos.subtitulo(String.format("$%,.0f COP", producto.getPrecio()));
        Button quitar = Estilos.botonPrimario("Quitar del carrito");
        quitar.setMaxWidth(Double.MAX_VALUE);
        quitar.setOnAction(e -> {
            contexto.getCarrito().eliminar(item);
            contexto.guardarDatosUsuarioActual();
            mostrar(panelProductos(contexto, false));
        });
        VBox tarjeta = new VBox(10, crearImagenProducto(producto), nombre, talla, precio, quitar);
        tarjeta.setPadding(new Insets(12));
        tarjeta.setPrefWidth(265);
        tarjeta.setStyle("-fx-background-color: #171717; -fx-background-radius: 8; "
                + "-fx-border-color: #333333; -fx-border-radius: 8;");
        return tarjeta;
    }

    private Node crearImagenProducto(Producto producto) {
        return crearImagenProducto(producto, 235, 175);
    }

    private Node crearImagenProducto(Producto producto, double ancho, double alto) {
        if (producto.getImagenArchivo() == null || producto.getImagenArchivo().isBlank()) {
            Label sinImagen = new Label("Imagen no disponible");
            sinImagen.setStyle("-fx-text-fill: #444444;");
            return sinImagen;
        }
        URL recurso = getClass().getResource("/imagenes/" + producto.getImagenArchivo());
        String urlImagen = recurso == null ? null : recurso.toExternalForm();
        if (urlImagen == null) {
            Path archivo = Path.of("src", "main", "resources", "imagenes", producto.getImagenArchivo());
            if (Files.isRegularFile(archivo)) urlImagen = archivo.toUri().toString();
        }
        if (urlImagen == null) {
            Label sinImagen = new Label("Imagen no disponible");
            sinImagen.setStyle("-fx-text-fill: #cccccc;");
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
}
