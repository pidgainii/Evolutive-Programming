package practica;

import practica.real.Board;
import practica.real.Fitness;
import practica.real.Population;
import practica.real.Evolution;
import practica.ui.GAResult;
import practica.ui.EvolutionListener;

public class GARunner {

    public static GAResult run(Board board,
                               int popSize,
                               int gens,
                               double pc,
                               double pm,
                               double elit,
                               String sel,
                               String cross,
                               String mut,
                               EvolutionListener listener) {
    	
        Fitness fitness = new Fitness(board);

        int numDrones = 5;

        int numCamaras = board.getNumCamaras();

        Population pop = new Population(fitness, popSize, numCamaras, numDrones);
        Evolution evo = new Evolution(fitness, popSize);

        return evo.evolveWithListener(gens, pop, elit, sel, listener);
    }
}