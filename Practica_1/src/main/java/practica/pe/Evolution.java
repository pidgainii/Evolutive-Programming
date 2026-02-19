package practica.pe;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static practica.pe.Utils.copy;

public class Evolution {

    private int populationSize;
    private int boardSizeX;
    private int boardSizeY;
    private int nCameras;
    private int cameraRange;
    private int[][] board;
    private int crossingProb;
    private int mutationProb;
    private int elitePerc;
    private int nGenerations;
    private String selectionMethod;

    ArrayList<Chromosome> population;


    public Evolution()
    {
        this.populationSize = 0;
        this.boardSizeX = 0;
        this.boardSizeY = 0;
        this.nCameras = 0;
        this.cameraRange = 0;
        this.board = new int[0][];
        this.crossingProb = 0;
        this.mutationProb = 0;
        this.elitePerc = 0;
        this.nGenerations = 0;
        this.selectionMethod = "roulette";

        this.population = new ArrayList<Chromosome>();
    }


    public void setParameters(int populationSize, int boardSizeX,
                              int boardSizeY, int nCameras, int cameraRange,
                              int[][] board, int crossingProb,
                              int mutationProb, int elitePerc, int nGenerations,
                              String selectionMethod)
    {
        this.populationSize = populationSize;
        this.boardSizeX = boardSizeX;
        this.boardSizeY = boardSizeY;
        this.nCameras = nCameras;
        this.cameraRange = cameraRange;
        this.board = board;
        this.crossingProb = crossingProb;
        this.mutationProb = mutationProb;
        this.elitePerc = elitePerc;
        this.nGenerations = nGenerations;
        this.selectionMethod = selectionMethod;

        this.population = new ArrayList<Chromosome>();
    }


    public void evolute(Graphic g, BoardPanel b) {
        // Initializing population with random chromosomes
        for (int i = 0; i < populationSize; i++) {
            this.population.add(new Chromosome(boardSizeX, boardSizeY, nCameras));
        }

        ArrayList<Chromosome> initialPopulation = Utils.copyPopulation(this.population);
        // Array with the scores of every generation
        int[][] scoresGen = new int[nGenerations][initialPopulation.size()];

        // We add the scores of the initial random population
        int[] scoresGen0 = Fitness.getFitnessP(initialPopulation, this.cameraRange, Utils.copy(this.board));
        scoresGen[0] = scoresGen0;

        // Rendering generation 0
        g.newGeneration(0, scoresGen0);



        // Calculating elite for generation 0
        ArrayList<Chromosome> elite = Elite.elite(this.population, scoresGen0, elitePerc);
        this.population.removeAll(elite);


        for (int i = 1; i < nGenerations; i++)
        {
            // Selecting from original population
            switch(this.selectionMethod) {
                case "Ruleta":
                    // Example: We create 90% of the new generation by selecting from current gen
                    //          10% will be the elite
                    // This way we preserve the best in each generation
                    this.population = Selection.roulette(this.population,
                            Fitness.getFitnessP(this.population, this.cameraRange, Utils.copy(this.board)),
                            this.populationSize - elite.size());
                    this.population.addAll(elite);
                case "Torneo":
                    this.population = Selection.tournament(this.population,
                            Fitness.getFitnessP(this.population, this.cameraRange, Utils.copy(this.board)),
                            this.populationSize - elite.size());
                    this.population.addAll(elite);
            }

            // Crossing phase
            this.population = Crossing.crossing(this.population, crossingProb);

            // Mutation phase
            this.population = Mutation.mutation(this.population, mutationProb);


            // Adding scores of generation i to scores array
            int[] scoresGeni = Fitness.getFitnessP(this.population, this.cameraRange, Utils.copy(this.board));
            scoresGen[i] = scoresGeni;


            // Calling graphic elements to render progress
            g.newGeneration(i, scoresGeni);
            // Rendering best chromosome in the board
            int[][] bestChr = Utils.getBoardWithChromosome(Elite.best(this.population, scoresGeni), this.board, this.cameraRange);
            b.setTablero(bestChr);



            // Calculating elite again
            ArrayList<Chromosome> populationPlusElite = Utils.copyPopulation(this.population);
            populationPlusElite.addAll(elite);
            int[] scores = Fitness.getFitnessP(populationPlusElite, this.cameraRange, Utils.copy(this.board));
            elite = Elite.elite(populationPlusElite, scores, elitePerc);

            try {Thread.sleep(5);}
            catch (InterruptedException e) {}
        }


        for (int i = 0; i < nGenerations; i++) {
            System.out.println("Scores for generation " + i);
            System.out.println(Arrays.toString(scoresGen[i]));
        }

    }




}
