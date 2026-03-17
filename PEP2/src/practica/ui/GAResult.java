package practica.ui;

public class GAResult {
    private final Object best;
    private final double[] bestOfGeneration;
    private final double[] globalBestSoFar;
    private final double[] averageFitness;

    public GAResult(Object best, double[] bestOfGeneration, double[] globalBestSoFar, double[] averageFitness) {
        this.best = best;
        this.bestOfGeneration = bestOfGeneration;
        this.globalBestSoFar = globalBestSoFar;
        this.averageFitness = averageFitness;
    }

    public Object getBest() { return best; }
    public double[] getBestOfGeneration() { return bestOfGeneration; }
    public double[] getGlobalBestSoFar() { return globalBestSoFar; }
    public double[] getAverageFitness() { return averageFitness; }
}