package practica.ast;

import practica.real.Contexto;

public abstract class NodoAST {
	public abstract void ejecutar(Contexto contexto);
	
	public abstract int tam();
	
	public abstract NodoAST deepCopy();
}
