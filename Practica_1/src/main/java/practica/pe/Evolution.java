package practica.pe;

import java.util.*;

public class Evolution {

    int populationSize = 100;
    ArrayList<String> population;

    int NUM_CAMARAS = 2;
    int RANGO_VISION = 3;
    int N = 5;
    int M = 5;

    void inicializa() {

        population = new ArrayList<>();
        Random rand = new Random();
        int xBits = (int)(Math.log(N)/Math.log(2)) + 1;
        int yBits = (int)(Math.log(M)/Math.log(2)) + 1;

        int totalBits = NUM_CAMARAS * (xBits + yBits);

        for (int i = 0; i < populationSize; i++) {

            StringBuilder gene = new StringBuilder();

            for (int j = 0; j < totalBits; j++) {
                gene.append(rand.nextBoolean() ? '1' : '0');
            }

            population.add(gene.toString());
        }
    }
    void evaluarPoblacion() {

        Set<Integer> set = new HashSet<>();

        for (String individual: population) {

            int xBits = (int)(Math.log(N)/Math.log(2)) + 1;
            int yBits = (int)(Math.log(M)/Math.log(2)) + 1;

            for (int camera = 0; camera < NUM_CAMARAS; camera++) {

                int initial = camera * (xBits + yBits);
                String xBinary = individual.substring(initial, initial + xBits);
                String yBinary = individual.substring(initial + xBits, initial + yBits + xBits);

                int x = Integer.parseInt(xBinary, 2);
                int y = Integer.parseInt(yBinary, 2);

                // check if camera in hashmap then delete one to num

                int[] dx = {-1, 1, 0, 0};
                int[] dy = {0, 0, -1, 1};

                for (int dir = 0; dir < 4; dir++) {
                    for (int r = 1; r <= RANGO_VISION; r++) {

                        // implement directions and sum to the hashmap
                    }
                }
            }
        }
    }

    /*
    evaluarPoblacion(); //evalúa los individuos y coge el mejor
    while (!terminado()) {
        numgeneracion++;
        seleccion();
        cruce();
        mutacion();
        evaluarPoblacion();
        . . .
    }
    devolver mejor;
     */


    int[][] matriz = new int[][]{
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 1, 0, 0, 0, 0, 0, 0, 1, 0},
            {0, 0, 0, 1, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0}
    };


}
