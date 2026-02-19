package main.java.practica.pe;

import main.java.practica.pe.Population;

import java.util.*;

public class Evolution {

    private final Fitness fitness;
    private Chromosome globalBest;
    private final int population_size;
    private final int total_bits;

    public Evolution(Fitness fitness,  int population_size,  int total_bits) {
        this.fitness = fitness;
        this.population_size = population_size;
        this.total_bits = total_bits;
        globalBest = null;
    }

    public Chromosome evolve(int nGenerations, Population population, double crossProbability, double mutationProbability) {

        this.globalBest = population.getPopulation().getFirst().clone();
        evaluateAndNormalize(population);

        Random rand = new Random();

        for (int i = 0; i < nGenerations; i++) {

            Population newPopulation = new Population(fitness, this.population_size, this.total_bits);

            Selection.roulette(population, newPopulation, population_size);

            populationCross(newPopulation, crossProbability);

            mutatePopulation(newPopulation, mutationProbability);

            evaluateAndNormalize(newPopulation);

            population = newPopulation;

        }

        return this.globalBest;
    }

    public void populationCross(Population population, double crossProbability) {

        ArrayList<Chromosome> selected = new ArrayList<>();
        Random rand = new Random();

        for (Chromosome individual : population.getPopulation()) {
            if (rand.nextDouble() < crossProbability) {
                selected.add(individual);
            }
        }

        if (selected.size() % 2 != 0) {
            selected.removeLast();
        }

        for (int i = 0; i < selected.size(); i += 2) {

            Chromosome father1 =  selected.get(i);
            Chromosome father2 = selected.get(i + 1);

            Chromosome[] children = father1.cross(father2);

            population.swap(i, children[0]);
            population.swap(i + 1, children[1]);
        }
    }

    public void mutatePopulation(Population population, double mutationProbability) {

        Random rand = new Random();
        for (Chromosome individual : population.getPopulation()) {
            boolean mutated = false;
            char[] genesArray = individual.getGenes().toCharArray();

            for (int i = 0; i < individual.getGenes().length(); i++) {

                if (rand.nextDouble() < mutationProbability) {
                    genesArray[i] = (genesArray[i] == '1') ? '0' : '1';
                    mutated = true;
                }
            }

            if (mutated) {
                individual.setGenes(new String(genesArray));
                individual.setFitness(fitness.evaluate(individual));
            }
        }
    }

    public void evaluateAndNormalize(Population population) {

        int fitnessSum = 0;
        Chromosome localBest = population.getPopulation().getFirst();

        for (Chromosome individual : population.getPopulation()) {
            fitnessSum += individual.getFitness();
            if (individual.getFitness() > this.globalBest.getFitness()) {
                localBest = individual;
            }
        }

        if (localBest.getFitness() > this.globalBest.getFitness()) {
           this.globalBest = localBest.clone();
        }

        double acumulated = 0;
        for  (Chromosome individual : population.getPopulation()) {
            individual.setRelative_fitness((double) individual.getFitness() / fitnessSum);
            acumulated += individual.getRelative_fitness();
            individual.setAcum_fitness(acumulated);
        }
    }
}
