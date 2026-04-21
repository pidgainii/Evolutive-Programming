package practica.ast;

import practica.enums.Sensor;
import practica.real.Contexto;

public class NodoCondicional extends NodoAST {

	private final Sensor sensor;
	private final int umbral;
	private final NodoAST hijoIzquierdo;
	private final NodoAST hijoDerecho;
	
	public NodoCondicional(Sensor sensor, int umbral, NodoAST izq, NodoAST der) {
		this.sensor = sensor;
		this.umbral = umbral;
		this.hijoIzquierdo = izq;
		this.hijoDerecho = der;
	}
	
	@Override
	public void ejecutar(Contexto contexto) {
		if (contexto.leerSensor(this.sensor) < this.umbral) {
			this.hijoIzquierdo.ejecutar(contexto);
		} else {
			this.hijoDerecho.ejecutar(contexto);
		}
		
	}

	@Override
	public int tam() {
		return 1 + this.hijoIzquierdo.tam() + this.hijoDerecho.tam();
	}

	@Override
	public NodoAST deepCopy() {
	    return new NodoCondicional(
	        this.sensor, 
	        this.umbral, 
	        this.hijoIzquierdo.deepCopy(), 
	        this.hijoDerecho.deepCopy()
	    );
	}

}
