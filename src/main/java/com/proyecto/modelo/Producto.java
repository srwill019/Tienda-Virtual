package com.proyecto.modelo;

public class Producto {
    private final int id;
    private final String nombre;
    private final String descripcion;
    private final double precio;
    private int stock;
    private final String imagenArchivo;

    public Producto(int id, String nombre, double precio, int stock) {
        this(id, nombre, "", precio, stock, "");
    }

    public Producto(int id, String nombre, String descripcion, double precio, int stock, String imagenArchivo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.imagenArchivo = imagenArchivo;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    public String getImagenArchivo() {
        return imagenArchivo;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "Producto " + id + ": " + nombre + " | Precio: $" + precio + " | Stock: " + stock;
    }
}
