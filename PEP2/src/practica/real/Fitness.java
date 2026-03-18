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

	public double evaluate(Chromosome individual) {

	    ArrayList<Integer> genes = individual.getGenes();

	    ArrayList<Double> costes_dron = new ArrayList<>();
	    costes_dron.add(0.0);

	    int current_dron = 0;
	    double[] dron_velocity = {1.5, 1.0, 0.7, 1.2, 0.5};

	    int num_camaras = this.board.getNumCamaras();

	    Integer prevCam = null; // última cámara del dron actual (para segmentos y para volver a base)
	    boolean started = false; // si este dron ya ha salido de base

	    for (int i = 0; i < genes.size(); i++) {
	        int g = genes.get(i);

	        // separador => cerrar dron (volver a base) y pasar al siguiente
	        if (g > num_camaras) {
	            if (prevCam != null) {
	                int costBack = board.getCosteBaseCam(prevCam);
	                costes_dron.set(current_dron,
	                        costes_dron.get(current_dron) + costBack / dron_velocity[current_dron]);
	            }

	            current_dron++;
	            costes_dron.add(0.0);
	            prevCam = null;
	            started = false;
	            continue;
	        }

	        // gen es cámara
	        int cam = g;

	        // primera cámara del dron: base -> cam
	        if (!started) {
	            int costOut = board.getCosteBaseCam(cam);
	            costes_dron.set(current_dron,
	                    costes_dron.get(current_dron) + costOut / dron_velocity[current_dron]);
	            started = true;
	            prevCam = cam;
	            continue;
	        }

	        // segmento normal prevCam -> cam
	        int costSeg = this.board.getCoste(prevCam - 1, cam - 1);
	        costes_dron.set(current_dron,
	                costes_dron.get(current_dron) + costSeg / dron_velocity[current_dron]);
	        prevCam = cam;
	    }

	    // cerrar último dron (volver a base)
	    if (prevCam != null) {
	        int costBack = board.getCosteBaseCam(prevCam);
	        costes_dron.set(current_dron,
	                costes_dron.get(current_dron) + costBack / dron_velocity[current_dron]);
	    }

	    double maxT = Collections.max(costes_dron);
	    double minT = Collections.min(costes_dron);
	    double penalizacionDesequilibrio = (maxT - minT) * 0.5;
	    double time = maxT + penalizacionDesequilibrio;

	    return 1.0 / (1.0 + time);
	}
	
}











