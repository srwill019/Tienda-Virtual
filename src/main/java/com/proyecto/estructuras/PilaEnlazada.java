package com.proyecto.estructuras;

import java.util.ArrayList;
import java.util.List;

/** Pila LIFO implementada con nodos propios. */
public class PilaEnlazada<T> {
    private Nodo<T> tope;
    private int tamano;

    /** El nuevo nodo apunta al tope anterior y pasa a ser el tope. */
    public void apilar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(tope);
        tope = nuevo;
        tamano++;
    }

    /** Retira el tope y mueve la referencia al nodo que estaba debajo. */
    public T desapilar() {
        if (estaVacia()) {
            throw new IllegalStateException("No se puede desapilar: la pila está vacía.");
        }
        T dato = tope.getDato();
        tope = tope.getSiguiente();
        tamano--;
        return dato;
    }

    public T verTope() {
        return estaVacia() ? null : tope.getDato();
    }

    public boolean estaVacia() {
        return tamano == 0;
    }

    public int tamano() {
        return tamano;
    }

    /** Copia los datos desde el tope hacia abajo, en orden LIFO. */
    public List<T> obtenerCopia() {
        List<T> copia = new ArrayList<>(tamano);
        for (Nodo<T> actual = tope; actual != null; actual = actual.getSiguiente()) {
            copia.add(actual.getDato());
        }
        return copia;
    }
}
