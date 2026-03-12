package practica.real;



public class Fitness {

	private final Board board;


	// Ahora fitness acepta un board, no un map
	public Fitness(Board board) {
		this.board = board;
	}

	// Esta funcion ahora cambia por completo
	public int evaluate(Chromosome individual, boolean ponderado) {

        return 0;
    }
}
