package practica.ast;

import practica.enums.Accion;
import practica.real.Contexto;

public class NodoAccion extends NodoAST {

	private final Accion accion;
	
	public NodoAccion(Accion accion) {
		this.accion = accion;
	}
	
	@Override
	public void ejecutar(Contexto contexto) {
		contexto.ejecutarAccion(this.accion);
		
	}

	@Override
	public int tam() {
		return 1;
	}

	@Override
	public NodoAST deepCopy() {
	    return new NodoAccion(this.accion);
	}
	
}
