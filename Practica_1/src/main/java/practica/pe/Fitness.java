package practica.pe;


import java.util.ArrayList;
import java.util.HashSet;

public class Fitness {

    public static int getFitness(ArrayList<Pair> fenotype, int cameraRange, int[][] board)
    {
        int fitness = 0;
        HashSet<Pair> seenCoord = new HashSet<>();

        boolean a;
        int i;


        // We iterate through each camera's coordinates
        for (Pair coord: fenotype)
        {
            // If valid, we add it to board obstacles (cameras can't see through cameras)
            // If not valid, we substract 100 points from fitness
            if (isCoordValid(coord, board)) board[coord.x()][coord.y()] = 1;
            else
            {
                fitness -= 100;
                continue;
            }



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
        return fitness + seenCoord.size() * 10;
    }

    // Given a board, this function will tell us if our coordinate is valid (inside range and no obstacle)
    private static boolean isCoordValid(Pair coord, int[][] board)
    {
        return (coord.x() >= 0 && coord.x() < board.length
                && coord.y() >= 0 && coord.y() < board[0].length
                && board[coord.x()][coord.y()] == 0
        );
    }
}
