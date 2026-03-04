package practica.real;

import java.util.ArrayList;
import java.util.Random;

public class Population {

    private final ArrayList<Chromosome> population;

    public Population(Fitness fitness, int size, int numCameras, int N, int M) {

        this.population = new ArrayList<>();
        Random rand = new Random();

        int totalGenes = 3 * numCameras;
        for (int i = 0; i < size; i++) {

            double[] genes = new double[totalGenes];


            for (int camera = 0; camera < numCameras; camera++) {

                int base = camera * 3;
                // x, y, theta
                genes[base] = rand.nextDouble() * N;
                genes[base + 1] = rand.nextDouble() * M;
                genes[base + 2] = rand.nextDouble() * 360;
            }

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