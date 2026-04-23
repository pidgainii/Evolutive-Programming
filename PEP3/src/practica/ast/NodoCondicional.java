package practica.ast;

import practica.enums.Sensor;
import practica.real.Contexto;

public class NodoCondicional extends NodoAST {

	public final Sensor sensor;
	public final int umbral;
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
	
	@Override
    public NodoAST getSubtree(int[] contador, int objetivo) {
        if (contador[0] == objetivo) return this;
        contador[0]++;
        
        NodoAST izq = this.hijoIzquierdo.getSubtree(contador, objetivo);
        if (izq != null) return izq;
        
        return this.hijoDerecho.getSubtree(contador, objetivo);
    }

    @Override
    public NodoAST replaceSubtree(int[] contador, int objetivo, NodoAST reemplazo) {
        if (contador[0] == objetivo) return reemplazo.deepCopy();
        contador[0]++;
        
        NodoAST nuevoIzq = this.hijoIzquierdo.replaceSubtree(contador, objetivo, reemplazo);
        NodoAST nuevoDer = this.hijoDerecho.replaceSubtree(contador, objetivo, reemplazo);
        
        return new NodoCondicional(this.sensor, this.umbral, nuevoIzq, nuevoDer);
    }

	@Override
	public boolean isLeaf() {
		return false;
	}
	
	public NodoAST getHijoIzquierdo() { return this.hijoIzquierdo; }
	public NodoAST getHijoDerecho() { return this.hijoDerecho; }
	
	@Override
	public int profundidad() {
	    int profIzq = (hijoIzquierdo != null) ? hijoIzquierdo.profundidad() : 0;
	    int profDer = (hijoDerecho != null) ? hijoDerecho.profundidad() : 0;
	    
	    return 1 + Math.max(profIzq, profDer);
	}

	public Sensor getSensor() {
		return this.sensor;
	}

	public int getUmbral() {
		return this.umbral;
	}

}
