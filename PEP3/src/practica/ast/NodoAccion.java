package practica.ast;

import practica.enums.Accion;
import practica.real.Contexto;

public class NodoAccion extends NodoAST {

	private final Accion accion;
	
	public NodoAccion(Accion accion) {
		this.accion = accion;
	}
	
	@Override
	void ejecutar(Contexto contexto) {
		contexto.ejecutarAccion(this.accion);
		
	}
	
}
