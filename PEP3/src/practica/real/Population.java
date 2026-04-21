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
        
        // 1. Calculate number of levels (inclusive)
        int numNiveles = (profMax - profMin + 1);
        int individuosPorNivel = population_size / numNiveles;
        
        // 2. Iterate through each depth level
        for (int p = profMin; p <= profMax; p++) {
            for (int i = 0; i < individuosPorNivel; i++) {
                NodoAST nuevoArbol;
                
                // Standard Ramped Half-and-Half: 50% Full, 50% Grow
                if (i < individuosPorNivel / 2) {
                    nuevoArbol = this.generarArbolFull(0, p);
                } else {
                    nuevoArbol = this.generarArbolGrow(0, p);
                }
                
                Chromosome individuo = new Chromosome(nuevoArbol);
                // Note: evaluate_final handles the 3-map average + bloating as per requirements
                individuo.setFitness(fitness.evaluate_final(individuo));
                population.add(individuo);
            }
        }
        
        // 3. FILL THE GAP (The Remainder)
        // If population_size was 300 and levels were 4, 300/4 is 75 (perfect).
        // But if it was 299, you'd be missing individuals. This ensures you hit exactly the target.
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
    		// Seleccionamos una Accion aleatoria
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
                
                // TODO: Poner el umbral en otro sitio
                int umbral = ThreadLocalRandom.current().nextInt(1, 16);
                
                NodoAST hijoIzquierdo = this.generarArbolFull(profActual+1, profMax);
                NodoAST hijoDerecho = this.generarArbolFull(profActual+1, profMax);
                
                return new NodoCondicional(sensor, umbral, hijoIzquierdo, hijoDerecho);
    		}
    	}
    }
    
    private NodoAST generarArbolGrow(int profActual, int profMax) {
    	if (profActual == profMax) {
    		// Seleccionamos una Accion aleatoria
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
                
                // TODO: Poner el umbral en otro sitio
                int umbral = ThreadLocalRandom.current().nextInt(1, 16);
                
                NodoAST hijoIzquierdo = this.generarArbolGrow(profActual+1, profMax);
                NodoAST hijoDerecho = this.generarArbolGrow(profActual+1, profMax);
                
                return new NodoCondicional(sensor, umbral, hijoIzquierdo, hijoDerecho);
    		}
    	}
    }
    
    
    
    
    
    
}