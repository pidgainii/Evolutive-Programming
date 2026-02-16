package practica.pe;

import java.util.ArrayList;




public class Main {
    public static void main(String[] args) {

        final int populationSize = 10;
        final int boardSizeX = 10;
        final int boardSizeY = 10;
        final int nCameras = 2;
        final int cameraRange = 3;

        // Creating population, which is an ArrayList of chromosomes
        ArrayList<Chromosome> population = new ArrayList<Chromosome>(populationSize);

        // Initializing population with random chromosomes
        for (int i = 0; i < populationSize; i++)
        {
            population.add(new Chromosome(boardSizeX, boardSizeY, nCameras));
        }


        int[][] tablero1 = new int[][]{
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        };


        for (Chromosome c: population)
        {
            System.out.println(c.getGenotype());
            System.out.println(c.getFenotype());

            System.out.println("Puntuación: " + Fitness.getFitness(c.getFenotype(), cameraRange, tablero1) + "\n");
        }

    }
}