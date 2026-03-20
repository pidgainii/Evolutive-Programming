package practica.real;

import java.util.ArrayList;
import java.util.Arrays;

public class Fitness {

    private final Board board;
    private final int numDrones;

    public Fitness(Board board, int numDrones) {
        this.board = board;
        this.numDrones = Math.max(1, Math.min(5, numDrones));
    }

    public double evaluate(Chromosome individual) {
        return evaluateBreakdown(individual).fitness();
    }

    public FitnessBreakdown evaluateBreakdown(Chromosome individual) {

        ArrayList<Integer> genes = individual.getGenes();

        double[] times = new double[numDrones];

        int current_dron = 0;

        double[] dron_velocity_all = {1.5, 1.0, 0.7, 1.2, 0.5};
        double[] dron_velocity = Arrays.copyOf(dron_velocity_all, numDrones);

        int num_camaras = this.board.getNumCamaras();

        Integer prevCam = null;
        boolean started = false;

        for (int g : genes) {

            // separador => cerrar dron y pasar al siguiente
            if (g > num_camaras) {
                if (prevCam != null) {
                    int costBack = board.getCosteBaseCam(prevCam);
                    times[current_dron] += costBack / dron_velocity[current_dron];
                }

                current_dron++;
                if (current_dron >= numDrones) break;

                prevCam = null;
                started = false;
                continue;
            }

            // cámara
            int cam = g;

            if (!started) {
                int costOut = board.getCosteBaseCam(cam);
                times[current_dron] += costOut / dron_velocity[current_dron];
                started = true;
                prevCam = cam;
                continue;
            }

            int costSeg = this.board.getCoste(prevCam, cam);
            times[current_dron] += costSeg / dron_velocity[current_dron];
            prevCam = cam;
        }

        // cerrar último dron si aplica
        if (current_dron < numDrones && prevCam != null) {
            int costBack = board.getCosteBaseCam(prevCam);
            times[current_dron] += costBack / dron_velocity[current_dron];
        }

        double maxT = times[0], minT = times[0];
        for (int i = 1; i < times.length; i++) {
            if (times[i] > maxT) maxT = times[i];
            if (times[i] < minT) minT = times[i];
        }

        double penalty = (maxT - minT) * 0.5;
        double fitness = maxT + penalty;

        return new FitnessBreakdown(times, fitness);
    }
}