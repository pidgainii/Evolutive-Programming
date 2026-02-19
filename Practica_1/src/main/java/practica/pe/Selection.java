package main.java.practica.pe;

import java.util.Random;

public class Selection {

    public static void roulette(Population actualPopulation, Population newPopulation, int populationSize) {

        for (int i = 0; i < populationSize; i++) {
            Random rand = new Random();
            double prob = rand.nextDouble();

            int pos = 0;
            while (prob > actualPopulation.getPopulation().get(pos).getAcum_fitness()) {
                pos++;
            }

            newPopulation.getPopulation().set(pos, actualPopulation.getPopulation().get(pos));
        }
    }
}
