package practica.real;

import java.util.*;
import practica.enums.CrossoverMethod;

public final class Crossover {

    private Crossover() {}

    public static Chromosome[] cross(CrossoverMethod method, Chromosome p1, Chromosome p2, Random rand) {
        ArrayList<Integer> a = p1.getGenes();
        ArrayList<Integer> b = p2.getGenes();

        if (a.size() != b.size()) throw new IllegalArgumentException("Parents with different length");
        int n = a.size();
        if (n < 2) return new Chromosome[]{ p1.clone(), p2.clone() };

        return switch (method) {
            case PMX -> pmxTwoChildren(a, b, rand);
            case OX  -> oxTwoChildren(a, b, rand);
            case CX  -> cxTwoChildren(a, b);
            case ERX -> erxTwoChildren(a, b, rand);
            case CO  -> coTwoChildren(a, b, rand);
            case OXPP -> oxppTwoChildren(a, b, rand);
            case RBX -> rbxTwoChildren(a, b, rand);
            
            // fallback (si aún no los implementas, devuelve copias)
            default -> new Chromosome[]{ p1.clone(), p2.clone() };
        };
    }

    // ============================================================
    // 1) PMX
    // ============================================================
    private static Chromosome[] pmxTwoChildren(List<Integer> p1, List<Integer> p2, Random rand) {
        int n = p1.size();
        int c1 = rand.nextInt(n);
        int c2 = rand.nextInt(n);
        int cut1 = Math.min(c1, c2);
        int cut2 = Math.max(c1, c2);

        Integer[] h1 = new Integer[n];
        Integer[] h2 = new Integer[n];

        // copy middle segment
        for (int i = cut1; i <= cut2; i++) {
            h1[i] = p1.get(i);
            h2[i] = p2.get(i);
        }

        // quick membership
        Set<Integer> h1Mid = new HashSet<>();
        Set<Integer> h2Mid = new HashSet<>();
        for (int i = cut1; i <= cut2; i++) {
            h1Mid.add(h1[i]);
            h2Mid.add(h2[i]);
        }

        // fill outside segment
        for (int i = 0; i < n; i++) {
            if (i >= cut1 && i <= cut2) continue;

            // fill h1 from p2
            int cand1 = p2.get(i);
            while (contains(h1, cand1)) {
                int idxConflict = indexOf(p2, cand1);
                cand1 = p1.get(idxConflict);
            }
            h1[i] = cand1;

            // fill h2 from p1
            int cand2 = p1.get(i);
            while (contains(h2, cand2)) {
                int idxConflict = indexOf(p1, cand2);
                cand2 = p2.get(idxConflict);
            }
            h2[i] = cand2;
        }

        return new Chromosome[] { new Chromosome(toArrayList(h1)), new Chromosome(toArrayList(h2)) };
    }

    // ============================================================
    // 2) OX
    // ============================================================
    private static Chromosome[] oxTwoChildren(List<Integer> p1, List<Integer> p2, Random rand) {
        int n = p1.size();
        int c1 = rand.nextInt(n);
        int c2 = rand.nextInt(n);
        int cut1 = Math.min(c1, c2);
        int cut2 = Math.max(c1, c2);

        Integer[] h1 = new Integer[n];
        Integer[] h2 = new Integer[n];

        // copy middle segment
        for (int i = cut1; i <= cut2; i++) {
            h1[i] = p1.get(i);
            h2[i] = p2.get(i);
        }

        // elements for child1: from p2 not in h1
        ArrayList<Integer> fill1 = new ArrayList<>(n);
        for (int x : p2) if (!contains(h1, x)) fill1.add(x);

        // elements for child2: from p1 not in h2
        ArrayList<Integer> fill2 = new ArrayList<>(n);
        for (int x : p1) if (!contains(h2, x)) fill2.add(x);

        // circular fill starting from cut2+1
        int idx1 = 0, idx2 = 0;
        for (int k = 0; k < n; k++) {
            int i = (cut2 + 1 + k) % n;

            if (h1[i] == null) h1[i] = fill1.get(idx1++);
            if (h2[i] == null) h2[i] = fill2.get(idx2++);
        }

        return new Chromosome[] { new Chromosome(toArrayList(h1)), new Chromosome(toArrayList(h2)) };
    }

