package com.proyecto.estructuras;

/** Nodo genérico que guarda un dato y el enlace al siguiente nodo. */
public class Nodo<T> {
    private final T dato;
    private Nodo<T> siguiente;

    public Nodo(T dato) {
        this.dato = dato;
    }

    public T getDato() {
        return dato;
    }

    public Nodo<T> getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Nodo<T> siguiente) {
        this.siguiente = siguiente;
    }
}
