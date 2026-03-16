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
		int num_camaras = this.board.getCamaras().size();
		
		// Vamos recorriendo la lista de camaras (genes)
		for (int i = 0; i < genes.size()-1; i++) {
			
			// Si nos encontramos con un separador, pasamos al siguiente dron
			if (genes.get(i) > num_camaras) {
				current_dron++;
				costes_dron.add(0.0);
			}
			
			// Calcular recorrido de genes.get(i) a genes.get(i+1)
			int costD = costeDistancia(genes.get(i), genes.get(i+1));
			
			// Calculamos el tiempo del dron
			double costeTiempo = (double)costD / dron_velocity[current_dron];

			
			// Le sumamos el coste al dron actual
			// aqui habria que ver cual es la velocidad del dron para el coste
			costes_dron.add(current_dron, costes_dron.get(current_dron) + costeTiempo);
			
		}
		
		// fitness = max (T1, T2, ..., TD) + Penalización_Desequilibrio
		double maxT = Collections.max(costes_dron);
		double minT = Collections.min(costes_dron);
		double penalizacionDesequilibrio = (maxT - minT) * 0.5;
		double fitness = maxT + penalizacionDesequilibrio;
		
		
        return fitness;
    }
	
	// esta funcion nos devuelve el coste en cuanto a distancia (con penalizacion por camaras en la ruta)
	private int costeDistancia(int camara1, int camara2) {

		
		int coste = 0;
		
		// funcion que nos devuelve la ruta de una camara a otra (sin pasar por muros)
		ArrayList<Pair> ruta = this.board.getRoute(camara1, camara2);
		
		
		// Vamos sumando el coste de cada celda
		for (Pair p: ruta) {
			coste += this.board.getCellCost(p);
		}
		
		// ahora tenemos que ver por cuantas camaras pasa la ruta para sumar penalizacion
		int numCamarasInPath = this.numCamarasInPath(ruta);
		
		
		// Sumamos la penalizacion de 500 por cada camara que este de por medio
		coste += numCamarasInPath*500;
		
		
		return coste;
	}
	
	
	private int numCamarasInPath(ArrayList<Pair> ruta) {
	    Set<Pair> camaras = this.board.getCamarasSet();
	    int count = 0;

	    for (int i = 1; i < ruta.size() - 1; i++) {
	        if (camaras.contains(ruta.get(i))) {
	            count++;
	        }
	    }

	    return count;
	}
}











