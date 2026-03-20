package practica.real;

import practica.enums.SelectionMethod;
import practica.enums.CrossoverMethod;
import practica.enums.MutationMethod;
import practica.ui.EvolutionListener;
import practica.ui.GAResult;

import java.util.ArrayList;
import java.util.Random;

public class Evolution {

    private final Fitness fitness;
    private Chromosome globalBest;
    private final int population_size;


    public Evolution(Fitness fitness,  int population_size) {
        this.fitness = fitness;
        this.population_size = population_size;


        globalBest = null;
    }
    
    public void evaluateAndNormalize(Population population) {
    	
    	double sumScores = 0.0;

    	// evaluar y conseguir el menor fitness
    	Chromosome localBest = null;
    	for (Chromosome individual : population.getPopulation()) {
    		individual.setFitness(fitness.evaluate(individual));
    		
    		if (localBest == null || individual.getFitness() < localBest.getFitness()) {
    			localBest = individual;
    		}
    	}
    	
    	// actualizar global fitness
    	if (this.globalBest == null || localBest.getFitness() < globalBest.getFitness()) {
    		this.globalBest = localBest.clone();
    	}
    	
    	double maxFitness = population.getPopulation().stream()
    	        .mapToDouble(Chromosome::getFitness)
    	        .max().orElse(1.0);

    	// recompute sum using inverted scores
    	double sumScoresInv = 0.0;
    	for (Chromosome individual : population.getPopulation()) {
    	    sumScoresInv += (maxFitness - individual.getFitness());
    	}

    	double accumulated = 0.0;
    	for (Chromosome individual : population.getPopulation()) {
    	    double rel = (maxFitness - individual.getFitness()) / sumScoresInv;
    	    accumulated += rel;

    	    individual.setRelative_fitness(rel);
    	    individual.setAcum_fitness(accumulated);
    	}

        // force exact 1.0 to prevent pointer overflow in roulette/SUS
        population.getPopulation().get(population.getPopulation().size() - 1).setAcum_fitness(1.0);
    	
    }

    public GAResult evolveWithListener(int nGenerations,
                                       Population population,
                                       double elitismRate,
                                       String selectionMethodString,
                                       double pc,
                                       String crossoverMethodString,
                                       double pm, 
                                       String mutationMethodString,
                                       EvolutionListener listener) {

        this.globalBest = population.getPopulation().get(0).clone();
        evaluateAndNormalize(population);

        double[] bestOfGen = new double[nGenerations];
        double[] globalBestSoFar = new double[nGenerations];
        double[] avgFitness = new double[nGenerations];

        Random rand = new Random();

        for (int gen = 0; gen < nGenerations; gen++) {

            int eliteCount = (int) Math.round(population_size * elitismRate);
            eliteCount = Math.max(0, Math.min(eliteCount, population_size));

            // 1) capturar élite de la población actual
            ArrayList<Chromosome> elite = getElite(population, eliteCount);


            Population newPopulation = new Population();

            SelectionMethod method = SelectionMethod.valueOf(selectionMethodString);
            Selection.select(method, population, newPopulation, population_size, rand);

            // 3) reinsertar élite al principio
            for (int i = 0; i < elite.size(); i++) {
                newPopulation.swap(i, elite.get(i));
            }
            
            CrossoverMethod crossMethod = CrossoverMethod.valueOf(crossoverMethodString);
            for (int i = eliteCount; i + 1 < population_size; i += 2) {
                if (rand.nextDouble() < pc) {
                    Chromosome p1 = newPopulation.getPopulation().get(i);
                    Chromosome p2 = newPopulation.getPopulation().get(i + 1);

                    Chromosome[] kids = Crossover.cross(crossMethod, p1, p2, rand);

                    newPopulation.swap(i, kids[0]);
                    newPopulation.swap(i + 1, kids[1]);
                }
            }
            
            MutationMethod mutMethod = MutationMethod.valueOf(mutationMethodString);

            // tras el bucle de cruce:
            for (int i = eliteCount; i < population_size; i++) {
                Mutation.mutate(mutMethod, newPopulation.getPopulation().get(i), rand, pm, fitness);
            }

            // 5) evaluar/normalizar
            evaluateAndNormalize(newPopulation);

            // stats
            double best = Double.POSITIVE_INFINITY;
            double sum = 0;
            Chromosome bestChr = newPopulation.getPopulation().get(0);

            for (Chromosome c : newPopulation.getPopulation()) {
            	double f = c.getFitness();
                sum += f;
                if (f < best) {
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
        list.sort((a, b) -> Double.compare(a.getFitness(), b.getFitness())); // asc

        ArrayList<Chromosome> elite = new ArrayList<>();
        for (int i = 0; i < eliteCount && i < list.size(); i++) {
            elite.add(list.get(i).clone());
        }
        return elite;
    }
}
