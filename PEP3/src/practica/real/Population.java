package practica.real;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import practica.ast.*;
import practica.enums.Accion;
import practica.enums.Sensor;


public class Population {

    private final ArrayList<Chromosome> population;

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
    	int individuosPorNivel = population_size / (profMax - profMin + 1);
    	ArrayList<Chromosome> population = new ArrayList<Chromosome>();
    	
    	for (int p = profMin; p < profMax; p++) {
    		for (int i = 1; i < individuosPorNivel; i++) {
    			NodoAST nuevoArbol;
        		if (i <= individuosPorNivel/2) {
        			nuevoArbol = this.generarArbolFull(0, p);
        		} else {
        			nuevoArbol = this.generarArbolGrow(0, p);
        		}
        		
        		Chromosome individuo = new Chromosome(nuevoArbol);
        		individuo.setFitness(fitness.evaluate(individuo));
        		this.population.add(individuo);
        	}
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