package practica.real;

import java.util.ArrayList;
import java.util.Arrays;

public class Fitness {

    private final Board board;
    private final int numVans;
    private final int numDeliveries;

    // La base siempre es el nodo 0
    private final int BASE_ID = 0;

    public Fitness(Board board, int numVans) {
        this.board = board;
        this.numVans = Math.max(1, Math.min(10, numVans));
        this.numDeliveries = board.getDeliveryPoints().size();
    }

    public double evaluate(Chromosome individual) {
        return evaluateBreakdown(individual).fitness();
    }

    public FitnessBreakdown evaluateBreakdown(Chromosome individual) {

        ArrayList<Integer> genes = individual.getGenes();

        double[] times = new double[numVans];
        int current_van = 0;

        // Velocidades de las vans
        double[] van_velocity_all = {1.5, 1.0, 0.7, 1.2, 0.5, 1.1, 0.9, 1.3, 0.8, 1.4};
        double[] van_velocity = Arrays.copyOf(van_velocity_all, numVans);

        DeliveryPoint prevDp = null;
        boolean started = false;

        // Nuevas métricas
        int lateDeliveries = 0;
        boolean[] delivered = new boolean[numDeliveries];

        for (int g : genes) {

            // Separador -> cambiar de van
            if (g > numDeliveries) {

                if (prevDp != null) {
                    double costBack = board.getCost(prevDp.getId(), BASE_ID);
                    times[current_van] += costBack / van_velocity[current_van];
                }

                current_van++;

                if (current_van >= numVans) {
                    break;
                }

                prevDp = null;
                started = false;
                continue;
            }

            // Punto de entrega
            int dpIndex = g - 1;
            DeliveryPoint dp = board.getDeliveryPoints().get(dpIndex);

            // Primera entrega de la van
            if (!started) {

                double costOut = board.getCost(BASE_ID, dp.getId());

                times[current_van] += costOut / van_velocity[current_van];

                // Comprobar retraso
                if (times[current_van] > dp.getWaitingTime()) {
                    lateDeliveries++;
                }

                delivered[dpIndex] = true;

                started = true;
                prevDp = dp;

                continue;
            }

            // Entregas intermedias
            double costSeg = board.getCost(prevDp.getId(), dp.getId());

            times[current_van] += costSeg / van_velocity[current_van];

            // Comprobar retraso
            if (times[current_van] > dp.getWaitingTime()) {
                lateDeliveries++;
            }

            delivered[dpIndex] = true;

            prevDp = dp;
        }

        // Volver a base
        if (current_van < numVans && prevDp != null) {

            double costBack = board.getCost(prevDp.getId(), BASE_ID);

            times[current_van] += costBack / van_velocity[current_van];
        }

        // Contar paquetes no entregados
        int undeliveredPackages = 0;

        for (boolean d : delivered) {
            if (!d) {
                undeliveredPackages++;
            }
        }

        // Buscar max y min tiempo
        double maxT = times[0];
        double minT = times[0];

        for (int i = 1; i < times.length; i++) {

            if (times[i] > maxT) {
                maxT = times[i];
            }

            if (times[i] < minT) {
                minT = times[i];
            }
        }

        // Penalización por desequilibrio
        double balancePenalty = (maxT - minT) * 0.5;

        // Penalizaciones fuertes
        double latePenalty = lateDeliveries * 50;
        double undeliveredPenalty = undeliveredPackages * 200;

        double fitness =
                maxT +
                balancePenalty +
                latePenalty +
                undeliveredPenalty;

        return new FitnessBreakdown(
                times,
                fitness,
                lateDeliveries,
                undeliveredPackages
        );
    }
}