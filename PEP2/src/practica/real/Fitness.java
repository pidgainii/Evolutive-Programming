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
		
		// En este array vamos a tener un max de 5 posiciones, en las cuales van a estar los costes de cada dron (tiempos)
		ArrayList<Double> costes_dron = new ArrayList<Double>();
		costes_dron.add(0.0);
		
		// indice del dron actual (0 es dron 1 en realidad)
		int current_dron = 0;
		double[] dron_velocity = {1.5, 1.0, 0.7, 1.2, 0.5};
		
		
		
		// necesitamos saber cual es el numero de camaras para saber cuando hay un separador
		int num_camaras = this.board.getNumCamaras();
		
		// Vamos recorriendo la lista de camaras (genes)
		for (int i = 0; i < genes.size()-2; i++) {
			
			int a = genes.get(i);
			int b = genes.get(i + 1);
			
			// Si nos encontramos con un separador, pasamos al siguiente dron
			if (a >= num_camaras) {
				current_dron++;
				costes_dron.add(0.0);
				continue;
			}
			
			if (b >= num_camaras) {
				continue;
			}
			
			// Calcular recorrido de genes.get(i) a genes.get(i+1)
			int costD = this.board.getCoste(a, b);
			
			// Calculamos el tiempo del dron
			double costeTiempo = (double)costD / dron_velocity[current_dron];

			
			// Le sumamos el coste al dron actual
			// aqui habria que ver cual es la velocidad del dron para el coste
			costes_dron.set(current_dron, costes_dron.get(current_dron) + costeTiempo);
			
		}
		
		// fitness = max (T1, T2, ..., TD) + Penalización_Desequilibrio
		double maxT = Collections.max(costes_dron);
		double minT = Collections.min(costes_dron);
		double penalizacionDesequilibrio = (maxT - minT) * 0.5;
		double time = maxT + penalizacionDesequilibrio;
		
		double fitness = 1.0 / (1.0 + time);
		
        return fitness;
    }
	

}











