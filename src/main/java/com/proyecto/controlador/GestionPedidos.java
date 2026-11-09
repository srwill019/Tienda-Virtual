package com.proyecto.controlador;

import com.proyecto.modelo.Operacion;
import com.proyecto.modelo.Pedido;
import com.proyecto.modelo.Producto;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GestionPedidos {
    private final LinkedList<Pedido> pedidosEnlazados = new LinkedList<>();
    private final Queue<Pedido> colaPedidos = pedidosEnlazados;
    private final Deque<Operacion> historial = new ArrayDeque<>();
    private int siguienteId = 1;

    public Pedido agregarPedido(Producto producto) {
        long pedidosDelProducto = colaPedidos.stream()
                .filter(pedido -> pedido.getProducto() == producto)
                .count();
        if (pedidosDelProducto >= producto.getStock()) {
            return null;
        }

        Pedido pedido = new Pedido(siguienteId++, producto);
        colaPedidos.offer(pedido);
        return pedido;
    }

    public Pedido procesarSiguiente() {
        Pedido siguiente = colaPedidos.peek();
        if (siguiente == null || siguiente.getProducto().getStock() <= 0) {
            return null;
        }

        Pedido procesado = colaPedidos.poll();
        procesado.getProducto().setStock(procesado.getProducto().getStock() - 1);
        historial.push(new Operacion(procesado));
        return procesado;
    }

    public Pedido deshacerUltimoProceso() {
        if (historial.isEmpty()) {
            return null;
        }

        Pedido deshecho = historial.pop().getPedido();
        deshecho.getProducto().setStock(deshecho.getProducto().getStock() + 1);
        pedidosEnlazados.addFirst(deshecho);
        return deshecho;
    }

    public List<Pedido> getPedidosPendientes() {
        return new ArrayList<>(colaPedidos);
    }

    public List<Operacion> getHistorial() {
        return new ArrayList<>(historial);
    }
}
