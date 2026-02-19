package practica.pe;

import java.util.ArrayList;
import java.util.HashSet;

public class Utils {
    public static int[][] copy(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i].clone();
        }
        return copy;
    }

    public static ArrayList<Chromosome> copyPopulation(ArrayList<Chromosome> population) {
        ArrayList<Chromosome> clone = new ArrayList<Chromosome>();
        for (Chromosome c: population) {
            clone.add(new Chromosome(c.getBoardSizeX(), c.getBoardSizeY(), c.getnCameras(), c.getGenotype()));
        }
        return clone;
    }

    public static int[][] getBoardWithChromosome(Chromosome c, int[][] board, int cameraRange) {
        ArrayList<Pair> fenotype = c.getFenotype();
        int[][] newBoard = copy(board);

        // Cameras will be marked as 2
        for (Pair p: fenotype) {
            newBoard[p.x()][p.y()] = 2;
        }

        HashSet<Pair> seenCords = seenCoords(fenotype, cameraRange, copy(board));
        // Seen coordinates will be marked as 3
        for (Pair p: seenCords) {
            newBoard[p.x()][p.y()] = 3;
        }

        return newBoard;
    }

    public static HashSet<Pair> seenCoords(ArrayList<Pair> fenotype, int cameraRange, int[][] board) {
        HashSet<Pair> seenCoord = new HashSet<>();

        boolean a;
        int i;


        for (Pair coord: fenotype) {
            if (isCoordValid(coord, board)) board[coord.x()][coord.y()] = 2;
        }

        // We iterate through each camera's coordinates
        for (Pair coord: fenotype) {
            a = true;
            i = 1;
            // Norte
            while (a && i <= cameraRange)
            {
                Pair n_coord = new Pair(coord.x(), coord.y() - i);
                if (isCoordValid(n_coord, board))
                {
                    i++;
                    seenCoord.add(n_coord);
                }
                else a = false;
            }

            a = true;
            i = 1;
            // Sur
            while (a && i <= cameraRange)
            {
                Pair n_coord = new Pair(coord.x(), coord.y() + i);
                if (isCoordValid(n_coord, board))
                {
                    i++;
                    seenCoord.add(n_coord);
                }
                else a = false;
            }

            a = true;
            i = 1;
            // Este
            while (a && i <= cameraRange)
            {
                Pair n_coord = new Pair(coord.x() + i, coord.y());
                if (isCoordValid(n_coord, board))
                {
                    i++;
                    seenCoord.add(n_coord);
                }
                else a = false;
            }

            a = true;
            i = 1;
            // Oeste
            while (a && i <= cameraRange)
            {
                Pair n_coord = new Pair(coord.x() - i, coord.y());
                if (isCoordValid(n_coord, board))
                {
                    i++;
                    seenCoord.add(n_coord);
                }
                else a = false;
            }

        }
        return seenCoord;
    }

    // Given a board, this function will tell us if our coordinate is valid (inside range and no obstacle)
    public static boolean isCoordValid(Pair coord, int[][] board)
    {
        return (coord.x() >= 0 && coord.x() < board.length
                && coord.y() >= 0 && coord.y() < board[0].length
                && board[coord.x()][coord.y()] == 0
        );
    }
}
