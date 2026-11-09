package com.proyecto.modelo;

public class Operacion {
    private final Pedido pedido;

    public Operacion(Pedido pedido) {
        this.pedido = pedido;
    }

    public Pedido getPedido() {
        return pedido;
    }

    @Override
    public String toString() {
        return "Procesado: " + pedido;
    }
}
