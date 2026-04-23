package practica.ast;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import practica.enums.Accion;
import practica.enums.Sensor;

public class Generator {

	private static final Random random = new Random();
	
	public static NodoAST generarArbolFull(int profActual, int profMax) {
    	if (profActual == profMax) {
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
    				listaHijos[i] = generarArbolFull(profActual+1, profMax);
    			}
    			return new NodoBloque(listaHijos);
    		} 
    		// Nodo condicional
    		else {
                Sensor[] sensores = Sensor.values();
                Sensor sensor = sensores[random.nextInt(sensores.length)];
                
                int[] options = {10, 50, 100};
                int umbral = options[new java.util.Random().nextInt(options.length)];
                
                NodoAST hijoIzquierdo = generarArbolFull(profActual+1, profMax);
                NodoAST hijoDerecho = generarArbolFull(profActual+1, profMax);
                
                return new NodoCondicional(sensor, umbral, hijoIzquierdo, hijoDerecho);
    		}
    	}
    }
    
	public static NodoAST generarArbolGrow(int profActual, int profMax) {
	    Random random = new Random();
	    
	    if (profActual == profMax) {
	        Accion[] acciones = Accion.values();
	        Accion accion = acciones[random.nextInt(acciones.length)];
	        return new NodoAccion(accion);
	    } else {
	        // Generamos un valor entre 0, 1, 2 y 3
	        int value = ThreadLocalRandom.current().nextInt(4);

	        // 50% de probabilidad (si sale 0 o 1)
	        if (value < 2) { 
	            Accion[] acciones = Accion.values();
	            Accion accion = acciones[random.nextInt(acciones.length)];
	            return new NodoAccion(accion);
	        }
	        // 25% de probabilidad (si sale 2)
	        else if (value == 2) {
	            int numHijos = ThreadLocalRandom.current().nextBoolean() ? 2 : 3;
	            NodoAST[] listaHijos = new NodoAST[numHijos];
	            for (int i = 0; i < numHijos; i++) {
	                listaHijos[i] = generarArbolGrow(profActual + 1, profMax);
	            }
	            return new NodoBloque(listaHijos);
	        } 
	        // 25% de probabilidad (si sale 3)
	        else {
	            Sensor[] sensores = Sensor.values();
	            Sensor sensor = sensores[random.nextInt(sensores.length)];
	            
	            int[] values = {10, 50, 100};
	            int umbral = values[random.nextInt(values.length)];
	            
	            NodoAST hijoIzquierdo = generarArbolGrow(profActual + 1, profMax);
	            NodoAST hijoDerecho = generarArbolGrow(profActual + 1, profMax);
	            
	            return new NodoCondicional(sensor, umbral, hijoIzquierdo, hijoDerecho);
	        }
	    }
	}
    
    
}
