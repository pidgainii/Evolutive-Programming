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
        Population pop = new Population(fitness);
        Evolution evo = new Evolution(fitness, popSize);
        
        return evo.evolveWithListener(gens, pop, pc, pm, elit, "change", "change", "change", listener);
    }
}