    // ============================================================
    // 3) CX (Cycle Crossover)
    // ============================================================
    private static Chromosome[] cxTwoChildren(List<Integer> p1, List<Integer> p2) {
        int n = p1.size();
        Integer[] h1 = new Integer[n];
        Integer[] h2 = new Integer[n];

        // build index map for p1 to jump fast: value -> index
        Map<Integer, Integer> posInP1 = new HashMap<>(n * 2);
        for (int i = 0; i < n; i++) posInP1.put(p1.get(i), i);

        int start = 0;
        while (start < n && h1[start] != null) start++;

        // take first cycle from p1 into h1, from p2 into h2
        int idx = start;
        while (idx >= 0 && idx < n && h1[idx] == null) {
            h1[idx] = p1.get(idx);
            h2[idx] = p2.get(idx);

            int valueFromP2 = p2.get(idx);
            idx = posInP1.get(valueFromP2);
        }

        // fill remaining positions swapped
        for (int i = 0; i < n; i++) {
            if (h1[i] == null) {
                h1[i] = p2.get(i);
                h2[i] = p1.get(i);
            }
        }

        return new Chromosome[] { new Chromosome(toArrayList(h1)), new Chromosome(toArrayList(h2)) };
    }

    // ============================================================
    // 5) ERX (Edge Recombination)
    // ============================================================
    private static Chromosome[] erxTwoChildren(List<Integer> p1, List<Integer> p2, Random rand) {
        Map<Integer, Set<Integer>> edgeMap = buildEdgeMap(p1, p2);
        Map<Integer, Set<Integer>> edgeMapCopy = deepCopyEdgeMap(edgeMap);

        ArrayList<Integer> h1 = buildErxChild(p1, edgeMap, rand);
        ArrayList<Integer> h2 = buildErxChild(p2, edgeMapCopy, rand);

        return new Chromosome[] { new Chromosome(h1), new Chromosome(h2) };
    }

    private static Map<Integer, Set<Integer>> buildEdgeMap(List<Integer> p1, List<Integer> p2) {
        Map<Integer, Set<Integer>> map = new HashMap<>();

        for (int x : p1) map.put(x, new HashSet<>());
        for (int x : p2) map.putIfAbsent(x, new HashSet<>());

        addEdgesFromParent(map, p1);
        addEdgesFromParent(map, p2);

        return map;
    }

    private static void addEdgesFromParent(Map<Integer, Set<Integer>> map, List<Integer> p) {
        int n = p.size();
        for (int i = 0; i < n; i++) {
            int curr = p.get(i);
            int left = p.get((i - 1 + n) % n);
            int right = p.get((i + 1) % n);
            map.get(curr).add(left);
            map.get(curr).add(right);
        }
    }

    private static Map<Integer, Set<Integer>> deepCopyEdgeMap(Map<Integer, Set<Integer>> src) {
        Map<Integer, Set<Integer>> copy = new HashMap<>();
        for (Map.Entry<Integer, Set<Integer>> e : src.entrySet()) {
            copy.put(e.getKey(), new HashSet<>(e.getValue()));
        }
        return copy;
    }

    private static ArrayList<Integer> buildErxChild(List<Integer> startParent,
                                                   Map<Integer, Set<Integer>> edgeMap,
                                                   Random rand) {
        int n = startParent.size();
        Set<Integer> remaining = new HashSet<>(startParent);
        ArrayList<Integer> child = new ArrayList<>(n);

        int current = startParent.get(0);
        child.add(current);
        remaining.remove(current);

        while (child.size() < n) {
            // remove references to current in all adjacency lists
            for (Set<Integer> neigh : edgeMap.values()) {
                neigh.remove(current);
            }

            Set<Integer> neighbors = edgeMap.getOrDefault(current, Collections.emptySet());

            int next;
            if (!neighbors.isEmpty()) {
                next = pickNeighborWithFewestConnections(neighbors, edgeMap, rand);
            } else {
                next = pickRandomFromRemaining(remaining, rand);
            }

            child.add(next);
            remaining.remove(next);
            current = next;
        }

        return child;
    }
    
