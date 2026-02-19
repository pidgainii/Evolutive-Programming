package practica.pe;

import java.util.ArrayList;

public class Elite {


    public static ArrayList<Chromosome> elite (ArrayList<Chromosome> population, int[] scores, int elitePerc) {
        int eliteSize = (int) ((elitePerc * population.size()) / 100.0);

        if (eliteSize <= 0)
            return new ArrayList<>();

        eliteSize = Math.min(eliteSize, population.size());

        // Create index list
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < scores.length; i++) {
            indices.add(i);
        }

        // Sort indices by score descending
        indices.sort((a, b) -> Integer.compare(scores[b], scores[a]));

        // Pick best chromosomes
        ArrayList<Chromosome> elites = new ArrayList<>();
        for (int i = 0; i < eliteSize; i++) {
            elites.add(population.get(indices.get(i)));
        }

        return elites;
    }

    public static Chromosome best(ArrayList<Chromosome> population, int[] scores){
        int bestIndex = 0;
        int i;
        for (i = 1; i < scores.length; i++) {
            if (scores[i] > scores[bestIndex]) bestIndex = i;
        }

        return population.get(bestIndex);
    }
}
