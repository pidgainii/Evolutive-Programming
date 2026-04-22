package practica.real;

import practica.enums.MutationMethod;
import practica.enums.SelectionMethod;
import practica.ui.EvolutionListener;
import practica.ui.GAResult;
import java.util.ArrayList;
import java.util.Random;

public class Evolution {

    private final Fitness fitness;
    private Chromosome globalBest;
    private final int population_size;

    public Evolution(Fitness fitness, int population_size) {
        this.fitness = fitness;
        this.population_size = population_size;
        this.globalBest = null;
    }

    public void evaluateAndNormalize(Population population) {
        Chromosome localBest = null;
        
        // 1. First pass: Evaluate fitness and find the local best
        for (Chromosome ind : population.getPopulation()) {
            ind.setFitness(fitness.evaluate_final(ind));
            if (localBest == null || ind.getFitness() > localBest.getFitness()) {
                localBest = ind;
            }
        }

        // 2. Update Global Best (Cloning is essential here)
        if (this.globalBest == null || localBest.getFitness() > globalBest.getFitness()) {
            this.globalBest = localBest.clone();
        }

        // 3. Normalization for Roulette/Selection
        // We shift fitness so the minimum is at least 1.0 (to handle negative fitness)
        double minFitness = population.getPopulation().stream()
                .mapToDouble(Chromosome::getFitness).min().orElse(0.0);
        
        double sumAdjustedFitness = 0.0;
        for (Chromosome ind : population.getPopulation()) {
            sumAdjustedFitness += (ind.getFitness() - minFitness + 1.0);
        }

        double accumulated = 0.0;
        for (Chromosome ind : population.getPopulation()) {
            double rel = (ind.getFitness() - minFitness + 1.0) / sumAdjustedFitness;
            accumulated += rel;
            ind.setRelative_fitness(rel);
            ind.setAcum_fitness(accumulated);
        }
        
        // Ensure the last individual's accumulated fitness is exactly 1.0 to avoid precision errors
        population.getPopulation().get(population.getPopulation().size() - 1).setAcum_fitness(1.0);
    }

    // MODIFICADO: Añadido "String selectionMethodString"
    public GAResult evolveWithListener(int nGenerations, Population population,
                                       double elitismRate, double pc, double pm, 
                                       String selectionMethodString,
                                       String mutationMethodString, EvolutionListener listener) {

        Random rand = new Random();
        evaluateAndNormalize(population);

        double[] bestOfGen = new double[nGenerations];
        double[] globalBestSoFar = new double[nGenerations];
        double[] avgFitness = new double[nGenerations];

        // Parseamos el método de selección dinámicamente
        SelectionMethod selMth = SelectionMethod.valueOf(selectionMethodString);

        for (int gen = 0; gen < nGenerations; gen++) {
            int eliteCount = (int) Math.round(population_size * elitismRate);
            ArrayList<Chromosome> elite = getElite(population, eliteCount);

            Population nextPop = new Population();
            
            // Usamos el método de selección elegido por el usuario
            Selection.select(selMth, population, nextPop, population_size, rand);

            
            // Crossover
            for (int i = eliteCount; i + 1 < population_size; i += 2) {
                if (rand.nextDouble() < pc) {
                    Chromosome[] children = Crossover.cross(nextPop.getPopulation().get(i), 
                                                           nextPop.getPopulation().get(i+1), rand);
                    nextPop.swap(i, children[0]);
                    nextPop.swap(i+1, children[1]);
                }
            }
            
            // Mutation
            MutationMethod mth = MutationMethod.valueOf(mutationMethodString);
            for (int i = eliteCount; i < population_size; i++) {
                if (rand.nextDouble() < pm) {
                    Mutation.mutate(mth, nextPop.getPopulation().get(i), rand, pm);
                }
            }
            

            for (int i = 0; i < elite.size(); i++) {
                nextPop.swap(i, elite.get(i));
            }

            evaluateAndNormalize(nextPop);
            population = nextPop;

            bestOfGen[gen] = population.getPopulation().stream().mapToDouble(Chromosome::getFitness).max().orElse(0);
            globalBestSoFar[gen] = globalBest.getFitness();
            avgFitness[gen] = population.getPopulation().stream().mapToDouble(Chromosome::getFitness).average().orElse(0);

            if (listener != null) {
                listener.onGeneration(gen, bestOfGen[gen], globalBestSoFar[gen], avgFitness[gen], globalBest);
            }
        }

        return new GAResult(globalBest, bestOfGen, globalBestSoFar, avgFitness);
    }

    private ArrayList<Chromosome> getElite(Population pop, int count) {
        ArrayList<Chromosome> sorted = new ArrayList<>(pop.getPopulation());
        sorted.sort((a, b) -> Double.compare(b.getFitness(), a.getFitness())); 
        ArrayList<Chromosome> elite = new ArrayList<>();
        for (int i = 0; i < count; i++) elite.add(sorted.get(i).clone());
        return elite;
    }
}