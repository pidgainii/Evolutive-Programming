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
                               int numVans,
                               EvolutionListener listener) {
        
        Fitness fitness = new Fitness(board, numVans);

        // Obtenemos el número de entregas en lugar del antiguo número de cámaras
        int numDeliveries = board.getDeliveryPoints().size();

        // Inicializamos la población sabiendo cuántos puntos reales hay que permutar
        Population pop = new Population(fitness, popSize, numDeliveries, numVans);
        Evolution evo = new Evolution(fitness, popSize);

        return evo.evolveWithListener(gens, pop, elit, sel, pc, cross, pm, mut, listener);
    }
}