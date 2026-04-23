package practica.real;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import practica.ast.*;
import practica.enums.Accion;
import practica.enums.Sensor;


public class Population {

    private ArrayList<Chromosome> population;

    public Population(Fitness fitness, int population_size, int profMin, int profMax) {
        this.population = this.InicializarPoblacionRampedHalfAndHalf(population_size, profMin, profMax, fitness);
    }

    public Population() {
        this.population = new ArrayList<>();
    }

    public ArrayList<Chromosome> getPopulation() {
        return population;
    }

    public void swap(int index, Chromosome chromosome) {
        population.set(index, chromosome);
    }
    
    
    private ArrayList<Chromosome> InicializarPoblacionRampedHalfAndHalf(int population_size, int profMin, int profMax, Fitness fitness) {
        ArrayList<Chromosome> population = new ArrayList<Chromosome>();
        
        int numNiveles = (profMax - profMin + 1);
        int individuosPorNivel = population_size / numNiveles;
        
        for (int p = profMin; p <= profMax; p++) {
            for (int i = 0; i < individuosPorNivel; i++) {
                NodoAST nuevoArbol;
                
                if (i < individuosPorNivel / 2) {
                    nuevoArbol = Generator.generarArbolFull(0, p);
                } else {
                    nuevoArbol = Generator.generarArbolGrow(0, p);
                }
                
                Chromosome individuo = new Chromosome(nuevoArbol);
                individuo.setFitness(fitness.evaluate_final(individuo));
                population.add(individuo);
            }
        }

        // Para rellenar la poblacion (salen menos con el codigo de antes)
        while (population.size() < population_size) {
            NodoAST extra = Generator.generarArbolGrow(0, profMax);
            Chromosome extraInd = new Chromosome(extra);
            extraInd.setFitness(fitness.evaluate_final(extraInd));
            population.add(extraInd);
        }
        
        return population;
    }
}