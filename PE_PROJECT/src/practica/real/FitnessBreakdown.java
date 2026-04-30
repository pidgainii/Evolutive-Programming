package practica.real;

public record FitnessBreakdown(
        double[] times,
        double fitness,
        int lateDeliveries,
        int undeliveredPackages
) {}