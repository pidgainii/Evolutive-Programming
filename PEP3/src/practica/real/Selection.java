package practica.real;

import practica.enums.SelectionMethod;
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
            case TOURNAMENT -> tournament(actual, dest, populationSize, 3, rand); 
            case STOCHASTIC -> stochasticUniversal(actual, dest, populationSize, rand);
            case TRUNCATION -> truncation(actual, dest, populationSize, 0.5, rand);
            case REMAINDERS -> remainders(actual, dest, populationSize, rand);
            case RANKING -> ranking(actual, dest, populationSize, 1.7, rand);
        }
    }


    public static void roulette(Population actual, Population dest, int populationSize, Random rand) {
        for (int i = 0; i < populationSize; i++) {
            double prob = rand.nextDouble();
            int pos = 0;
            while (pos < actual.getPopulation().size() - 1 && 
                   prob > actual.getPopulation().get(pos).getAcum_fitness()) {
                pos++;
            }
            put(dest, i, actual.getPopulation().get(pos).clone());
        }
    }

    public static void tournament(Population actual, Population dest, int populationSize, int k, Random rand) {
        int n = actual.getPopulation().size();
        for (int i = 0; i < populationSize; i++) {
            Chromosome best = null;
            for (int j = 0; j < k; j++) {
                Chromosome c = actual.getPopulation().get(rand.nextInt(n));
                // FIX: Compare fitness directly (higher is better)
                if (best == null || c.getFitness() > best.getFitness()) {
                    best = c;
                }
            }
            put(dest, i, best.clone());
        }
    }

    public static void stochasticUniversal(Population actual, Population dest, int populationSize, Random rand) {
        double step = 1.0 / populationSize;
        double start = rand.nextDouble() * step;

        int idx = 0;
        for (int i = 0; i < populationSize; i++) {
            double pointer = start + i * step;
            while (idx < actual.getPopulation().size() - 1 && 
                   pointer > actual.getPopulation().get(idx).getAcum_fitness()) {
                idx++;
            }
            put(dest, i, actual.getPopulation().get(idx).clone());
        }
    }

    public static void truncation(Population actual, Population dest, int populationSize, double truncationRate, Random rand) {
        ArrayList<Chromosome> sorted = new ArrayList<>(actual.getPopulation());
        sorted.sort((a, b) -> Double.compare(b.getFitness(), a.getFitness()));

        int cutoff = Math.max(1, (int) Math.floor(sorted.size() * truncationRate));

        for (int i = 0; i < populationSize; i++) {
            Chromosome chosen = sorted.get(rand.nextInt(cutoff));
            put(dest, i, chosen.clone());
        }
    }

    public static void remainders(Population actual, Population dest, int populationSize, Random rand) {
        ArrayList<Chromosome> remainderPool = new ArrayList<>();
        int filled = 0;

        for (Chromosome c : actual.getPopulation()) {
            double expected = c.getRelative_fitness() * populationSize;
            int copies = (int) Math.floor(expected);
            for (int k = 0; k < copies && filled < populationSize; k++) {
                put(dest, filled++, c.clone());
            }
            double frac = expected - copies;
            if (frac > 0) {
                int tickets = (int) Math.round(frac * 100.0);
                for (int t = 0; t < tickets; t++) remainderPool.add(c);
            }
        }

        while (filled < populationSize) {
            Chromosome chosen = remainderPool.isEmpty() ? 
                actual.getPopulation().get(rand.nextInt(actual.getPopulation().size())) :
                remainderPool.get(rand.nextInt(remainderPool.size()));
            put(dest, filled++, chosen.clone());
        }
    }

    public static void ranking(Population actual, Population dest, int populationSize, double s, Random rand) {
        s = Math.max(1.0, Math.min(2.0, s));
        ArrayList<Chromosome> sorted = new ArrayList<>(actual.getPopulation());
        
        sorted.sort(Comparator.comparingDouble(Chromosome::getFitness));

        int n = sorted.size();
        double[] cum = new double[n];
        double acc = 0.0;

        for (int i = 0; i < n; i++) {
            double p = ((2.0 - s) / n) + (2.0 * i * (s - 1.0) / (n * (n - 1.0)));
            acc += p;
            cum[i] = acc;
        }
        cum[n - 1] = 1.0; 

        for (int k = 0; k < populationSize; k++) {
            double r = rand.nextDouble();
            int idx = 0;
            while (idx < n - 1 && r > cum[idx]) idx++;
            put(dest, k, sorted.get(idx).clone());
        }
    }

    private static void put(Population dest, int index, Chromosome value) {
        if (dest.getPopulation().size() <= index) {
            dest.getPopulation().add(value);
        } else {
            dest.getPopulation().set(index, value);
        }
    }
}