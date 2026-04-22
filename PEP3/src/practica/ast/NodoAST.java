package practica.ast;

import practica.real.Contexto;

public abstract class NodoAST {
	public abstract void ejecutar(Contexto contexto);
	
	public abstract int tam();
	
	public abstract NodoAST deepCopy();
	
	public abstract NodoAST getSubtree(int[] contador, int objetivo);

    /**
     * Reconstruye el árbol reemplazando el nodo en el índice objetivo por el reemplazo.
     */
    public abstract NodoAST replaceSubtree(int[] contador, int objetivo, NodoAST reemplazo);
    
    public abstract boolean isLeaf();
}
