package practica.pe;

import java.util.Random;
import java.util.ArrayList;

public class Selection {


    public static ArrayList<Chromosome> roulette(ArrayList<Chromosome> population, ArrayList<Integer> scores, int totalScore)
    {
        // Selection proccess
        // Now we are going to create a probability array
        // where each element is an index pointing to a chromosome of the population arraylist
        short[] prob = new short[totalScore/10];
        int counter = 0;

        int populationSize = population.size();
        for (int i = 0; i < populationSize; i++) {
            if (scores.get(i) < 0) continue;
            int nProb = scores.get(i) / 10;
            for (int j = 0; j < nProb; j++) {
                prob[counter] = (short) i;
                counter++;
            }
        }


        // selection using probability array
        ArrayList<Chromosome> newPopulation = new ArrayList<Chromosome>();
        Random random = new Random();
        int roulettePointer;

        for (int i = 0; i < populationSize; i++) {
            roulettePointer = random.nextInt(prob.length);
            newPopulation.add(population.get(prob[roulettePointer]));
        }

        return newPopulation;
    }


}
