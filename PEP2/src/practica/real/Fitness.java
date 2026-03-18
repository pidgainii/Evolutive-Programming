package practica.real;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;



public class Fitness {

	private final Board board;


	// Ahora fitness acepta un board, no un map
	public Fitness(Board board) {
		this.board = board;
	}

	// Esta funcion ahora cambia por completo
	public double evaluate(Chromosome individual) {

	    ArrayList<Integer> genes = individual.getGenes();

	    ArrayList<Double> costes_dron = new ArrayList<>();
	    costes_dron.add(0.0);

	    int current_dron = 0;
	    double[] dron_velocity = {1.5, 1.0, 0.7, 1.2, 0.5};

	    int num_camaras = this.board.getNumCamaras();

	    for (int i = 0; i < genes.size() - 1; i++) {

	        int aId = genes.get(i);
	        int bId = genes.get(i + 1);

	        // separador => cambiamos de dron
	        if (aId > num_camaras) {
	            current_dron++;
	            costes_dron.add(0.0);
	            continue;
	        }

	        // si el siguiente es separador, no calculamos tramo
	        if (bId > num_camaras) {
	            continue;
	        }

	        // convertir IDs 1-based a índices 0-based para la matriz de costes
	        int a0 = aId - 1;
	        int b0 = bId - 1;

	        int costD = this.board.getCoste(a0, b0);

	        double costeTiempo = (double) costD / dron_velocity[current_dron];
	        costes_dron.set(current_dron, costes_dron.get(current_dron) + costeTiempo);
	    }

	    double maxT = Collections.max(costes_dron);
	    double minT = Collections.min(costes_dron);
	    double penalizacionDesequilibrio = (maxT - minT) * 0.5;
	    double time = maxT + penalizacionDesequilibrio;

	    return 1.0 / (1.0 + time);
	}
	
}











