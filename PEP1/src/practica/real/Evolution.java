package practica.real;

import practica.enums.SelectionMethod;
import practica.ui.EvolutionListener;
import practica.ui.GAResult;

import java.util.ArrayList;
import java.util.Random;

public class Evolution {

    private final Fitness fitness;
    private Chromosome globalBest;
    private final int population_size;
    private final int num_cameras;
    private final int N;
    private final int M;

    public Evolution(Fitness fitness,  int population_size,  int num_cameras, int N, int M) {
        this.fitness = fitness;
        this.population_size = population_size;
        this.num_cameras = num_cameras;
        this.N = N;
        this.M = M;

        globalBest = null;
    }

    public void populationCross(Population population,
                                double crossProbability,
                                String crossoverMethodString,
                                Random rand,
                                int startIdx) {

        ArrayList<Integer> selectedIdx = new ArrayList<>();

        for (int idx = startIdx; idx < population.getPopulation().size(); idx++) {
            if (rand.nextDouble() < crossProbability) selectedIdx.add(idx);
        }

        if (selectedIdx.size() % 2 != 0) selectedIdx.remove(selectedIdx.size()-1);

        for (int i = 0; i < selectedIdx.size(); i += 2) {
            int idx1 = selectedIdx.get(i);
            int idx2 = selectedIdx.get(i + 1);

            Chromosome father1 = population.getPopulation().get(idx1);
            Chromosome father2 = population.getPopulation().get(idx2);

            CrossoverMethod cm = CrossoverMethod.valueOf(crossoverMethodString);

            Chromosome[] children = switch (cm) {
                case ONE_POINT -> father1.cross(father2, rand);
                case UNIFORM -> father1.crossUniform(father2, rand);
                case ARITHMETIC -> father1.crossArithmetic(father2, rand);
                case BLX_ALPHA -> father1.crossBlxAlpha(father2, rand, 0.5);
            };

            population.swap(idx1, children[0]);
            population.swap(idx2, children[1]);
        }
    }

    public void mutatePopulation(Population population,
                                 double mutationProbability,
                                 String mutationMethodString,
                                 Random rand,
                                 int startIdx) {

        double sigmaX = 0.5;
        double sigmaY = 0.5;
        double sigmaTheta = 10.0;

        MutationMethod mm = MutationMethod.valueOf(mutationMethodString);

        for (int idx = startIdx; idx < population.getPopulation().size(); idx++) {
            Chromosome individual = population.getPopulation().get(idx);

            switch (mm) {
                case GAUSSIAN -> individual.mutateGaussian(rand, mutationProbability, num_cameras, N, M,
                        sigmaX, sigmaY, sigmaTheta);
                case GENE -> individual.mutateGeneLevel(rand, mutationProbability, num_cameras, N, M);
            }

            individual.setFitness(fitness.evaluate(individual));
        }
    }

    public void evaluateAndNormalize(Population population) {

        for (Chromosome individual : population.getPopulation()) {
            individual.setFitness(fitness.evaluate(individual));
        }

        Chromosome localBest = population.getPopulation().get(0);
        for (Chromosome individual : population.getPopulation()) {
            if (individual.getFitness() > localBest.getFitness()) {
                localBest = individual;
            }
        }

        if (this.globalBest == null || localBest.getFitness() > this.globalBest.getFitness()) {
            this.globalBest = localBest.clone();
        }

        int minFitness = Integer.MAX_VALUE;
        for (Chromosome individual : population.getPopulation()) {
            minFitness = Math.min(minFitness, individual.getFitness());
        }

        double fitnessSum = 0.0;
        for (Chromosome individual : population.getPopulation()) {
            double shifted = (double) (individual.getFitness() - minFitness) + 1.0;
            fitnessSum += shifted;
        }

        double accumulated = 0.0;
        for (Chromosome individual : population.getPopulation()) {
            double shifted = (double) (individual.getFitness() - minFitness) + 1.0;
            double rel = shifted / fitnessSum;
            accumulated += rel;

            individual.setRelative_fitness(rel);
            individual.setAcum_fitness(accumulated);
        }

        population.getPopulation().get(population.getPopulation().size()-1).setAcum_fitness(1.0);
    }

    public GAResult evolveWithListener(int nGenerations,
                                       Population population,
                                       double crossProbability,
                                       double mutationProbability,
                                       double elitismRate,
                                       String selectionMethodString,
                                       String crossOverMethodString,
                                       String mutationMethodString,
                                       EvolutionListener listener) {

        this.globalBest = population.getPopulation().get(0).clone();
        evaluateAndNormalize(population);

        int[] bestOfGen = new int[nGenerations];
        int[] globalBestSoFar = new int[nGenerations];
        double[] avgFitness = new double[nGenerations];

        Random rand = new Random();

        for (int gen = 0; gen < nGenerations; gen++) {

            int eliteCount = (int) Math.round(population_size * elitismRate);
            eliteCount = Math.max(0, Math.min(eliteCount, population_size));

            // 1) capturar élite de la población actual
            ArrayList<Chromosome> elite = getElite(population, eliteCount);

            // 2) crear nueva población con tamaño y seleccionar como siempre
            Population newPopulation = new Population(fitness, this.population_size, this.num_cameras, N, M);

            SelectionMethod method = SelectionMethod.valueOf(selectionMethodString);
            Selection.select(method, population, newPopulation, population_size, rand);

            // 3) reinsertar élite al principio
            for (int i = 0; i < elite.size(); i++) {
                newPopulation.swap(i, elite.get(i));
            }

            // 4) cruce y mutación SOLO desde eliteCount
            populationCross(newPopulation, crossProbability, crossOverMethodString, rand, eliteCount);
            mutatePopulation(newPopulation, mutationProbability, mutationMethodString, rand, eliteCount);

            // 5) evaluar/normalizar
            evaluateAndNormalize(newPopulation);

            // stats
            int best = Integer.MIN_VALUE;
            long sum = 0;
            Chromosome bestChr = newPopulation.getPopulation().get(0);

            for (Chromosome c : newPopulation.getPopulation()) {
                int f = c.getFitness();
                sum += f;
                if (f > best) {
                    best = f;
                    bestChr = c;
                }
            }

            double avg = (double) sum / newPopulation.getPopulation().size();

            bestOfGen[gen] = best;
            globalBestSoFar[gen] = this.globalBest.getFitness();
            avgFitness[gen] = avg;

            if (listener != null) {
                listener.onGeneration(gen, bestOfGen[gen], globalBestSoFar[gen], avgFitness[gen], bestChr.clone());
            }

            population = newPopulation;
        }

        return new GAResult(this.globalBest.clone(), bestOfGen, globalBestSoFar, avgFitness);
    }

    private ArrayList<Chromosome> getElite(Population pop, int eliteCount) {
        ArrayList<Chromosome> list = new ArrayList<>(pop.getPopulation());
        list.sort((a, b) -> Integer.compare(b.getFitness(), a.getFitness())); // desc

        ArrayList<Chromosome> elite = new ArrayList<>();
        for (int i = 0; i < eliteCount && i < list.size(); i++) {
            elite.add(list.get(i).clone());
        }
        return elite;
    }
}
