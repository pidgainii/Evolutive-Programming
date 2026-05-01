package practica.real;

import practica.enums.MutationMethod;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

public final class Mutation {
    private Mutation() {}

    public static void mutate(MutationMethod method,
                              Chromosome chr,
                              Random rand,
                              double pm,
                              Fitness fitness) {
        if (rand.nextDouble() >= pm) return;

        switch (method) {
            case INSERTION -> insertion(chr.getGenes(), rand);
            case SWAP -> swap(chr.getGenes(), rand);
            case INVERSION -> inversion(chr.getGenes(), rand);
            case HEURISTIC -> heuristicKPermutations(chr, rand, fitness, 3); // k=3 (puedes poner 2 o 3)
            case BALANCE_MOVE -> balanceMove(chr, rand, fitness, 10);        }
    }

    // -----------------------
    // Inserción: sacar i e insertar en j
    // -----------------------
    private static void insertion(ArrayList<Integer> g, Random rand) {
        int n = g.size();
        if (n < 2) return;

        int i = rand.nextInt(n);
        int j = rand.nextInt(n);
        while (j == i) j = rand.nextInt(n);

        int val = g.remove(i);
        if (i < j) j--;
        g.add(j, val);
    }

    // -----------------------
    // Intercambio
    // -----------------------
    private static void swap(ArrayList<Integer> g, Random rand) {
        int n = g.size();
        if (n < 2) return;

        int i = rand.nextInt(n);
        int j = rand.nextInt(n);
        while (j == i) j = rand.nextInt(n);

        Collections.swap(g, i, j);
    }

    // -----------------------
    // Inversión de segmento
    // -----------------------
    private static void inversion(ArrayList<Integer> g, Random rand) {
        int n = g.size();
        if (n < 2) return;

        int a = rand.nextInt(n);
        int b = rand.nextInt(n);
        int lo = Math.min(a, b);
        int hi = Math.max(a, b);

        while (lo < hi) {
            Collections.swap(g, lo, hi);
            lo++;
            hi--;
        }
    }

    // -----------------------
    // Heurística (la que pides):
    // Elegir k posiciones -> probar todas las permutaciones de esos k elementos -> quedarse con la mejor por fitness.
    // -----------------------
    private static void heuristicKPermutations(Chromosome chr,
                                               Random rand,
                                               Fitness fitness,
                                               int kMax) {
        if (fitness == null) return;

        ArrayList<Integer> genes = chr.getGenes();
        int n = genes.size();
        if (n < 2) return;

        int k = Math.min(kMax, n);
        // en tu texto: k=2-3; si n es pequeño, ajusta
        if (k < 2) return;

        // escoger k índices distintos
        int[] idx = pickDistinctIndices(rand, n, k);

        // valores originales en esas posiciones
        ArrayList<Integer> elems = new ArrayList<>(k);
        for (int i = 0; i < k; i++) elems.add(genes.get(idx[i]));

        // generar permutaciones y evaluar
        double bestFit = Double.POSITIVE_INFINITY;
        ArrayList<Integer> bestPerm = null;

        ArrayList<ArrayList<Integer>> perms = new ArrayList<>();
        permute(elems, 0, perms);

        for (ArrayList<Integer> perm : perms) {
            ArrayList<Integer> candidateGenes = new ArrayList<>(genes);

            for (int i = 0; i < k; i++) {
                candidateGenes.set(idx[i], perm.get(i));
            }

            Chromosome candidate = new Chromosome(candidateGenes);
            double f = fitness.evaluate(candidate);

            if (f < bestFit) {
                bestFit = f;
                bestPerm = perm;
            }
        }

        // aplicar mejor permutación al cromosoma real
        if (bestPerm != null) {
            for (int i = 0; i < k; i++) {
                genes.set(idx[i], bestPerm.get(i));
            }
        }
    }

