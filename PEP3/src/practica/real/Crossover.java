package practica.real;

import java.util.Random;
import practica.ast.*;
import practica.enums.Accion;

public class Crossover {

    public static Chromosome[] cross(Chromosome parent1, Chromosome parent2, Random rand, boolean poda, int profPoda) {
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

        if (poda) {
        	// --- SISTEMA DE PODADO ---
            if (childTree1.profundidad() > profPoda) {
                childTree1 = Utils.podar(childTree1, 1, profPoda, rand);
            }
            if (childTree2.profundidad() > profPoda) {
                childTree2 = Utils.podar(childTree2, 1, profPoda, rand);
            }
        }
        

        return new Chromosome[]{
            new Chromosome(childTree1), 
            new Chromosome(childTree2)
        };
    }

}