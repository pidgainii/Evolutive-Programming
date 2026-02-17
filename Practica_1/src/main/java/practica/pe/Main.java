package practica.pe;

import java.util.ArrayList;




public class Main {
    public static void main(String[] args) {

        final int populationSize = 10;
        final int boardSizeX = 7;
        final int boardSizeY = 7;
        final int nCameras = 2;
        final int cameraRange = 3;

        // Creating population, which is an ArrayList of chromosomes
        ArrayList<Chromosome> population = new ArrayList<Chromosome>(populationSize);

        // Initializing population with random chromosomes
        for (int i = 0; i < populationSize; i++)
        {
            population.add(new Chromosome(boardSizeX, boardSizeY, nCameras));
        }

        int[][] tablero0 = new int[][]{
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0},
        };

        int[][] tablero1 = new int[][]{
                {0, 0, 1, 0, 0, 0, 1},
                {0, 1, 0, 0, 1, 1, 0},
                {0, 1, 0, 0, 1, 0, 0},
                {1, 1, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 0, 0, 0},
                {0, 1, 0, 0, 1, 0, 1},
                {0, 0, 0, 0, 1, 0, 1},
        };

        int[][] tablero2 = new int[][]{
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        };


        Evolution evolution = new Evolution(populationSize, boardSizeX, boardSizeY, nCameras, cameraRange, tablero1);
        evolution.evolute(10);


    }
}