package practica.ui;

public class GAResult {
    private final Object best;
    private final int[] bestOfGeneration;
    private final int[] globalBestSoFar;
    private final double[] averageFitness;

    public GAResult(Object best, int[] bestOfGeneration, int[] globalBestSoFar, double[] averageFitness) {
        this.best = best;
        this.bestOfGeneration = bestOfGeneration;
        this.globalBestSoFar = globalBestSoFar;
        this.averageFitness = averageFitness;
    }

    public Object getBest() { return best; }
    public int[] getBestOfGeneration() { return bestOfGeneration; }
    public int[] getGlobalBestSoFar() { return globalBestSoFar; }
    public double[] getAverageFitness() { return averageFitness; }
}