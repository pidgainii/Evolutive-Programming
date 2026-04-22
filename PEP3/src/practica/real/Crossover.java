package practica.real;

import java.util.Random;
import practica.ast.NodoAST;

public class Crossover {

    public static Chromosome[] cross(Chromosome parent1, Chromosome parent2, Random rand) {
        NodoAST tree1 = parent1.getTree();
        NodoAST tree2 = parent2.getTree();

        int size1 = tree1.tam();
        int size2 = tree2.tam();

        // 1. Elegir puntos de cruce aleatorios (0 a tam - 1)
        int point1 = rand.nextInt(size1);
        int point2 = rand.nextInt(size2);

        // 2. Extraer los subárboles seleccionados
        NodoAST subTree1 = tree1.getSubtree(new int[]{0}, point1);
        NodoAST subTree2 = tree2.getSubtree(new int[]{0}, point2);

        // 3. Crear los nuevos árboles intercambiando las ramas
        NodoAST childTree1 = tree1.replaceSubtree(new int[]{0}, point1, subTree2);
        NodoAST childTree2 = tree2.replaceSubtree(new int[]{0}, point2, subTree1);

        // 4. Devolver los nuevos cromosomas hijos
        return new Chromosome[]{
            new Chromosome(childTree1), 
            new Chromosome(childTree2)
        };
    }
}