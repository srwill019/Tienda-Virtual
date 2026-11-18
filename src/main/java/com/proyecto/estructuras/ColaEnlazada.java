package com.proyecto.estructuras;

import java.util.ArrayList;
import java.util.List;

/** Cola FIFO implementada con nodos propios. */
public class ColaEnlazada<T> {
    private Nodo<T> frente;
    private Nodo<T> fin;
    private int tamano;

    /** Enlaza el nodo nuevo después del final actual, en O(1). */
    public void encolar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            frente = nuevo;
        } else {
            fin.setSiguiente(nuevo);
        }
        fin = nuevo;
        tamano++;
    }

    /** Retira el frente y avanza el enlace al siguiente nodo. */
    public T desencolar() {
        if (estaVacia()) {
            throw new IllegalStateException("No se puede desencolar: la cola está vacía.");
        }
        T dato = frente.getDato();
        frente = frente.getSiguiente();
        tamano--;
        if (frente == null) {
            fin = null;
        }
        return dato;
    }

    public T verFrente() {
        return estaVacia() ? null : frente.getDato();
    }

    public boolean estaVacia() {
        return tamano == 0;
    }

    public int tamano() {
        return tamano;
    }

    /** Copia los datos desde el frente hasta el final, sin exponer nodos. */
    public List<T> obtenerCopia() {
        List<T> copia = new ArrayList<>(tamano);
        for (Nodo<T> actual = frente; actual != null; actual = actual.getSiguiente()) {
            copia.add(actual.getDato());
        }
        return copia;
    }

    /** Inserta al frente para conservar el comportamiento de deshacer pedidos. */
    public void encolarAlFrente(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(frente);
        frente = nuevo;
        if (fin == null) {
            fin = nuevo;
        }
        tamano++;
    }
}
