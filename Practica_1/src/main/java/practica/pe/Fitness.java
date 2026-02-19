package practica.pe;


import java.util.ArrayList;
import java.util.HashSet;

import static practica.pe.Utils.isCoordValid;

public class Fitness {

    public static int getFitness(ArrayList<Pair> fenotype, int cameraRange, int[][] board)
    {
        int fitness = 0;
        HashSet<Pair> seenCoord = Utils.seenCoords(fenotype, cameraRange, Utils.copy(board));

        for (Pair coord: fenotype) {
            if (!isCoordValid(coord, board)) board[coord.x()][coord.y()] = fitness -= 100;
        }

        return fitness + seenCoord.size() * 10;
    }



    public static int[] getFitnessP(ArrayList<Chromosome> population, int cameraRange, int[][] board) {
        int[] scores = new int[population.size()];
        for (int i = 0; i < population.size(); i++) {
            scores[i] = getFitness(population.get(i).getFenotype(), cameraRange, Utils.copy(board));
        }
        return scores;
    }
}
