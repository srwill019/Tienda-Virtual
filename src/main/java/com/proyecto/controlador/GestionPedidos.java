package com.proyecto.controlador;

import com.proyecto.modelo.Operacion;
import com.proyecto.modelo.Pedido;
import com.proyecto.modelo.Producto;
import com.proyecto.modelo.Usuario;
import com.proyecto.modelo.DatosCompra;
import com.proyecto.modelo.EstadoPedido;
import com.proyecto.modelo.ItemPedido;
import com.proyecto.modelo.ItemCarrito;
import com.proyecto.estructuras.ColaEnlazada;
import com.proyecto.estructuras.ListaSimpleEnlazada;
import com.proyecto.estructuras.PilaEnlazada;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.time.LocalDateTime;

public class GestionPedidos {
    private static final Path ARCHIVO_PEDIDOS = Path.of("pedidos.txt");
    private final ColaEnlazada<Pedido> colaPedidos = new ColaEnlazada<>();
    private final PilaEnlazada<Operacion> historial = new PilaEnlazada<>();
    private final List<Pedido> todosLosPedidos = new ArrayList<>();
    private Runnable guardarInventario = () -> { };
    private int siguienteId = 1;

    public Pedido crearPedido(Usuario usuario, DatosCompra datos, ListaSimpleEnlazada<ItemCarrito> carrito) {
        if (usuario == null || datos == null || carrito == null || carrito.estaVacia()) {
            throw new IllegalArgumentException("El carrito esta vacio o faltan datos para crear el pedido.");
        }
        ListaSimpleEnlazada<ItemPedido> items = new ListaSimpleEnlazada<>();
        List<ItemCarrito> itemsAgrupados = new ArrayList<>();
        List<Integer> cantidades = new ArrayList<>();
        List<Producto> productosValidar = new ArrayList<>();
        List<Integer> cantidadesPorProducto = new ArrayList<>();
        for (ItemCarrito item : carrito.obtenerCopia()) {
            Producto producto = item.getProducto();
            int indiceProducto = productosValidar.indexOf(producto);
            if (indiceProducto < 0) {
                productosValidar.add(producto);
                cantidadesPorProducto.add(1);
            } else {
                cantidadesPorProducto.set(indiceProducto, cantidadesPorProducto.get(indiceProducto) + 1);
            }
            int indice = itemsAgrupados.indexOf(item);
            if (indice < 0) {
                itemsAgrupados.add(item);
                cantidades.add(1);
            } else {
                cantidades.set(indice, cantidades.get(indice) + 1);
            }
        }
        for (int i = 0; i < productosValidar.size(); i++) {
            Producto producto = productosValidar.get(i);
            int cantidad = cantidadesPorProducto.get(i);
            int yaSolicitadas = unidadesPendientes(producto);
            if (cantidad + yaSolicitadas > producto.getStock()) {
                throw new IllegalArgumentException("No hay stock suficiente de " + producto.getNombre()
                        + ". Disponibles para nuevos pedidos: " + Math.max(0, producto.getStock() - yaSolicitadas)
                        + ".");
            }
        }
        for (int i = 0; i < itemsAgrupados.size(); i++) {
            ItemCarrito articulo = itemsAgrupados.get(i);
            items.agregar(new ItemPedido(articulo.getProducto(), cantidades.get(i),
                    articulo.getProducto().getPrecio(), articulo.getTalla()));
        }
        Pedido pedido = new Pedido(siguienteId++, usuario.getNombreUsuario(), datos, items);
        colaPedidos.encolar(pedido);
        todosLosPedidos.add(pedido);
        for (ItemCarrito item : carrito.obtenerCopia()) {
            carrito.eliminar(item);
        }
        guardarPedidos();
        return pedido;
    }

