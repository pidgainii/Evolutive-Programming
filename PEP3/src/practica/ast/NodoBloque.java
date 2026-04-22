package practica.ast;

import practica.real.Contexto;

public class NodoBloque extends NodoAST {

	
	public final NodoAST[] hijos;
	
	public NodoBloque(NodoAST[] hijos) {
		this.hijos = hijos;
	}
	
	@Override
	public void ejecutar(Contexto contexto) {
		for (NodoAST hijo: this.hijos) {
			hijo.ejecutar(contexto);
		}
	}

	@Override
	public int tam() {
		int tam_hijos = 0;
		for (int i = 0; i < this.hijos.length; i++) {
			tam_hijos += this.hijos[i].tam();
		}
		
		return 1 + tam_hijos;
	}

	@Override
	public NodoAST deepCopy() {
	    NodoAST[] copiasHijos = new NodoAST[this.hijos.length];
	    for (int i = 0; i < this.hijos.length; i++) {
	        // Recursively copy each child node
	        copiasHijos[i] = this.hijos[i].deepCopy();
	    }
	    return new NodoBloque(copiasHijos);
	}
	
	@Override
    public NodoAST getSubtree(int[] contador, int objetivo) {
        if (contador[0] == objetivo) return this;
        contador[0]++;
        
        for (NodoAST hijo : this.hijos) {
            NodoAST res = hijo.getSubtree(contador, objetivo);
            if (res != null) return res;
        }
        return null;
    }

    @Override
    public NodoAST replaceSubtree(int[] contador, int objetivo, NodoAST reemplazo) {
        if (contador[0] == objetivo) return reemplazo.deepCopy();
        contador[0]++;
        
        NodoAST[] nuevosHijos = new NodoAST[this.hijos.length];
        for (int i = 0; i < this.hijos.length; i++) {
            nuevosHijos[i] = this.hijos[i].replaceSubtree(contador, objetivo, reemplazo);
        }
        
        return new NodoBloque(nuevosHijos);
    }

	@Override
	public boolean isLeaf() {
		return false;
	}

}
