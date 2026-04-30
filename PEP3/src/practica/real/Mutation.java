package practica.real;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import practica.enums.Accion;
import practica.enums.MutationMethod;
import practica.enums.Sensor;
import practica.ast.*;

public class Mutation {

    public static void mutate(MutationMethod mth, Chromosome ind, Random rand, double pm, boolean poda, int profPoda) {
        if (mth == MutationMethod.ALEATORIA) {
            MutationMethod[] methods = {
                MutationMethod.SUBARBOL, MutationMethod.FUNCIONAL, 
                MutationMethod.TERMINAL, MutationMethod.HOIST
            };
            mth = methods[rand.nextInt(methods.length)];
        }

        NodoAST root = ind.getTree().deepCopy(); // Trabajamos sobre una copia
        int tam = root.tam();

        switch (mth) {
            case SUBARBOL:
                int point = rand.nextInt(tam);
                NodoAST randomBranch = Utils.generarArbolGrow(0, 3);
                root = root.replaceSubtree(new int[]{0}, point, randomBranch);
                
                if (poda) {
                	if (root.profundidad() > profPoda) {
                        root = Utils.podar(root, 1, profPoda, rand);
                    }
                }
                
                ind.setTree(root);
                break;

            case HOIST:
                if (tam > 1) {
                    int hoistPoint = 1 + rand.nextInt(tam - 1); 
                    // Extraemos y desconectamos totalmente de la raíz vieja
                    NodoAST branch = root.getSubtree(new int[]{0}, hoistPoint).deepCopy();
                    ind.setTree(branch);
                }
                break;

            case FUNCIONAL:
                List<Integer> internals = getIndices(root, false);
                if (!internals.isEmpty()) {
                    int funcPoint = internals.get(rand.nextInt(internals.size()));
                    NodoAST oldNode = root.getSubtree(new int[]{0}, funcPoint);
                    NodoAST newNode = mutateInternalNode(oldNode, rand);
                    ind.setTree(root.replaceSubtree(new int[]{0}, funcPoint, newNode));
                }
                break;

            case TERMINAL:
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
            int[] opcionesUmbral = {10, 50, 100};
            int nuevoUmbral = opcionesUmbral[rand.nextInt(opcionesUmbral.length)];

            return new NodoCondicional(
                nuevoSensor, 
                nuevoUmbral, 
                cond.getHijoIzquierdo().deepCopy(), 
                cond.getHijoDerecho().deepCopy()
            );
        }
        return node.deepCopy();
    }

    private static NodoAST mutateLeafNode(Random rand) {
        Accion nuevaAccion = Accion.values()[rand.nextInt(Accion.values().length)];
        return new NodoAccion(nuevaAccion);
    }
}