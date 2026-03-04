package practica.binary;

import practica.enums.SelectionMethod;
import practica.ui.EvolutionListener;
import practica.ui.GAResult;

import java.util.ArrayList;
import java.util.Random;

public class Evolution {

    private final Fitness fitness;
    private Chromosome globalBest;
    private final int population_size;
    private final int total_bits;
    private final boolean ponderado;

    public Evolution(Fitness fitness,  int population_size,  int total_bits, boolean ponderado) {
        this.fitness = fitness;
        this.population_size = population_size;
        this.total_bits = total_bits;
        globalBest = null;
        this.ponderado = ponderado;
        
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
            Chromosome[] children = (cm == CrossoverMethod.UNIFORM)
                    ? father1.crossUniform(father2, rand)
                    : father1.cross(father2, rand);

            population.swap(idx1, children[0]);
            population.swap(idx2, children[1]);
        }
    }

    public void mutatePopulation(Population population,
                                 double mutationProbability,
                                 Random rand,
                                 int startIdx) {

        for (int idx = startIdx; idx < population.getPopulation().size(); idx++) {
            Chromosome individual = population.getPopulation().get(idx);

            boolean mutated = false;
            char[] genesArray = individual.getGenes().toCharArray();

            for (int i = 0; i < genesArray.length; i++) {
                if (rand.nextDouble() < mutationProbability) {
                    genesArray[i] = (genesArray[i] == '1') ? '0' : '1';
                    mutated = true;
                }
            }

            if (mutated) individual.setGenes(new String(genesArray));
        }
    }

    public void evaluateAndNormalize(Population population) {

        for (Chromosome individual : population.getPopulation()) {
            individual.setFitness(fitness.evaluate(individual, ponderado));
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
        int maxFitness = Integer.MIN_VALUE;
        for (Chromosome individual : population.getPopulation()) {
            minFitness = Math.min(minFitness, individual.getFitness());
            maxFitness = Math.max(maxFitness, individual.getFitness());
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
                                       String crossoverMethodString,
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

            // 1) capturar élite de la población actual (ya está evaluada/normalizada)
            ArrayList<Chromosome> elite = getElite(population, eliteCount);

            // 2) crear población nueva (con tamaño) y aplicar selección como siempre
            Population newPopulation = new Population(fitness, this.population_size, this.total_bits, this.ponderado);

            SelectionMethod method = SelectionMethod.valueOf(selectionMethodString);
            Selection.select(method, population, newPopulation, population_size, rand);

            // 3) reinsertar élite al principio (así no se pierde)
            for (int i = 0; i < elite.size(); i++) {
                newPopulation.swap(i, elite.get(i));
            }

            // 4) cruce y mutación SOLO desde eliteCount
            populationCross(newPopulation, crossProbability, crossoverMethodString, rand, eliteCount);
            mutatePopulation(newPopulation, mutationProbability, rand, eliteCount);

            // 5) evaluar/normalizar
            evaluateAndNormalize(newPopulation);

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
