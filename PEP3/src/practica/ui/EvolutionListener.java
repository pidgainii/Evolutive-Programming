package practica.ui;

public interface EvolutionListener {
    void onGeneration(int generation,
                      double bestOfGeneration,
                      double globalBestSoFar,
                      double averageFitness,
                      Object bestChromosomeOfGeneration);
}