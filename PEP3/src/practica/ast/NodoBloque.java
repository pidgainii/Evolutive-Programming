package practica.ast;

public class NodoBloque extends NodoAST {

	
	private final NodoAST[] hijos;
	
	public NodoBloque(NodoAST[] hijos) {
		this.hijos = hijos;
	}
	
	@Override
	void ejecutar(Contexto contexto) {
		for (NodoAST hijo: this.hijos) {
			hijo.ejecutar(contexto);
		}
		
	}

}
