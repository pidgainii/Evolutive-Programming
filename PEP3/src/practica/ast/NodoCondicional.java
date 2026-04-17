package practica.ast;

import practica.enums.Sensor;

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
	void ejecutar(Contexto contexto) {
		if (contexto.leerSensor(this.sensor) < this.umbral) {
			this.hijoIzquierdo.ejecutar(contexto);
		} else {
			this.hijoDerecho.ejecutar(contexto);
		}
		
	}

}
