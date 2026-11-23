package com.proyecto.modelo;

public class Producto {
    private final int id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;
    private String imagenArchivo;

    public Producto(int id, String nombre, double precio, int stock) {
        this(id, nombre, "", precio, stock, "");
    }

    public Producto(int id, String nombre, String descripcion, double precio, int stock, String imagenArchivo) {
        this.id = id;
        setNombre(nombre);
        setDescripcion(descripcion);
        setPrecio(precio);
        setStock(stock);
        setImagenArchivo(imagenArchivo);
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
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        this.stock = stock;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio.");
        }
        this.nombre = nombre.trim();
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion == null ? "" : descripcion.trim();
    }

    public void setPrecio(double precio) {
        if (!Double.isFinite(precio) || precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser finito y mayor que cero.");
        }
        this.precio = precio;
    }

    public void setImagenArchivo(String imagenArchivo) {
        this.imagenArchivo = imagenArchivo == null ? "" : imagenArchivo.trim();
    }

    @Override
    public String toString() {
        return "Producto " + id + ": " + nombre + " | Precio: $" + precio + " | Stock: " + stock;
    }
}
