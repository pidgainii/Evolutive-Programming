package practica.ast;

import practica.real.Contexto;

public class NodoBloque extends NodoAST {

	
	private final NodoAST[] hijos;
	
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

}
