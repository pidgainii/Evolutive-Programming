package practica.real;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import practica.enums.Accion;
import practica.enums.MutationMethod;
import practica.enums.Sensor;
import practica.ast.*;

public class Mutation {

    public static void mutate(MutationMethod mth, Chromosome ind, Random rand, double pm) {
        // Si es aleatoria, elegimos una de las 4 principales
        if (mth == MutationMethod.ALEATORIA) {
            MutationMethod[] methods = {
                MutationMethod.SUBARBOL, MutationMethod.FUNCIONAL, 
                MutationMethod.TERMINAL, MutationMethod.HOIST
            };
            mth = methods[rand.nextInt(methods.length)];
        }

        NodoAST root = ind.getTree();
        int tam = root.tam();

        switch (mth) {
            case SUBARBOL:
                int point = rand.nextInt(tam);
                // IMPORTANTE: Llama a tu generador para crear una rama nueva aleatoria
                NodoAST randomBranch = new NodoAccion(Accion.AVANZAR); // <-- CAMBIAR POR TU GENERADOR REAL
                ind.setTree(root.replaceSubtree(new int[]{0}, point, randomBranch));
                break;

            case HOIST:
                if (tam > 1) {
                    // Elegimos un nodo aleatorio que NO sea la raíz (índice > 0)
                    int hoistPoint = 1 + rand.nextInt(tam - 1); 
                    NodoAST hoistedBranch = root.getSubtree(new int[]{0}, hoistPoint);
                    // La sub-rama elegida se convierte en el individuo completo
                    ind.setTree(hoistedBranch.deepCopy());
                }
                break;

            case FUNCIONAL:
                // Obtenemos solo los índices de los nodos internos
                List<Integer> internals = getIndices(root, false);
                if (!internals.isEmpty()) {
                    int funcPoint = internals.get(rand.nextInt(internals.size()));
                    NodoAST oldNode = root.getSubtree(new int[]{0}, funcPoint);
                    NodoAST newNode = mutateInternalNode(oldNode, rand);
                    ind.setTree(root.replaceSubtree(new int[]{0}, funcPoint, newNode));
                }
                break;

            case TERMINAL:
                // Obtenemos solo los índices de las hojas (terminales)
                List<Integer> leaves = getIndices(root, true);
                if (!leaves.isEmpty()) {
                    int termPoint = leaves.get(rand.nextInt(leaves.size()));
                    NodoAST newLeaf = mutateLeafNode(rand);
                    ind.setTree(root.replaceSubtree(new int[]{0}, termPoint, newLeaf));
                }
                break;
        }
    }


    private static List<Integer> getIndices(NodoAST root, boolean wantLeaves) {
        List<Integer> indices = new ArrayList<>();
        int tam = root.tam();
        for (int i = 0; i < tam; i++) {
            NodoAST node = root.getSubtree(new int[]{0}, i);
            if (node.isLeaf() == wantLeaves) {
                indices.add(i);
            }
        }
        return indices;
    }


    private static NodoAST mutateInternalNode(NodoAST node, Random rand) {
        if (node instanceof NodoCondicional) {
            NodoCondicional cond = (NodoCondicional) node;
            
            Sensor nuevoSensor = Sensor.values()[rand.nextInt(Sensor.values().length)];
            int nuevoUmbral = rand.nextInt(100); 

            return new NodoCondicional(
                nuevoSensor, 
                nuevoUmbral, 
                cond.getHijoIzquierdo().deepCopy(), 
                cond.getHijoDerecho().deepCopy()
            );
        } else if (node instanceof NodoBloque) {
            return node.deepCopy();
        }
        return node.deepCopy();
    }

 
    private static NodoAST mutateLeafNode(Random rand) {
        Accion nuevaAccion = Accion.values()[rand.nextInt(Accion.values().length)];
        return new NodoAccion(nuevaAccion);
    }
}