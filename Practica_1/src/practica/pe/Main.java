package practica.pe;

public class Main {

    public static void main(String[] args) {
        int populationSize = 100;

        int[][] map = new int[][]{
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

        int NUM_CAMARAS = 1;
        int RANGO_VISION = 1;
        int N = 10;
        int M = 10;

        int xBits = Integer.toBinaryString(N - 1).length();
        int yBits = Integer.toBinaryString(M - 1).length();
        int totalBits = NUM_CAMARAS * (xBits + yBits);


        Fitness fitness = new Fitness(map, NUM_CAMARAS, RANGO_VISION, N, M);
        Population population = new Population(fitness, populationSize, totalBits);
        Evolution evolution = new Evolution(fitness, populationSize, totalBits);
        double crossProbability = 0.8;
        double mutationProbability = 0.01;


        Chromosome best = evolution.evolve(100, population, crossProbability, mutationProbability);

        System.out.println("Best fitness: " + best.getFitness());
        System.out.println("Best genes: " + best.getGenes());

    }

}