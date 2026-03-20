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
		case TOURNAMENT -> tournament(actual, dest, populationSize, 3, rand); // k=3
		case STOCHASTIC -> stochasticUniversal(actual, dest, populationSize, rand);
		case TRUNCATION -> truncation(actual, dest, populationSize, 0.5, rand); // top 50%
		case REMAINDERS -> remainders(actual, dest, populationSize, rand);
		case RANKING -> ranking(actual, dest, populationSize, 1.7, rand);
		}
	}

	// ---- Helper para obtener score invertido ----
	private static double score(Chromosome c, double maxFitness) {
		return maxFitness - c.getFitness(); // menor fitness = mayor score
	}

	// ---- Métodos adaptados ----

	public static void roulette(Population actual, Population dest, int populationSize, Random rand) {
		double maxFitness = actual.getPopulation().stream()
				.mapToDouble(Chromosome::getFitness)
				.max().orElse(1.0);

		double sumScores = 0.0;
		for (Chromosome c : actual.getPopulation()) {
			sumScores += score(c, maxFitness);
		}

		// Calcular acumulado
		double accumulated = 0.0;
		for (Chromosome c : actual.getPopulation()) {
			double rel = score(c, maxFitness) / sumScores;
			accumulated += rel;
			c.setRelative_fitness(rel);
			c.setAcum_fitness(accumulated);
		}
		actual.getPopulation().get(actual.getPopulation().size() - 1).setAcum_fitness(1.0);

		// Selección ruleta
		for (int i = 0; i < populationSize; i++) {
			double prob = rand.nextDouble();
			int pos = 0;
			while (prob > actual.getPopulation().get(pos).getAcum_fitness()) pos++;
			put(dest, i, actual.getPopulation().get(pos).clone());
		}
	}

	public static void tournament(Population actual, Population dest, int populationSize, int k, Random rand) {
		int n = actual.getPopulation().size();
		double maxFitness = actual.getPopulation().stream()
				.mapToDouble(Chromosome::getFitness)
				.max().orElse(1.0);

		for (int i = 0; i < populationSize; i++) {
			Chromosome best = null;
			for (int j = 0; j < k; j++) {
				Chromosome c = actual.getPopulation().get(rand.nextInt(n));
				if (best == null || score(c, maxFitness) > score(best, maxFitness)) best = c;
			}
			put(dest, i, best.clone());
		}
	}

	public static void stochasticUniversal(Population actual, Population dest, int populationSize, Random rand) {
		double maxFitness = actual.getPopulation().stream()
				.mapToDouble(Chromosome::getFitness)
				.max().orElse(1.0);

		// recalcular acumulado
		double sumScores = 0.0;
		for (Chromosome c : actual.getPopulation()) sumScores += score(c, maxFitness);

		double accumulated = 0.0;
		for (Chromosome c : actual.getPopulation()) {
			double rel = score(c, maxFitness) / sumScores;
			accumulated += rel;
			c.setRelative_fitness(rel);
			c.setAcum_fitness(accumulated);
		}
		actual.getPopulation().get(actual.getPopulation().size() - 1).setAcum_fitness(1.0);

		// SUS
		double step = 1.0 / populationSize;
		double start = rand.nextDouble() * step;

		int idx = 0;
		for (int i = 0; i < populationSize; i++) {
			double pointer = start + i * step;
			while (pointer > actual.getPopulation().get(idx).getAcum_fitness()) idx++;
			put(dest, i, actual.getPopulation().get(idx).clone());
		}
	}

	public static void truncation(Population actual, Population dest, int populationSize, double truncationRate, Random rand) {
		double maxFitness = actual.getPopulation().stream()
				.mapToDouble(Chromosome::getFitness)
				.max().orElse(1.0);

		ArrayList<Chromosome> sorted = new ArrayList<>(actual.getPopulation());
		sorted.sort(Comparator.comparingDouble(c -> -score(c, maxFitness))); // score descendente

		int cutoff = Math.max(1, (int) Math.floor(sorted.size() * truncationRate));

		for (int i = 0; i < populationSize; i++) {
			Chromosome chosen = sorted.get(rand.nextInt(cutoff));
			put(dest, i, chosen.clone());
		}
	}

	public static void remainders(Population actual, Population dest, int populationSize, Random rand) {
		double maxFitness = actual.getPopulation().stream()
				.mapToDouble(Chromosome::getFitness)
				.max().orElse(1.0);

		ArrayList<Chromosome> remainderPool = new ArrayList<>();
		int filled = 0;

		// 1) deterministic copies
		for (Chromosome c : actual.getPopulation()) {
			double expected = c.getRelative_fitness() * populationSize;
			int copies = (int) Math.floor(expected);
			for (int k = 0; k < copies && filled < populationSize; k++) put(dest, filled++, c.clone());
			double frac = expected - copies;
			if (frac > 0) {
				int tickets = (int) Math.round(frac * 1000.0);
				for (int t = 0; t < tickets; t++) remainderPool.add(c);
			}
		}

		if (filled >= populationSize) return;

		if (remainderPool.isEmpty()) {
			for (int i = filled; i < populationSize; i++) {
				double prob = rand.nextDouble();
				int pos = 0;
				while (prob > actual.getPopulation().get(pos).getAcum_fitness()) pos++;
				put(dest, i, actual.getPopulation().get(pos).clone());
			}
			return;
		}

		for (int i = filled; i < populationSize; i++) {
			Chromosome chosen = remainderPool.get(rand.nextInt(remainderPool.size()));
			put(dest, i, chosen.clone());
		}
	}

	public static void ranking(Population actual,
			Population dest,
			int populationSize,
			double s,
			Random rand) {

		// s = selective pressure in [1.0, 2.0]
		s = Math.max(1.0, Math.min(2.0, s));

		ArrayList<Chromosome> sorted = new ArrayList<>(actual.getPopulation());
		sorted.sort(Comparator.comparingDouble(Chromosome::getFitness)); // mejor (menor) primero

		int n = sorted.size();
		if (n == 0) return;

		// Probabilidad lineal por ranking (Baker):
		// p(i) = (2 - s)/n + 2*i*(s-1)/(n*(n-1)), con i=0 peor o i=0 mejor según convención.
		// Usamos i=0 = mejor.
		double[] p = new double[n];
		double sum = 0.0;

		if (n == 1) {
			p[0] = 1.0;
			sum = 1.0;
		} else {
			for (int i = 0; i < n; i++) {
			    int rank = n - 1 - i; // invert: best individual gets highest rank
			    p[i] = ((2.0 - s) / n) + (2.0 * rank * (s - 1.0) / (n * (n - 1.0)));
			    sum += p[i];
			}
		}

		// acumuladas
		double acc = 0.0;
		double[] cum = new double[n];
		for (int i = 0; i < n; i++) {
			acc += p[i] / sum;
			cum[i] = acc;
		}
		cum[n - 1] = 1.0;

		// ruleta sobre el ranking
		for (int k = 0; k < populationSize; k++) {
			double r = rand.nextDouble();
			int idx = 0;
			while (r > cum[idx]) idx++;
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