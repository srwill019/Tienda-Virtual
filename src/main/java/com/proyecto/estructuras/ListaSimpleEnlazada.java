package com.proyecto.estructuras;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Lista simplemente enlazada propia, recorrida desde la cabeza. */
public class ListaSimpleEnlazada<T> {
    private Nodo<T> cabeza;
    private Nodo<T> fin;
    private int tamano;

    /** Agrega al final enlazando el último nodo con el nuevo. */
    public void agregar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            fin.setSiguiente(nuevo);
        }
        fin = nuevo;
        tamano++;
    }

    /** Agrega solo si no existe un dato igual; útil para favoritos únicos. */
    public boolean agregarSiNoExiste(T dato) {
        if (contiene(dato)) {
            return false;
        }
        agregar(dato);
        return true;
    }

    public boolean contiene(T dato) {
        for (Nodo<T> actual = cabeza; actual != null; actual = actual.getSiguiente()) {
            if (Objects.equals(actual.getDato(), dato)) {
                return true;
            }
        }
        return false;
    }

    /** Desenlaza la primera coincidencia y repara cabeza/final según corresponda. */
    public boolean eliminar(T dato) {
        Nodo<T> anterior = null;
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (Objects.equals(actual.getDato(), dato)) {
                if (anterior == null) {
                    cabeza = actual.getSiguiente();
                } else {
                    anterior.setSiguiente(actual.getSiguiente());
                }
                if (actual == fin) {
                    fin = anterior;
                }
                tamano--;
                return true;
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }
        return false;
    }

    public boolean estaVacia() {
        return tamano == 0;
    }

    public int tamano() {
        return tamano;
    }

    /** Entrega una copia en orden sin exponer referencias a los nodos. */
    public List<T> obtenerCopia() {
        List<T> copia = new ArrayList<>(tamano);
        for (Nodo<T> actual = cabeza; actual != null; actual = actual.getSiguiente()) {
            copia.add(actual.getDato());
        }
        return copia;
    }
}
