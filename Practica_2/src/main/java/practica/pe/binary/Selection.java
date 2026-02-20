package practica.pe.binary;

import practica.pe.enums.SelectionMethod;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Selection {

    public static void select(SelectionMethod method,
                              Population actual,
                              Population dest,
                              int populationSize,
                              Random rand) {
        switch (method) {
            case ROULETTE -> roulette(actual, dest, populationSize, rand);
            case TOURNAMENT -> tournament(actual, dest, populationSize, 3, rand); // k=3
            case STOCHASTIC -> stochasticUniversal(actual, dest, populationSize, rand);
            case TRUNCATION -> truncation(actual, dest, populationSize, 0.5, rand); // top 50%
            case REMAINDERS -> remainders(actual, dest, populationSize, rand);
        }
    }

    public static void roulette(Population actual, Population dest, int populationSize, Random rand) {
        for (int i = 0; i < populationSize; i++) {
            double prob = rand.nextDouble();
            int pos = 0;
            while (prob > actual.getPopulation().get(pos).getAcum_fitness()) pos++;
            dest.getPopulation().set(i, actual.getPopulation().get(pos).clone());
        }
    }

    public static void tournament(Population actual, Population dest, int populationSize, int k, Random rand) {
        int n = actual.getPopulation().size();
        for (int i = 0; i < populationSize; i++) {
            Chromosome best = null;
            for (int j = 0; j < k; j++) {
                Chromosome c = actual.getPopulation().get(rand.nextInt(n));
                if (best == null || c.getFitness() > best.getFitness()) best = c;
            }
            dest.getPopulation().set(i, best.clone());
        }
    }

    // SUS: Stochastic Universal Sampling
    public static void stochasticUniversal(Population actual, Population dest, int populationSize, Random rand) {
        double step = 1.0 / populationSize;
        double start = rand.nextDouble() * step;

        int idx = 0;
        for (int i = 0; i < populationSize; i++) {
            double pointer = start + i * step;
            while (pointer > actual.getPopulation().get(idx).getAcum_fitness()) idx++;
            dest.getPopulation().set(i, actual.getPopulation().get(idx).clone());
        }
    }

    // truncationRate=0.5 => eliges del top 50% de fitness
    public static void truncation(Population actual, Population dest, int populationSize, double truncationRate, Random rand) {
        ArrayList<Chromosome> sorted = new ArrayList<>(actual.getPopulation());
        sorted.sort(Comparator.comparingInt(Chromosome::getFitness).reversed());

        int cutoff = Math.max(1, (int) Math.floor(sorted.size() * truncationRate));

        for (int i = 0; i < populationSize; i++) {
            Chromosome chosen = sorted.get(rand.nextInt(cutoff));
            dest.getPopulation().set(i, chosen.clone());
        }
    }

    // Remainders: floor(expected) copies + roulette for the remaining slots
    public static void remainders(Population actual, Population dest, int populationSize, Random rand) {
        // expected copies = relativeFitness * populationSize
        ArrayList<Chromosome> remainderPool = new ArrayList<>();
        int filled = 0;

        // 1) deterministic copies
        for (Chromosome c : actual.getPopulation()) {
            double expected = c.getRelative_fitness() * populationSize;
            int copies = (int) Math.floor(expected);
            for (int k = 0; k < copies && filled < populationSize; k++) {
                dest.getPopulation().set(filled++, c.clone());
            }
            double frac = expected - copies;
            // metemos el individuo en un pool con peso "frac"
            if (frac > 0) {
                // discretización simple: multiplicamos por 1000
                int tickets = (int) Math.round(frac * 1000.0);
                for (int t = 0; t < tickets; t++) remainderPool.add(c);
            }
        }

        if (filled >= populationSize) return;

        // 2) completar por ruleta sobre el pool fraccional (si está vacío, fallback ruleta normal)
        if (remainderPool.isEmpty()) {
            for (int i = filled; i < populationSize; i++) {
                double prob = rand.nextDouble();
                int pos = 0;
                while (prob > actual.getPopulation().get(pos).getAcum_fitness()) pos++;
                dest.getPopulation().set(i, actual.getPopulation().get(pos).clone());
            }
            return;
        }

        for (int i = filled; i < populationSize; i++) {
            Chromosome chosen = remainderPool.get(rand.nextInt(remainderPool.size()));
            dest.getPopulation().set(i, chosen.clone());
        }
    }
}