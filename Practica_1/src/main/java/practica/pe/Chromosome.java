package main.java.practica.pe;

public class Chromosome {

    private final String genes;
    private int fitness;

    public Chromosome(String genes) {
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
}