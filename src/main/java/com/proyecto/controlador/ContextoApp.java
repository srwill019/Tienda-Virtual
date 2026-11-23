package com.proyecto.controlador;

import com.proyecto.estructuras.GestionCatalogo;
import com.proyecto.estructuras.GestionUsuarios;
import com.proyecto.estructuras.ListaSimpleEnlazada;
import com.proyecto.controlador.GestionPedidos;
import com.proyecto.modelo.Usuario;
import com.proyecto.modelo.ItemCarrito;
import com.proyecto.modelo.Producto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


/** Datos compartidos por las pantallas durante la ejecucion de la aplicacion. */
public class ContextoApp {
    private static final Path ARCHIVO_PREFERENCIAS = Path.of("preferencias_clientes.txt");
    private final GestionCatalogo catalogo = new GestionCatalogo();
    private final GestionUsuarios gestionUsuarios = new GestionUsuarios();
    private final GestionPedidos gestionPedidos = new GestionPedidos();
    private final ListaSimpleEnlazada<ItemCarrito> carrito = new ListaSimpleEnlazada<>();
    private final ListaSimpleEnlazada<Integer> favoritos = new ListaSimpleEnlazada<>();
    private Usuario usuarioActual;

    public ContextoApp() {
        catalogo.cargarDatosPrueba();
        gestionUsuarios.cargarUsuarios();
        gestionPedidos.setGuardarInventario(catalogo::guardarProductos);
        gestionPedidos.cargarPedidos(catalogo.getProductos(), catalogo.habiaInventarioGuardadoAlIniciar());
    }

    public GestionCatalogo getCatalogo() {
        return catalogo;
    }

    public GestionUsuarios getGestionUsuarios() {
        return gestionUsuarios;
    }

    public GestionPedidos getGestionPedidos() {
        return gestionPedidos;
    }

    public ListaSimpleEnlazada<ItemCarrito> getCarrito() {
        return carrito;
    }

    public ListaSimpleEnlazada<Integer> getFavoritos() {
        return favoritos;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(Usuario usuarioActual) {
        if (this.usuarioActual != null && usuarioActual != null
                && !this.usuarioActual.getNombreUsuario().equalsIgnoreCase(usuarioActual.getNombreUsuario())) {
            guardarPreferenciasUsuarioActual();
        }
        if (this.usuarioActual == null || usuarioActual == null
                || !this.usuarioActual.getNombreUsuario().equalsIgnoreCase(usuarioActual.getNombreUsuario())) {
            limpiarPreferenciasEnMemoria();
        }
        this.usuarioActual = usuarioActual;
        if (usuarioActual != null) cargarPreferenciasUsuario(usuarioActual.getNombreUsuario());
    }

    public void cerrarSesion() {
        guardarPreferenciasUsuarioActual();
        limpiarPreferenciasEnMemoria();
        usuarioActual = null;
    }

    public void guardarDatosUsuarioActual() {
        guardarPreferenciasUsuarioActual();
    }

    private void cargarPreferenciasUsuario(String nombreUsuario) {
        if (!Files.exists(ARCHIVO_PREFERENCIAS)) return;
        try {
            for (String linea : Files.readAllLines(ARCHIVO_PREFERENCIAS, StandardCharsets.UTF_8)) {
                try {
                    String[] campos = linea.split("\\|", -1);
                    if (campos.length < 3 || !decodificar(campos[0]).equalsIgnoreCase(nombreUsuario)) continue;
                    if (campos[1].equals("C") && campos.length == 4) {
                        int productoId = Integer.parseInt(campos[2]);
                        String talla = decodificar(campos[3]);
                        buscarProducto(productoId).ifPresent(producto -> carrito.agregar(
                                new ItemCarrito(producto, talla)));
                    } else if (campos[1].equals("F")) {
                        favoritos.agregarSiNoExiste(Integer.parseInt(campos[2]));
                    }
                } catch (RuntimeException ex) {
                    System.err.println("Se omitio una preferencia guardada invalida: " + ex.getMessage());
                }
            }
        } catch (IOException ex) {
            System.err.println("No se pudieron leer las preferencias: " + ex.getMessage());
        }
    }

    private void guardarPreferenciasUsuarioActual() {
        if (usuarioActual == null) return;
        List<String> lineas = new ArrayList<>();
        if (Files.exists(ARCHIVO_PREFERENCIAS)) {
            try {
                for (String linea : Files.readAllLines(ARCHIVO_PREFERENCIAS, StandardCharsets.UTF_8)) {
                    String[] campos = linea.split("\\|", -1);
                    boolean perteneceAlUsuarioActual;
                    try {
                        perteneceAlUsuarioActual = campos.length > 0 && decodificar(campos[0])
                                .equalsIgnoreCase(usuarioActual.getNombreUsuario());
                    } catch (IllegalArgumentException ex) {
                        perteneceAlUsuarioActual = false;
                    }
                    if (!perteneceAlUsuarioActual) lineas.add(linea);
                }
            } catch (IOException ex) {
                System.err.println("No se pudieron conservar las preferencias de otros usuarios: " + ex.getMessage());
                return;
            }
        }
        String usuarioCodificado = codificar(usuarioActual.getNombreUsuario());
        for (ItemCarrito item : carrito.obtenerCopia()) {
            lineas.add(usuarioCodificado + "|C|" + item.getProducto().getId() + "|" + codificar(item.getTalla()));
        }
        for (Integer productoId : favoritos.obtenerCopia()) {
            lineas.add(usuarioCodificado + "|F|" + productoId);
        }
        Path temporal = ARCHIVO_PREFERENCIAS.resolveSibling(ARCHIVO_PREFERENCIAS.getFileName() + ".tmp");
        try {
            Files.write(temporal, lineas, StandardCharsets.UTF_8);
            try {
                Files.move(temporal, ARCHIVO_PREFERENCIAS, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temporal, ARCHIVO_PREFERENCIAS, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            System.err.println("No se pudieron guardar las preferencias: " + ex.getMessage());
        }
    }

    private java.util.Optional<Producto> buscarProducto(int id) {
        return catalogo.getProductos().stream().filter(producto -> producto.getId() == id).findFirst();
    }

    private void limpiarPreferenciasEnMemoria() {
        for (ItemCarrito item : carrito.obtenerCopia()) carrito.eliminar(item);
        for (Integer favorito : favoritos.obtenerCopia()) favoritos.eliminar(favorito);
    }

    private String codificar(String texto) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(texto.getBytes(StandardCharsets.UTF_8));
    }

    private String decodificar(String texto) {
        return new String(Base64.getUrlDecoder().decode(texto), StandardCharsets.UTF_8);
    }
}
