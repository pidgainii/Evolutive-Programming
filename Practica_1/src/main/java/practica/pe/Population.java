package main.java.practica.pe;

import main.java.practica.pe.Chromosome;

import java.util.ArrayList;
import java.util.Random;

public class Population {

    private final ArrayList<Chromosome> population;

    public Population(Fitness fitness, int size, int totalBits) {

        this.population = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < size; i++) {

            StringBuilder gene = new StringBuilder();

            for (int j = 0; j < totalBits; j++) {
                gene.append(rand.nextBoolean() ? '1' : '0');
            }

            Chromosome newChromosome = new Chromosome(gene.toString());
            newChromosome.setFitness(fitness.evaluate(newChromosome));
            this.population.add(newChromosome);
        }
    }

    public ArrayList<Chromosome> getPopulation() {
        return population;
    }

    public void swap(int index, Chromosome chromosome) {
        population.set(index, chromosome);
    }
}