    public Pedido procesarSiguiente() {
        Pedido siguiente = colaPedidos.verFrente();
        if (siguiente == null) return null;
        for (ItemPedido item : siguiente.getItems().obtenerCopia()) {
            if (item.getProducto().getStock() < item.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente de " + item.getProducto().getNombre()
                        + ". El pedido permanece pendiente.");
            }
        }
        Pedido procesado = colaPedidos.desencolar();
        for (ItemPedido item : procesado.getItems().obtenerCopia()) {
            item.getProducto().setStock(item.getProducto().getStock() - item.getCantidad());
        }
        procesado.setEstado(EstadoPedido.ATENDIDO);
        historial.apilar(new Operacion(procesado));
        guardarInventario.run();
        guardarPedidos();
        return procesado;
    }

    public Pedido deshacerUltimoProceso() {
        if (historial.estaVacia()) {
            return null;
        }

        Pedido deshecho = historial.desapilar().getPedido();
        for (ItemPedido item : deshecho.getItems().obtenerCopia()) {
            item.getProducto().setStock(item.getProducto().getStock() + item.getCantidad());
        }
        deshecho.setEstado(EstadoPedido.PENDIENTE);
        colaPedidos.encolarAlFrente(deshecho);
        guardarInventario.run();
        guardarPedidos();
        return deshecho;
    }

    public boolean productoEnPedidos(Producto producto) {
        if (producto == null) return false;
        for (Pedido pedido : colaPedidos.obtenerCopia()) {
            if (contieneProducto(pedido, producto)) return true;
        }
        for (Operacion operacion : historial.obtenerCopia()) {
            if (contieneProducto(operacion.getPedido(), producto)) return true;
        }
        return false;
    }

    private int unidadesPendientes(Producto producto) {
        int cantidad = 0;
        for (Pedido pedido : colaPedidos.obtenerCopia()) {
            for (ItemPedido item : pedido.getItems().obtenerCopia()) {
                if (item.getProducto() == producto) cantidad += item.getCantidad();
            }
        }
        return cantidad;
    }

    private boolean contieneProducto(Pedido pedido, Producto producto) {
        for (ItemPedido item : pedido.getItems().obtenerCopia()) {
            if (item.getProducto() == producto) return true;
        }
        return false;
    }

    public List<Pedido> getPedidosPendientes() {
        return colaPedidos.obtenerCopia();
    }

    public List<Operacion> getHistorial() {
        return historial.obtenerCopia();
    }

    public List<Pedido> getPedidosDeUsuario(String nombreUsuario) {
        List<Pedido> pedidosUsuario = new ArrayList<>();
        for (Pedido pedido : todosLosPedidos) {
            if (pedido.getNombreUsuario().equals(nombreUsuario)) pedidosUsuario.add(pedido);
        }
        return pedidosUsuario;
    }

    public void cargarPedidos(List<Producto> productosCatalogo, boolean inventarioGuardado) {
        todosLosPedidos.clear();
        int mayorId = 0;
        if (!Files.exists(ARCHIVO_PEDIDOS)) return;
        try {
            for (String linea : Files.readAllLines(ARCHIVO_PEDIDOS, StandardCharsets.UTF_8)) {
                try {
                    Pedido pedido = leerPedido(linea, productosCatalogo);
                    if (pedido == null) continue;
                    todosLosPedidos.add(pedido);
                    mayorId = Math.max(mayorId, pedido.getId());
                    if (pedido.getEstado() == EstadoPedido.PENDIENTE) {
                        colaPedidos.encolar(pedido);
                    } else {
                        historial.apilar(new Operacion(pedido));
                        if (!inventarioGuardado) {
                            for (ItemPedido item : pedido.getItems().obtenerCopia()) {
                                item.getProducto().setStock(Math.max(0,
                                        item.getProducto().getStock() - item.getCantidad()));
                            }
                        }
                    }
                } catch (RuntimeException ex) {
                    System.err.println("Se omitio un pedido guardado invalido: " + ex.getMessage());
                }
            }
            siguienteId = mayorId + 1;
            if (!inventarioGuardado && mayorId > 0) guardarInventario.run();
        } catch (IOException ex) {
            System.err.println("No se pudo leer pedidos.txt: " + ex.getMessage());
        }
    }

    public void setGuardarInventario(Runnable guardarInventario) {
        this.guardarInventario = guardarInventario == null ? () -> { } : guardarInventario;
    }

    private Pedido leerPedido(String linea, List<Producto> productosCatalogo) {
        String[] campos = linea.split("\\|", -1);
        if (campos.length != 12) return null;
        int id = Integer.parseInt(campos[0]);
        String usuario = decodificar(campos[1]);
        EstadoPedido estado = EstadoPedido.valueOf(campos[2]);
        LocalDateTime fecha = LocalDateTime.parse(campos[3]);
        DatosCompra datos = new DatosCompra(decodificar(campos[4]), decodificar(campos[5]),
                decodificar(campos[6]), decodificar(campos[7]), decodificar(campos[8]),
                decodificar(campos[9]), com.proyecto.modelo.MetodoPago.valueOf(campos[10]));
        ListaSimpleEnlazada<ItemPedido> items = new ListaSimpleEnlazada<>();
        if (!campos[11].isBlank()) {
            for (String itemTexto : campos[11].split(";")) {
                String[] partes = itemTexto.split(",", -1);
                if (partes.length != 6 && partes.length != 7) continue;
                int productoId = Integer.parseInt(partes[0]);
                String nombre = decodificar(partes[1]);
                String descripcion = decodificar(partes[2]);
                double precio = Double.parseDouble(partes[3]);
                String imagen = decodificar(partes[4]);
                int cantidad = Integer.parseInt(partes[5]);
                String talla = partes.length == 7 ? decodificar(partes[6]) : "No especificada";
                Producto producto = productosCatalogo.stream().filter(p -> p.getId() == productoId)
                        .findFirst().orElseGet(() -> new Producto(productoId, nombre, descripcion,
                                precio, 0, imagen));
                items.agregar(new ItemPedido(producto, cantidad, precio, talla));
            }
        }
        if (items.estaVacia()) return null;
        return new Pedido(id, usuario, datos, items, fecha, estado);
    }

    private void guardarPedidos() {
        List<String> lineas = new ArrayList<>();
        for (Pedido pedido : todosLosPedidos) {
            DatosCompra datos = pedido.getDatosCompra();
            List<String> items = new ArrayList<>();
            for (ItemPedido item : pedido.getItems().obtenerCopia()) {
                Producto producto = item.getProducto();
                items.add(producto.getId() + "," + codificar(producto.getNombre()) + ","
                        + codificar(producto.getDescripcion()) + "," + item.getPrecioUnitario() + ","
                        + codificar(producto.getImagenArchivo()) + "," + item.getCantidad() + ","
                        + codificar(item.getTalla()));
            }
            lineas.add(pedido.getId() + "|" + codificar(pedido.getNombreUsuario()) + "|"
                    + pedido.getEstado().name() + "|" + pedido.getFechaHora() + "|"
                    + codificar(datos.getNombreCompleto()) + "|" + codificar(datos.getDocumento()) + "|"
                    + codificar(datos.getTelefono()) + "|" + codificar(datos.getCorreo()) + "|"
                    + codificar(datos.getDireccion()) + "|" + codificar(datos.getCiudad()) + "|"
                    + datos.getMetodoPago().name() + "|" + String.join(";", items));
        }
        Path temporal = ARCHIVO_PEDIDOS.resolveSibling(ARCHIVO_PEDIDOS.getFileName() + ".tmp");
        try {
            Files.write(temporal, lineas, StandardCharsets.UTF_8);
            try {
                Files.move(temporal, ARCHIVO_PEDIDOS, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temporal, ARCHIVO_PEDIDOS, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudieron guardar las compras en pedidos.txt: "
                    + ex.getMessage(), ex);
        }
    }

    private String codificar(String texto) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(texto.getBytes(StandardCharsets.UTF_8));
    }

    private String decodificar(String texto) {
        return new String(Base64.getUrlDecoder().decode(texto), StandardCharsets.UTF_8);
    }
}
