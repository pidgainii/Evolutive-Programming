package practica.pe;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static practica.pe.Utils.copy;

public class Evolution {

    final int populationSize;
    final int boardSizeX;
    final int boardSizeY;
    final int nCameras;
    final int cameraRange;
    final int[][] board;

    ArrayList<Chromosome> population;


    public Evolution(int populationSize, int boardSizeX,
                     int boardSizeY, int nCameras, int cameraRange,
                     int[][] board)
    {
        this.populationSize = populationSize;
        this.boardSizeX = boardSizeX;
        this.boardSizeY = boardSizeY;
        this.nCameras = nCameras;
        this.cameraRange = cameraRange;
        this.board = board;

        this.population = new ArrayList<Chromosome>();

        // Initializing population with random chromosomes
        for (int i = 0; i < populationSize; i++) {
            this.population.add(new Chromosome(boardSizeX, boardSizeY, nCameras));
        }
    }





    public void evolute(int nGenerations)
    {
        // Getting scores for each chromosome in same order

        ArrayList<Chromosome> newPopulation = new ArrayList<Chromosome>(this.population);



        for (int i = 0; i < nGenerations; i++) {
            ArrayList<Integer> scores = new ArrayList<Integer>();
            int totalScore = 0;
            int score;

            for (Chromosome c: this.population) {
                score = Fitness.getFitness(c.getFenotype(), this.cameraRange, copy(this.board));
                scores.add(score);
                if (score>0) totalScore += score;
            }

            newPopulation = Selection.roulette(newPopulation, scores, totalScore);
        }



        System.out.println("Initial population: ");
        for (Chromosome c: population)
        {
            System.out.println(c.getGenotype());
            System.out.println(c.getFenotype());

            System.out.println("Puntuación: " + Fitness.getFitness(c.getFenotype(), cameraRange, copy(this.board)) + "\n");
        }
        System.out.println("\n\nEvoluted population: ");
        for (Chromosome c: newPopulation)
        {
            System.out.println(c.getGenotype());
            System.out.println(c.getFenotype());

            System.out.println("Puntuación: " + Fitness.getFitness(c.getFenotype(), cameraRange, copy(this.board)) + "\n");
        }


    }

    /*
    inicializa(); //crea población inicial de cromosomas
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


}