    private static int[] pickDistinctIndices(Random rand, int n, int k) {
        HashSet<Integer> set = new HashSet<>();
        while (set.size() < k) set.add(rand.nextInt(n));
        int[] idx = new int[k];
        int i = 0;
        for (int v : set) idx[i++] = v;
        // importante: ordenarlos para que “posición i” sea estable y reproducible
        java.util.Arrays.sort(idx);
        return idx;
    }

    private static void permute(ArrayList<Integer> a, int start, ArrayList<ArrayList<Integer>> out) {
        if (start == a.size()) {
            out.add(new ArrayList<>(a));
            return;
        }
        for (int i = start; i < a.size(); i++) {
            Collections.swap(a, start, i);
            permute(a, start + 1, out);
            Collections.swap(a, start, i);
        }
    }
    private static void balanceMove(Chromosome chr, Random rand, Fitness fitness, int tries) {
        if (fitness == null) return;

        FitnessBreakdown bd = fitness.evaluateBreakdown(chr);
        double[] times = bd.timesPerDrone();
        if (times.length < 2) return;

        int slow = 0, fast = 0;
        for (int i = 1; i < times.length; i++) {
            if (times[i] > times[slow]) slow = i;
            if (times[i] < times[fast]) fast = i;
        }
        if (slow == fast) return;

        // Obtener límites (start/end indices) del segmento de cada dron en el genotipo
        int numDrones = times.length;
        int numCams = chr.getGenes().size() - (numDrones - 1);

        Segment[] segs = splitBySeparators(chr.getGenes(), numCams, numDrones);

        Segment sSlow = segs[slow];
        Segment sFast = segs[fast];

        if (sSlow.camPositions.isEmpty()) return;

        double bestFit = fitness.evaluate(chr);
        ArrayList<Integer> bestGenes = null;

        for (int t = 0; t < tries; t++) {
            int pickIdx = sSlow.camPositions.get(rand.nextInt(sSlow.camPositions.size()));
            int geneToMove = chr.getGenes().get(pickIdx);

            ArrayList<Integer> candidate = new ArrayList<>(chr.getGenes());
            candidate.remove(pickIdx);

            // inserción dentro del segmento fast (ojo: tras remove cambian índices)
            // recalcular segmentos sobre candidate (simple y seguro)
            Segment[] segs2 = splitBySeparators(candidate, numCams, numDrones);
            Segment sFast2 = segs2[fast];

            int insertPos;
            if (sFast2.start <= sFast2.endExclusive) {
                insertPos = sFast2.start + rand.nextInt(Math.max(1, sFast2.endExclusive - sFast2.start + 1));
            } else {
                insertPos = rand.nextInt(candidate.size() + 1);
            }

            if (insertPos < 0) insertPos = 0;
            if (insertPos > candidate.size()) insertPos = candidate.size();

            candidate.add(insertPos, geneToMove);

            Chromosome candChr = new Chromosome(candidate);
            double f = fitness.evaluate(candChr);
            if (f < bestFit) {
                bestFit = f;
                bestGenes = candidate;
            }
        }

        if (bestGenes != null) {
            chr.setGenes(bestGenes);
        }
    }


    private static Segment[] splitBySeparators(ArrayList<Integer> genes, int numCams, int numDrones) {
        Segment[] segs = new Segment[numDrones];
        for (int i = 0; i < numDrones; i++) segs[i] = new Segment();

        int d = 0;
        segs[d].start = 0;

        for (int i = 0; i < genes.size(); i++) {
            int g = genes.get(i);
            if (g > numCams) { // separator => close current segment
                segs[d].endExclusive = i; // cameras before separator
                d++;
                if (d >= numDrones) break;
                segs[d].start = i + 1;
            } else {
                segs[d].camPositions.add(i);
            }
        }

        // close last segment
        if (d < numDrones) {
            segs[d].endExclusive = genes.size();
        }
        // fill remaining (if any)
        for (int i = d + 1; i < numDrones; i++) {
            segs[i].start = genes.size();
            segs[i].endExclusive = genes.size();
        }

        return segs;
    }

    private static final class Segment {
        int start;
        int endExclusive;
        ArrayList<Integer> camPositions = new ArrayList<>();
    }
}