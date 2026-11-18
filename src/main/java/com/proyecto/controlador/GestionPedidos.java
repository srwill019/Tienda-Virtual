package com.proyecto.controlador;

import com.proyecto.modelo.Operacion;
import com.proyecto.modelo.Pedido;
import com.proyecto.modelo.Producto;
import com.proyecto.estructuras.ColaEnlazada;
import com.proyecto.estructuras.PilaEnlazada;

import java.util.List;

public class GestionPedidos {
    private final ColaEnlazada<Pedido> colaPedidos = new ColaEnlazada<>();
    private final PilaEnlazada<Operacion> historial = new PilaEnlazada<>();
    private int siguienteId = 1;

    public Pedido agregarPedido(Producto producto) {
        long pedidosDelProducto = colaPedidos.obtenerCopia().stream()
                .filter(pedido -> pedido.getProducto() == producto)
                .count();
        if (pedidosDelProducto >= producto.getStock()) {
            return null;
        }

        Pedido pedido = new Pedido(siguienteId++, producto);
        colaPedidos.encolar(pedido);
        return pedido;
    }

    public Pedido procesarSiguiente() {
        Pedido siguiente = colaPedidos.verFrente();
        if (siguiente == null || siguiente.getProducto().getStock() <= 0) {
            return null;
        }

        Pedido procesado = colaPedidos.desencolar();
        procesado.getProducto().setStock(procesado.getProducto().getStock() - 1);
        historial.apilar(new Operacion(procesado));
        return procesado;
    }

    public Pedido deshacerUltimoProceso() {
        if (historial.estaVacia()) {
            return null;
        }

        Pedido deshecho = historial.desapilar().getPedido();
        deshecho.getProducto().setStock(deshecho.getProducto().getStock() + 1);
        colaPedidos.encolarAlFrente(deshecho);
        return deshecho;
    }

    public List<Pedido> getPedidosPendientes() {
        return colaPedidos.obtenerCopia();
    }

    public List<Operacion> getHistorial() {
        return historial.obtenerCopia();
    }
}
