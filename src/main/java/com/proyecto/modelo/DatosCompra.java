package com.proyecto.modelo;

public class DatosCompra {
    private final String nombreCompleto;
    private final String documento;
    private final String telefono;
    private final String correo;
    private final String direccion;
    private final String ciudad;
    private final MetodoPago metodoPago;

    public DatosCompra(String nombreCompleto, String documento, String telefono, String correo,
                       String direccion, String ciudad, MetodoPago metodoPago) {
        this.nombreCompleto = obligatorio(nombreCompleto, "El nombre completo es obligatorio.");
        this.documento = obligatorio(documento, "El documento es obligatorio.");
        if (!this.documento.matches("\\d+")) {
            throw new IllegalArgumentException("El documento debe contener solo digitos.");
        }
        this.telefono = obligatorio(telefono, "El telefono es obligatorio.");
        if (!this.telefono.matches("\\d{7,10}")) {
            throw new IllegalArgumentException("El telefono debe tener de 7 a 10 digitos.");
        }
        this.correo = obligatorio(correo, "El correo es obligatorio.");
        if (!this.correo.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new IllegalArgumentException("Escribe un correo valido, por ejemplo nombre@correo.com.");
        }
        this.direccion = obligatorio(direccion, "La direccion es obligatoria.");
        if (this.direccion.length() < 5) {
            throw new IllegalArgumentException("La direccion debe tener al menos 5 caracteres.");
        }
        this.ciudad = obligatorio(ciudad, "La ciudad es obligatoria.");
        if (metodoPago == null) {
            throw new IllegalArgumentException("Selecciona un metodo de pago.");
        }
        this.metodoPago = metodoPago;
    }

    private String obligatorio(String valor, String mensaje) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(mensaje);
        }
        return valor.trim();
    }

    public String getNombreCompleto() { return nombreCompleto; }
    public String getDocumento() { return documento; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public String getDireccion() { return direccion; }
    public String getCiudad() { return ciudad; }
    public MetodoPago getMetodoPago() { return metodoPago; }
}