 // ============================================================
 // 2b) OXPP (Order Crossover with Priority Positions)
 // ============================================================
 private static Chromosome[] oxppTwoChildren(List<Integer> p1, List<Integer> p2, Random rand) {
     int n = p1.size();
     if (n < 2) return new Chromosome[]{ new Chromosome(new ArrayList<>(p1)), new Chromosome(new ArrayList<>(p2)) };

     // número de posiciones prioritarias:
     int min = Math.max(1, n / 4);
     int max = Math.max(min, n / 2);
     int m = min + rand.nextInt(max - min + 1);

     boolean[] priority = new boolean[n];
     int chosen = 0;
     while (chosen < m) {
         int idx = rand.nextInt(n);
         if (!priority[idx]) {
             priority[idx] = true;
             chosen++;
         }
     }

     Integer[] h1 = new Integer[n];
     Integer[] h2 = new Integer[n];

     // hijo1: copiar prioritarias desde p1
     for (int i = 0; i < n; i++) {
         if (priority[i]) h1[i] = p1.get(i);
     }

     // hijo2: copiar prioritarias desde p2
     for (int i = 0; i < n; i++) {
         if (priority[i]) h2[i] = p2.get(i);
     }

     // rellenar huecos con orden del otro padre
     fillHolesInOrder(h1, p2);
     fillHolesInOrder(h2, p1);

     return new Chromosome[]{ new Chromosome(toArrayList(h1)), new Chromosome(toArrayList(h2)) };
 }

 private static void fillHolesInOrder(Integer[] child, List<Integer> donorParent) {
     int n = child.length;

     int writePos = 0;
     for (int gene : donorParent) {
         if (contains(child, gene)) continue;

         // avanzar a siguiente hueco
         while (writePos < n && child[writePos] != null) writePos++;
         if (writePos >= n) break;

         child[writePos] = gene;
         writePos++;
     }
 }

    private static int pickNeighborWithFewestConnections(Set<Integer> neighbors,
                                                        Map<Integer, Set<Integer>> edgeMap,
                                                        Random rand) {
        int best = -1;
        int bestSize = Integer.MAX_VALUE;

        // if ties, pick random among ties
        ArrayList<Integer> ties = new ArrayList<>();
        for (int v : neighbors) {
            int size = edgeMap.getOrDefault(v, Collections.emptySet()).size();
            if (size < bestSize) {
                bestSize = size;
                ties.clear();
                ties.add(v);
            } else if (size == bestSize) {
                ties.add(v);
            }
        }

        return ties.get(rand.nextInt(ties.size()));
    }

    private static int pickRandomFromRemaining(Set<Integer> remaining, Random rand) {
        int r = rand.nextInt(remaining.size());
        int i = 0;
        for (int x : remaining) {
            if (i++ == r) return x;
        }
        throw new IllegalStateException("Remaining empty");
    }

    // ============================================================
    // 6) CO (Ordinal coding + one-point crossover)
    // ============================================================
    private static Chromosome[] coTwoChildren(List<Integer> p1, List<Integer> p2, Random rand) {
        int n = p1.size();

        int[] o1 = toOrdinal(p1);
        int[] o2 = toOrdinal(p2);

        int cut = 1 + rand.nextInt(n - 1);

        int[] c1 = new int[n];
        int[] c2 = new int[n];

        System.arraycopy(o1, 0, c1, 0, cut);
        System.arraycopy(o2, cut, c1, cut, n - cut);

        System.arraycopy(o2, 0, c2, 0, cut);
        System.arraycopy(o1, cut, c2, cut, n - cut);

        ArrayList<Integer> h1 = fromOrdinal(c1, p1); // use same alphabet
        ArrayList<Integer> h2 = fromOrdinal(c2, p1);

        return new Chromosome[] { new Chromosome(h1), new Chromosome(h2) };
    }

    /**
     * Ordinal encoding: for each gene, store its index within the list of remaining symbols (sorted).
     */
    private static int[] toOrdinal(List<Integer> perm) {
        int n = perm.size();

        ArrayList<Integer> alphabet = new ArrayList<>(perm);
        Collections.sort(alphabet);

        int[] ord = new int[n];
        for (int i = 0; i < n; i++) {
            int gene = perm.get(i);
            int idx = alphabet.indexOf(gene); // O(n) but n is small/medium; can optimize later
            ord[i] = idx;
            alphabet.remove(idx);
        }
        return ord;
    }

