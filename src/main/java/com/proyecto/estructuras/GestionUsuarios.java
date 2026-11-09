package com.proyecto.estructuras;

import com.proyecto.modelo.Rol;
import com.proyecto.modelo.Usuario;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class GestionUsuarios {
    private static final Path ARCHIVO = Path.of("usuarios.txt");
    private static final String SEPARADOR = ";";
    private static final int LARGO_MINIMO_USUARIO = 3;
    private static final int LARGO_MINIMO_CONTRASENA = 4;

    private final List<Usuario> usuarios = new ArrayList<>();

    public void cargarUsuarios() {
        usuarios.clear();
        cargarAdministradores();
        cargarClientesGuardados();
    }


    public Usuario registrarCliente(String nombreUsuario, String contrasena) {
        String nombre = nombreUsuario == null ? "" : nombreUsuario.trim();
        if (nombre.length() < LARGO_MINIMO_USUARIO) {
            throw new IllegalArgumentException("El usuario debe tener al menos 3 caracteres.");
        }
        if (nombre.contains(SEPARADOR)) {
            throw new IllegalArgumentException("El usuario no puede contener el simbolo ;.");
        }
        if (contrasena == null || contrasena.length() < LARGO_MINIMO_CONTRASENA) {
            throw new IllegalArgumentException("La contrasena debe tener al menos 4 caracteres.");
        }
        if (existe(nombre)) {
            throw new IllegalArgumentException("El usuario " + nombre + " ya existe. Elige otro.");
        }

        Usuario nuevo = new Usuario(nombre, contrasena, Rol.CLIENTE);
        usuarios.add(nuevo);
        try {
            guardarClientes();
        } catch (IllegalStateException e) {
            usuarios.remove(nuevo);
            throw e;
        }
        return nuevo;
    }

    public Usuario autenticar(String nombreUsuario, String contrasena) {
        if (nombreUsuario == null || contrasena == null) {
            return null;
        }
        for (Usuario usuario : usuarios) {
            if (usuario.coincideCon(nombreUsuario.trim(), contrasena)) {
                return usuario;
            }
        }
        return null;
    }

    public boolean existe(String nombreUsuario) {
        if (nombreUsuario == null) {
            return false;
        }
        for (Usuario usuario : usuarios) {
            if (usuario.getNombreUsuario().equalsIgnoreCase(nombreUsuario.trim())) {
                return true;
            }
        }
        return false;
    }

    private void cargarAdministradores() {
        usuarios.add(new Usuario("admin", "admin123", Rol.ADMINISTRADOR));
    }

    private void cargarClientesGuardados() {
        if (!Files.exists(ARCHIVO)) {
            usuarios.add(new Usuario("cliente", "cliente123", Rol.CLIENTE));
            try {
                guardarClientes();
            } catch (IllegalStateException e) {
                System.err.println(e.getMessage());
            }
            return;
        }

        try {
            for (String linea : Files.readAllLines(ARCHIVO, StandardCharsets.UTF_8)) {
                String[] partes = linea.split(SEPARADOR, 2);
                if (partes.length != 2) {
                    continue;
                }
                String nombre = partes[0].trim();
                String contrasena = partes[1];
                if (nombre.length() < LARGO_MINIMO_USUARIO
                        || contrasena.length() < LARGO_MINIMO_CONTRASENA
                        || existe(nombre)) {
                    continue;
                }
                usuarios.add(new Usuario(nombre, contrasena, Rol.CLIENTE));
            }
        } catch (IOException e) {
            System.err.println("No se pudo leer " + ARCHIVO + ": " + e.getMessage());
        }
    }

    private void guardarClientes() {
        List<String> lineas = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (!usuario.esAdministrador()) {
                lineas.add(usuario.getNombreUsuario() + SEPARADOR + usuario.getContrasena());
            }
        }
        try {
            Files.write(ARCHIVO, lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar usuarios.txt: " + e.getMessage(), e);
        }
    }
}
