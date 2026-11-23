package com.proyecto.modelo;

import com.proyecto.estructuras.ListaSimpleEnlazada;

import java.time.LocalDateTime;

public class Pedido {
    private final int id;
    private final String nombreUsuario;
    private final DatosCompra datosCompra;
    private final ListaSimpleEnlazada<ItemPedido> items;
    private final double total;
    private final LocalDateTime fechaHora;
    private EstadoPedido estado;

    public Pedido(int id, String nombreUsuario, DatosCompra datosCompra, ListaSimpleEnlazada<ItemPedido> items) {
        this(id, nombreUsuario, datosCompra, items, LocalDateTime.now(), EstadoPedido.PENDIENTE);
    }

    public Pedido(int id, String nombreUsuario, DatosCompra datosCompra, ListaSimpleEnlazada<ItemPedido> items,
                  LocalDateTime fechaHora, EstadoPedido estado) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.datosCompra = datosCompra;
        this.items = new ListaSimpleEnlazada<>();
        double suma = 0;
        for (ItemPedido item : items.obtenerCopia()) {
            this.items.agregar(item);
            suma += item.getSubtotal();
        }
        this.total = suma;
        this.fechaHora = fechaHora;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public String getNombreUsuario() { return nombreUsuario; }
    public DatosCompra getDatosCompra() { return datosCompra; }
    public ListaSimpleEnlazada<ItemPedido> getItems() { return items; }
    public double getTotal() { return total; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Pedido #" + id + " - " + nombreUsuario;
    }
}
