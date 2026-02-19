package main.java.practica.pe;

import main.java.practica.pe.Chromosome;

import java.util.ArrayList;
import java.util.Random;

public class Population {

    private final ArrayList<Chromosome> population;

    public Population(int size, int totalBits) {

        this.population = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < size; i++) {

            StringBuilder gene = new StringBuilder();

            for (int j = 0; j < totalBits; j++) {
                gene.append(rand.nextBoolean() ? '1' : '0');
            }

            this.population.add(new Chromosome(gene.toString()));
        }
    }

    public ArrayList<Chromosome> getPopulation() {
        return population;
    }
}