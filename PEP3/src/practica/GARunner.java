package practica;

import practica.real.Contexto;
import practica.real.Fitness;
import practica.real.Population;
import practica.real.Evolution;
import practica.ui.GAResult;
import practica.ui.EvolutionListener;

public class GARunner {

    public static GAResult run(
            Contexto c1,
            Contexto c2,
            Contexto c3,
            int tam_poblacion,
            int generaciones,
            double probC,
            double probM,
            double elitismo,
            String tipoSeleccion,
            String tipoMutacion,
            int profundidad,
            boolean poda,
            int profPoda,
            double bloating,
            EvolutionListener listener) {
    
        Fitness fitness = new Fitness(c1, c2, c3, bloating);
        Population pop = new Population(fitness, tam_poblacion, 1, profundidad);
        Evolution evo = new Evolution(fitness, tam_poblacion);

        return evo.evolveWithListener(generaciones, pop, elitismo, probC, probM, tipoSeleccion, tipoMutacion, poda, profPoda, listener);
    }
}