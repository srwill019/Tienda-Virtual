package com.proyecto.estructuras;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/** Lista doble circular: el último enlaza con el primero en ambos sentidos. */
public class ListaDobleCircular<T> {
    private static final class NodoDoble<E> {
        private final E dato;
        private NodoDoble<E> siguiente;
        private NodoDoble<E> anterior;

        private NodoDoble(E dato) {
            this.dato = dato;
        }
    }

    private NodoDoble<T> primero;
    private int tamano;

    /** Enlaza el nodo nuevo entre el último y el primero, cerrando el círculo. */
    public void agregar(T dato) {
        NodoDoble<T> nuevo = new NodoDoble<>(dato);
        if (primero == null) {
            primero = nuevo;
            nuevo.siguiente = nuevo;
            nuevo.anterior = nuevo;
        } else {
            NodoDoble<T> ultimo = primero.anterior;
            nuevo.anterior = ultimo;
            nuevo.siguiente = primero;
            ultimo.siguiente = nuevo;
            primero.anterior = nuevo;
        }
        tamano++;
    }

    public boolean estaVacia() {
        return tamano == 0;
    }

    public int tamano() {
        return tamano;
    }


    public List<T> obtenerCopia() {
        List<T> copia = new ArrayList<>(tamano);
        if (primero == null) return copia;
        NodoDoble<T> nodo = primero;
        do {
            copia.add(nodo.dato);
            nodo = nodo.siguiente;
        } while (nodo != primero);
        return copia;
    }

    /** Devuelve los elementos que cumplen el criterio, recorriendo una vez el circulo. */
    public List<T> buscar(Predicate<T> criterio) {
        List<T> coincidencias = new ArrayList<>();
        if (primero == null) return coincidencias;
        NodoDoble<T> nodo = primero;
        do {
            if (criterio.test(nodo.dato)) {
                coincidencias.add(nodo.dato);
            }
            nodo = nodo.siguiente;
        } while (nodo != primero);
        return coincidencias;
    }

    /** Elimina la primera coincidencia y conserva el cierre circular en ambos sentidos. */
    public boolean eliminar(T dato) {
        if (primero == null) return false;
        NodoDoble<T> actual = primero;
        do {
            if (Objects.equals(actual.dato, dato)) {
                if (tamano == 1) {
                    primero = null;
                } else {
                    actual.anterior.siguiente = actual.siguiente;
                    actual.siguiente.anterior = actual.anterior;
                    if (actual == primero) {
                        primero = actual.siguiente;
                    }
                }
                tamano--;
                return true;
            }
            actual = actual.siguiente;
        } while (actual != primero);
        return false;
    }
}
