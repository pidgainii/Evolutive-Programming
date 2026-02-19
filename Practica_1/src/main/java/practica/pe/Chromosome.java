package main.java.practica.pe;

import java.awt.*;
import java.util.Random;

public class Chromosome {

    private String genes;
    private int fitness;
    private double acum_fitness;
    private double relative_fitness;

    public Chromosome(String genes) {
        this.genes = genes;
    }

    public Chromosome(Chromosome chromosome) {
        this.genes = chromosome.getGenes();
        this.fitness = chromosome.getFitness();
        this.acum_fitness = chromosome.getAcum_fitness();
        this.relative_fitness = chromosome.getRelative_fitness();
    }

    @Override
    public Chromosome clone() {
        return new Chromosome(this);
    }

    public void setGenes(String genes) {
        this.genes = genes;
    }

    public String getGenes() {
        return genes;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public double getAcum_fitness() {
        return acum_fitness;
    }

    public void setAcum_fitness(double acum_fitness) {
        this.acum_fitness = acum_fitness;
    }

    public double getRelative_fitness() {
        return relative_fitness;
    }

    public void setRelative_fitness(double relative_fitness) {
        this.relative_fitness = relative_fitness;
    }

    public Chromosome[] cross(Chromosome other) {

        Random rand = new Random();
        int point = rand.nextInt(genes.length());

        String childGenes1 =
                genes.substring(0, point) +
                        other.genes.substring(point);

        String childGenes2 =
                other.genes.substring(0, point) +
                        genes.substring(point);

        return new Chromosome[]{
                new Chromosome(childGenes1),
                new Chromosome(childGenes2)
        };
    }

}