    private static ArrayList<Integer> fromOrdinal(int[] ord, List<Integer> referenceAlphabet) {
        int n = ord.length;

        ArrayList<Integer> alphabet = new ArrayList<>(referenceAlphabet);
        Collections.sort(alphabet);

        ArrayList<Integer> perm = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            int idx = ord[i];
            if (idx < 0 || idx >= alphabet.size()) {
                // if something went out of bounds due to bugs, fail fast
                throw new IllegalStateException("Invalid ordinal index " + idx + " at i=" + i);
            }
            perm.add(alphabet.get(idx));
            alphabet.remove(idx);
        }
        return perm;
    }
    
 // ============================================================
 // Inventado) RBX - Route Based Crossover (mTSP-aware)
 // ============================================================
 private static Chromosome[] rbxTwoChildren(List<Integer> p1, List<Integer> p2, Random rand) {
     // Inferir C (num cámaras) como el máximo "corte" entre cámaras y separadores:
     // En vuestra codificación, cámaras son 1..C y separadores > C.
     // Como siempre hay (D-1) separadores, C será: maxGene - (D-1).
     // Pero no tenemos D aquí. En su lugar: detectamos C como el count de genes <= candidateC...
     // Lo más robusto: C = número de "genes cámara" = (totalGenes) - (numSeparators).
     int total = p1.size();
     int numSeparators = 0;
     for (int g : p1) if (g > 0) {} // no-op

     // Detectar separadores: son los genes que NO son cámaras, pero aquí no sabemos C.
     // En tu codificación real: los separadores son exactamente los genes que aparecen > numCamaras
     // y esos valores son consecutivos al final. Podemos deducirlos porque son los mayores.
     // Estrategia: contar cuántos de los genes son "los más grandes consecutivos" al final del alfabeto.
     // Como el alfabeto es 1..total, los separadores son (C+1..total) => son los mayores.
     // Entonces numSeparators es cuántos drones-1: lo podemos asumir como cantidad de genes "grandes"
     // si conocemos C, pero C = total - numSeparators => circular.
     //
     // SOLUCIÓN simple: asumir que separadores son aquellos genes > (total - (D-1))...
     // pero no tenemos D.
     //
     // Para evitar esto, implementamos RBX SIN separar cámaras vs separadores:
     // tomamos un bloque entre separadores como "ruta" (incluye solo cámaras en la práctica) y lo copiamos.

     ArrayList<Integer> child1 = rbxOneChild(p1, p2, rand);
     ArrayList<Integer> child2 = rbxOneChild(p2, p1, rand);

     return new Chromosome[]{ new Chromosome(child1), new Chromosome(child2) };
 }

 private static ArrayList<Integer> rbxOneChild(List<Integer> routeDonor, List<Integer> orderDonor, Random rand) {
     int n = routeDonor.size();

     // 1) elegir un segmento aleatorio [a,b] que actuará como "ruta-bloque"
     // (si tus separadores están presentes, con bastante probabilidad el bloque caerá dentro de una ruta)
     int a = rand.nextInt(n);
     int b = rand.nextInt(n);
     int lo = Math.min(a, b);
     int hi = Math.max(a, b);

     Integer[] child = new Integer[n];

     // 2) copiar bloque del routeDonor
     for (int i = lo; i <= hi; i++) child[i] = routeDonor.get(i);

     // 3) rellenar el resto en orden con orderDonor (estilo OX)
     int write = (hi + 1) % n;
     for (int gene : orderDonor) {
         if (contains(child, gene)) continue;

         while (child[write] != null) write = (write + 1) % n;
         child[write] = gene;
         write = (write + 1) % n;
     }

     return toArrayList(child);
 }

    // ============================================================
    // small utils
    // ============================================================
    private static boolean contains(Integer[] arr, int value) {
        for (Integer x : arr) if (x != null && x == value) return true;
        return false;
    }

    private static int indexOf(List<Integer> list, int value) {
        for (int i = 0; i < list.size(); i++) if (list.get(i) == value) return i;
        return -1;
    }

    private static ArrayList<Integer> toArrayList(Integer[] arr) {
        ArrayList<Integer> out = new ArrayList<>(arr.length);
        for (Integer x : arr) out.add(x);
        return out;
    }
}