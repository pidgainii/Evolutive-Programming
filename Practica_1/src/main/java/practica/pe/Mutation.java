package practica.pe;

import java.util.ArrayList;
import java.util.Random;

public class Mutation {


    public static ArrayList<Chromosome> mutation(ArrayList<Chromosome> population, double prob) {
        ArrayList<Chromosome> newPopulation = Utils.copyPopulation(population);
        StringBuilder genotype;

        double probability = (double)(prob) / 100.0;
        for (Chromosome c: newPopulation) {
            genotype = c.getGenotype();
            for (int i = 0; i < genotype.length(); i++) {
                // we mutate bit with probability: prob
                if (Math.random() < probability) {
                    if (genotype.charAt(i) == 0) genotype.setCharAt(i, '1');
                    else genotype.setCharAt(i, '0');
                }
            }
            c.setGenotype(genotype);
        }

        return newPopulation;
    }
}
