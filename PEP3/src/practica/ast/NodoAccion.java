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
		if (!contexto.getAccionTomada()) {
			contexto.ejecutarAccion(this.accion);
			contexto.setAccionTomada(true);
		}
		else contexto.addAccion(accion);
	}

	@Override
	public int tam() {
		return 1;
	}

	@Override
	public NodoAST deepCopy() {
	    return new NodoAccion(this.accion);
	}
	
	@Override
    public NodoAST getSubtree(int[] contador, int objetivo) {
        if (contador[0] == objetivo) return this;
        contador[0]++;
        return null;
    }

    @Override
    public NodoAST replaceSubtree(int[] contador, int objetivo, NodoAST reemplazo) {
        if (contador[0] == objetivo) return reemplazo.deepCopy();
        contador[0]++;
        return new NodoAccion(this.accion);
    }

	@Override
	public boolean isLeaf() {
		return true;
	}

	public Accion getAccion() {
		return this.accion;
	}

	@Override
	public int profundidad() {
		return 1;
	}
	
}
