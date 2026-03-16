package practica.real;

import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

public class Population {

    private final ArrayList<Chromosome> population;

    public Population(Fitness fitness, int population_size, int num_camaras, int num_drones) {

    	
        this.population = new ArrayList<>();
        int totalGenes = num_camaras + num_drones - 1;
        
        ArrayList<Integer> ordered_list = new ArrayList<>();
        for (int i = 0; i < totalGenes; i++) {
        	ordered_list.add(i);
        }
        
        
        for (int i = 0; i < population_size; i++) {
        	// NUESTRO CROMOSOMA SERÁ UNA PERMUTACION
        	ArrayList<Integer> genes = new ArrayList<Integer>(ordered_list);
            Collections.shuffle(genes);
            
            
            Chromosome newChromosome = new Chromosome(genes);
            newChromosome.setFitness(fitness.evaluate(newChromosome));
            population.add(newChromosome);
        }

    }

    public Population() {
        this.population = new ArrayList<>();
    }

    public ArrayList<Chromosome> getPopulation() {
        return population;
    }

    public void swap(int index, Chromosome chromosome) {
        population.set(index, chromosome);
    }
}