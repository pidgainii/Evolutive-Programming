package practica.pe.ui;

public interface EvolutionListener {
    void onGeneration(int generation,
                      int bestOfGeneration,
                      int globalBestSoFar,
                      double averageFitness,
                      Object bestChromosomeOfGeneration);
}