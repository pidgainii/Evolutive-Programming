package main.java.practica.pe;

import java.util.Random;

public class Selection {

    public static void roulette(Population actualPopulation, Population newPopulation, int populationSize) {

        Random rand = new Random();

        for (int i = 0; i < populationSize; i++) {
            double prob = rand.nextDouble();

            int pos = 0;
            while (prob > actualPopulation.getPopulation().get(pos).getAcum_fitness()) {
                pos++;
            }

            newPopulation.getPopulation().set(i, actualPopulation.getPopulation().get(pos).clone());
        }
    }
}
