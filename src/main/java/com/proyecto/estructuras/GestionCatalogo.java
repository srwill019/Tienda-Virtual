package com.proyecto.estructuras;

import com.proyecto.modelo.Producto;

import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.AtomicMoveNotSupportedException;
import java.io.IOException;

public class GestionCatalogo {
    private static final Path ARCHIVO_PRODUCTOS = Path.of("productos.txt");
    private final ListaDobleCircular<Producto> productos = new ListaDobleCircular<>();
    private boolean inventarioGuardadoAlIniciar;

    public void cargarDatosPrueba() {
        if (!productos.estaVacia()) {
            return;
        }
        inventarioGuardadoAlIniciar = Files.exists(ARCHIVO_PRODUCTOS);
        if (inventarioGuardadoAlIniciar) {
            cargarProductosGuardados();
            return;
        }
        productos.agregar(new Producto(1, "Buzo verde",
                "Buzo verde de manga larga, comodo para un estilo casual en dias frescos.",
                69900, 10, "buzo-verde.png"));
        productos.agregar(new Producto(2, "Camiseta azul oscuro",
                "Camiseta de manga corta y cuello redondo, facil de combinar.",
                29900, 12, "camiseta-azul-oscuro.png"));
        productos.agregar(new Producto(3, "Camiseta roja",
                "Camiseta roja de manga corta que aporta color a tu estilo diario.",
                29900, 12, "camiseta-roja.png"));
        productos.agregar(new Producto(4, "Camiseta negra",
                "Camiseta negra de corte clasico, versatil para distintos looks.",
                29900, 12, "camiseta-negra.png"));
        productos.agregar(new Producto(5, "Camiseta blanca",
                "Camiseta blanca de corte clasico, una prenda esencial para cada dia.",
                29900, 12, "camiseta-blanca.png"));
        productos.agregar(new Producto(6, "Jean holgado azul",
                "Jean azul de corte holgado y estilo relajado para mayor comodidad.",
                79900, 8, "jean-holgado-azul.png"));
        productos.agregar(new Producto(7, "Jean holgado negro",
                "Jean de tono oscuro y corte holgado, facil de combinar con camisetas.",
                79900, 8, "jean-holgado-negro.png"));
        productos.agregar(new Producto(8, "Gorra de beisbol",
                "Gorra negra con visera plana y bordado decorativo para un look urbano.",
                49900, 10, "gorra-beisbol.png"));
        guardarProductos();
    }

    public void agregarProducto(Producto producto) {
        productos.agregar(producto);
        guardarProductos();
    }

    public int siguienteId() {
        return productos.obtenerCopia().stream().mapToInt(Producto::getId).max().orElse(0) + 1;
    }

    public boolean actualizarStock(int id, int nuevoStock) {
        if (nuevoStock < 0) {
            return false;
        }
        for (Producto producto : productos.obtenerCopia()) {
            if (producto.getId() == id) {
                producto.setStock(nuevoStock);
                guardarProductos();
                return true;
            }
        }
        return false;
    }

    public boolean modificar(int id, String nombre, String descripcion, double precio,
                             int stock, String imagenArchivo) {
        Producto producto = buscarPorId(id);
        if (producto == null) return false;
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio.");
        }
        if (!Double.isFinite(precio) || precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser finito y mayor que cero.");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setImagenArchivo(imagenArchivo);
        guardarProductos();
        return true;
    }

    public boolean eliminar(int id) {
        Producto producto = buscarPorId(id);
        boolean eliminado = producto != null && productos.eliminar(producto);
        if (eliminado) guardarProductos();
        return eliminado;
    }

    private Producto buscarPorId(int id) {
        for (Producto producto : productos.obtenerCopia()) {
            if (producto.getId() == id) return producto;
        }
        return null;
    }

    public List<Producto> getProductos() {
        return productos.obtenerCopia();
    }

    public List<Producto> buscar(String texto) {
        String consulta = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
        if (consulta.isEmpty()) {
            return productos.obtenerCopia();
        }
        return productos.buscar(producto -> producto.getNombre().toLowerCase(Locale.ROOT).contains(consulta)
                || String.valueOf(producto.getId()).contains(consulta));
    }

    public int total() {
        return productos.tamano();
    }

    public boolean habiaInventarioGuardadoAlIniciar() {
        return inventarioGuardadoAlIniciar;
    }

    public void guardarProductos() {
        List<String> lineas = new ArrayList<>();
        for (Producto producto : productos.obtenerCopia()) {
            lineas.add(producto.getId() + "|" + codificar(producto.getNombre()) + "|"
                    + codificar(producto.getDescripcion()) + "|" + producto.getPrecio() + "|"
                    + producto.getStock() + "|" + codificar(producto.getImagenArchivo()));
        }
        Path temporal = ARCHIVO_PRODUCTOS.resolveSibling(ARCHIVO_PRODUCTOS.getFileName() + ".tmp");
        try {
            Files.write(temporal, lineas, StandardCharsets.UTF_8);
            moverArchivo(temporal, ARCHIVO_PRODUCTOS);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo guardar el catalogo en productos.txt: "
                    + ex.getMessage(), ex);
        }
    }

    private void cargarProductosGuardados() {
        try {
            for (String linea : Files.readAllLines(ARCHIVO_PRODUCTOS, StandardCharsets.UTF_8)) {
                try {
                    String[] campos = linea.split("\\|", -1);
                    if (campos.length != 6) continue;
                    productos.agregar(new Producto(Integer.parseInt(campos[0]), decodificar(campos[1]),
                            decodificar(campos[2]), Double.parseDouble(campos[3]),
                            Integer.parseInt(campos[4]), decodificar(campos[5])));
                } catch (RuntimeException ex) {
                    System.err.println("Se omitio un producto guardado invalido: " + ex.getMessage());
                }
            }
            if (productos.estaVacia()) {
                System.err.println("productos.txt no contiene productos validos.");
            }
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo leer productos.txt: " + ex.getMessage(), ex);
        }
    }

    private String codificar(String texto) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(texto.getBytes(StandardCharsets.UTF_8));
    }

    private String decodificar(String texto) {
        return new String(Base64.getUrlDecoder().decode(texto), StandardCharsets.UTF_8);
    }

    private void moverArchivo(Path temporal, Path destino) throws IOException {
        try {
            Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
