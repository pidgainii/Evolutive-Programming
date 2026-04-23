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
                    nuevoArbol = this.generarArbolFull(0, p);
                } else {
                    nuevoArbol = this.generarArbolGrow(0, p);
                }
                
                Chromosome individuo = new Chromosome(nuevoArbol);
                individuo.setFitness(fitness.evaluate_final(individuo));
                population.add(individuo);
            }
        }

        // Para rellenar la poblacion (salen menos con el codigo de antes)
        while (population.size() < population_size) {
            NodoAST extra = this.generarArbolGrow(0, profMax);
            Chromosome extraInd = new Chromosome(extra);
            extraInd.setFitness(fitness.evaluate_final(extraInd));
            population.add(extraInd);
        }
        
        return population;
    }
    
    private NodoAST generarArbolFull(int profActual, int profMax) {
    	if (profActual == profMax) {
    		Random random = new Random();
            Accion[] acciones = Accion.values();
            Accion accion = acciones[random.nextInt(acciones.length)];

    		
    		return new NodoAccion(accion);
    	}
    	else
    	{
    		// Nodo bloque
    		if (ThreadLocalRandom.current().nextBoolean()) {
    			int numHijos = ThreadLocalRandom.current().nextBoolean() ? 2 : 3;
    			NodoAST[] listaHijos = new NodoAST[numHijos];
    			for (int i = 0; i < numHijos; i++) {
    				listaHijos[i] = this.generarArbolFull(profActual+1, profMax);
    			}
    			return new NodoBloque(listaHijos);
    		} 
    		// Nodo condicional
    		else {
    			Random random = new Random();
                Sensor[] sensores = Sensor.values();
                Sensor sensor = sensores[random.nextInt(sensores.length)];
                
                int[] options = {10, 50, 100};
                int umbral = options[new java.util.Random().nextInt(options.length)];
                
                NodoAST hijoIzquierdo = this.generarArbolFull(profActual+1, profMax);
                NodoAST hijoDerecho = this.generarArbolFull(profActual+1, profMax);
                
                return new NodoCondicional(sensor, umbral, hijoIzquierdo, hijoDerecho);
    		}
    	}
    }
    
    private NodoAST generarArbolGrow(int profActual, int profMax) {
    	if (profActual == profMax) {
    		Random random = new Random();
            Accion[] acciones = Accion.values();
            Accion accion = acciones[random.nextInt(acciones.length)];

    		
    		return new NodoAccion(accion);
    	}
    	else
    	{
    		int value = ThreadLocalRandom.current().nextInt(3);
    		
    		
    		if (value == 0) {
    			Random random = new Random();
                Accion[] acciones = Accion.values();
                Accion accion = acciones[random.nextInt(acciones.length)];
        		return new NodoAccion(accion);
    		}
    		
    		// Nodo bloque
    		else if (value == 1) {
    			int numHijos = ThreadLocalRandom.current().nextBoolean() ? 2 : 3;
    			NodoAST[] listaHijos = new NodoAST[numHijos];
    			for (int i = 0; i < numHijos; i++) {
    				listaHijos[i] = this.generarArbolGrow(profActual+1, profMax);
    			}
    			return new NodoBloque(listaHijos);
    		} 
    		// Nodo condicional
    		else {
    			Random random = new Random();
                Sensor[] sensores = Sensor.values();
                Sensor sensor = sensores[random.nextInt(sensores.length)];
                
                int[] values = {10, 50, 100};
                int umbral = values[new java.util.Random().nextInt(values.length)];
                
                NodoAST hijoIzquierdo = this.generarArbolGrow(profActual+1, profMax);
                NodoAST hijoDerecho = this.generarArbolGrow(profActual+1, profMax);
                
                return new NodoCondicional(sensor, umbral, hijoIzquierdo, hijoDerecho);
    		}
    	}
    }
    
    
    
    
    
    
}