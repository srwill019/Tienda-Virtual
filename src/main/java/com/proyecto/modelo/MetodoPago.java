package com.proyecto.modelo;

public enum MetodoPago {
    CONTRA_ENTREGA("Contra entrega"),
    TRANSFERENCIA("Transferencia simulada");

    private final String etiqueta;

    MetodoPago(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
