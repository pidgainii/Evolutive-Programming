package practica.real;

import java.util.Random;
import practica.ast.*;
import practica.enums.Accion;

public class Crossover {

    public static Chromosome[] cross(Chromosome parent1, Chromosome parent2, Random rand) {
        // Hacemos copia profunda para no destrozar a los padres originales
        NodoAST tree1 = parent1.getTree().deepCopy();
        NodoAST tree2 = parent2.getTree().deepCopy();

        int size1 = tree1.tam();
        int size2 = tree2.tam();

        // Selección de puntos de cruce UNIFORME (como antes)
        int point1 = rand.nextInt(size1);
        int point2 = rand.nextInt(size2);

        // Extraemos los subárboles
        NodoAST subTree1 = tree1.getSubtree(new int[]{0}, point1);
        NodoAST subTree2 = tree2.getSubtree(new int[]{0}, point2);

        // Intercambiamos
        NodoAST childTree1 = tree1.replaceSubtree(new int[]{0}, point1, subTree2);
        NodoAST childTree2 = tree2.replaceSubtree(new int[]{0}, point2, subTree1);

        // --- SISTEMA DE PODADO ---
        if (childTree1.profundidad() > 10) {
            childTree1 = podar(childTree1, 1, 10, rand);
        }
        if (childTree2.profundidad() > 10) {
            childTree2 = podar(childTree2, 1, 10, rand);
        }

        return new Chromosome[]{
            new Chromosome(childTree1), 
            new Chromosome(childTree2)
        };
    }

    /**
     * Recorre el árbol y sustituye cualquier nodo en el límite por una Acción simple.
     */
    private static NodoAST podar(NodoAST nodo, int profActual, int profMax, Random rand) {
        // Si hemos llegado al límite de profundidad
        if (profActual >= profMax) {
            // Si el nodo actual no es una hoja (es un bloque o condición), 
            // lo "podamos" convirtiéndolo en una acción aleatoria.
            if (!nodo.isLeaf()) {
                Accion[] acciones = Accion.values();
                return new NodoAccion(acciones[rand.nextInt(acciones.length)]);
            }
            return nodo; // Si ya era una hoja, la dejamos tal cual
        }

        // Si no estamos en el límite, seguimos bajando recursivamente
        if (nodo instanceof NodoBloque) {
            NodoBloque bloque = (NodoBloque) nodo;
            NodoAST[] hijos = bloque.getHijos();
            for (int i = 0; i < hijos.length; i++) {
                hijos[i] = podar(hijos[i], profActual + 1, profMax, rand);
            }
            return new NodoBloque(hijos);
        } else if (nodo instanceof NodoCondicional) {
            NodoCondicional cond = (NodoCondicional) nodo;
            NodoAST izq = podar(cond.getHijoIzquierdo(), profActual + 1, profMax, rand);
            NodoAST der = podar(cond.getHijoDerecho(), profActual + 1, profMax, rand);
            return new NodoCondicional(cond.getSensor(), cond.getUmbral(), izq, der);
        }

        return nodo;
    }
}