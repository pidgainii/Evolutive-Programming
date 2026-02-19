package practica.pe;

import java.util.Random;
import java.util.ArrayList;

public class Selection {


    public static ArrayList<Chromosome> roulette(ArrayList<Chromosome> population, int[] scores, int sizeNewPopulation)
    {
        // Selection proccess
        // Now we are going to create a probability array
        // where each element is an index pointing to a chromosome of the population arraylist
        int totalScore = 0;
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > 0) totalScore+=scores[i];
        }

        Random random = new Random();

        short[] prob;
        if (totalScore == 0) {
            prob = new short[1];
            prob[0] = (short)random.nextInt(population.size());
        }
        else {
            prob = new short[totalScore / 10];
            int counter = 0;

            for (int i = 0; i < population.size(); i++) {
                if (scores[i] < 0) continue;
                int nProb = scores[i] / 10;
                for (int j = 0; j < nProb; j++) {
                    prob[counter] = (short) i;
                    counter++;
                }
            }
        }

        // selection using probability array
        ArrayList<Chromosome> newPopulation = new ArrayList<Chromosome>();
        int roulettePointer;

        for (int i = 0; i < sizeNewPopulation; i++) {
            roulettePointer = random.nextInt(prob.length);
            newPopulation.add(population.get(prob[roulettePointer]));
        }

        return newPopulation;
    }


    public static ArrayList<Chromosome> tournament(ArrayList<Chromosome> population, int[] scores, int sizeNewPopulation) {
        ArrayList<Chromosome> newPopulation = new ArrayList<Chromosome>();
        for (int i = 0; i < sizeNewPopulation; i++) {
            Random random = new Random();
            int index1 = random.nextInt(population.size());
            int index2 = random.nextInt(population.size());

            if (scores[index1] > scores[index2]) newPopulation.add(population.get(index1));
            else newPopulation.add(population.get(index2));
        }
        return newPopulation;
    }


}
