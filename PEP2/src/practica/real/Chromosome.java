package practica.real;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Chromosome {
	
	
	// Usamos arrayList y no int[] porque podemos hacer shuffle facilmente
	// Además, los cruces y mutaciones van a ser más faciles con ArrayList
	// A no ser que sea muy lento. Si es muy lento, lo cambiamos a int[][]
	private ArrayList<Integer> genes;
    private double fitness;
    private double acum_fitness;
    private double relative_fitness;
    
    
    public Chromosome(ArrayList<Integer> genes) {
    	this.genes = genes;
    }
    
    public Chromosome(Chromosome chromosome) {
        this.genes = new ArrayList<Integer>(chromosome.getGenes());
        this.fitness = chromosome.getFitness();
        this.acum_fitness = chromosome.getAcum_fitness();
        this.relative_fitness = chromosome.getRelative_fitness();
    }
    
    @Override
    public Chromosome clone() {
        return new Chromosome(this);
    }

    public void setGenes(ArrayList<Integer> genes) {
    	this.genes = genes;
    }
    
    public ArrayList<Integer> getGenes() {
        return genes;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
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



}