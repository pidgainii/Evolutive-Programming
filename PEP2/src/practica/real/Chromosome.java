package practica.real;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Chromosome {
	
	
	// Usamos arrayList y no int[] porque podemos hacer shuffle facilmente
	// Además, los cruces y mutaciones van a ser más faciles con ArrayList
	// A no ser que sea muy lento. Si es muy lento, lo cambiamos a int[][]
	private ArrayList<Integer> genes;
    private double fitness;
    private double acum_fitness;
    private double relative_fitness;
    
    
    public Chromosome(ArrayList<Integer> genes) {
    	this.genes = genes;
    }
    
    public Chromosome(Chromosome chromosome) {
        this.genes = new ArrayList<Integer>(chromosome.getGenes());
        this.fitness = chromosome.getFitness();
        this.acum_fitness = chromosome.getAcum_fitness();
        this.relative_fitness = chromosome.getRelative_fitness();
    }
    
    @Override
    public Chromosome clone() {
        return new Chromosome(this);
    }

    public void setGenes(ArrayList<Integer> genes) {
    	this.genes = genes;
    }
    
    public ArrayList<Integer> getGenes() {
        return genes;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getAcum_fitness() {
        return acum_fitness;
    }

    public void setAcum_fitness(double acum_fitness) {
        this.acum_fitness = acum_fitness;
    }

    public double getRelative_fitness() {
        return relative_fitness;
    }

    public void setRelative_fitness(double relative_fitness) {
        this.relative_fitness = relative_fitness;
    }

    
    /*
    public Chromosome[] cross(Chromosome other, Random rand) {
        double[] g1 = this.getGenes();
        double[] g2 = other.getGenes();

        double[] c1 = Arrays.copyOf(g1, g1.length);
        double[] c2 = Arrays.copyOf(g2, g2.length);

        int crossPoint = rand.nextInt(c1.length);
        for (int i = crossPoint; i < g1.length; i++) {
            c1[i] = g2[i];
            c2[i] = g1[i];
        }

        return new Chromosome[] {new Chromosome(c1), new Chromosome(c2)};
    }

    public Chromosome[] crossUniform(Chromosome other, Random rand) {
        double[] a = this.genes;
        double[] b = other.genes;

        double[] c1 = new double[a.length];
        double[] c2 = new double[a.length];

        for (int i = 0; i < a.length; i++) {
            if (rand.nextBoolean()) {
                c1[i] = a[i]; c2[i] = b[i];
            } else {
                c1[i] = b[i]; c2[i] = a[i];
            }
        }
        return new Chromosome[]{ new Chromosome(c1), new Chromosome(c2) };
    }

    public Chromosome[] crossArithmetic(Chromosome other, Random rand) {
        double alpha = rand.nextDouble(); // [0,1]
        double[] a = this.genes;
        double[] b = other.genes;

        double[] c1 = new double[a.length];
        double[] c2 = new double[a.length];

        for (int i = 0; i < a.length; i++) {
            c1[i] = alpha * a[i] + (1.0 - alpha) * b[i];
            c2[i] = alpha * b[i] + (1.0 - alpha) * a[i];
        }
        return new Chromosome[]{ new Chromosome(c1), new Chromosome(c2) };
    }

    public Chromosome[] crossBlxAlpha(Chromosome other, Random rand, double alpha) {
        double[] a = this.genes;
        double[] b = other.genes;

        double[] c1 = new double[a.length];
        double[] c2 = new double[a.length];

        for (int i = 0; i < a.length; i++) {
            double min = Math.min(a[i], b[i]);
            double max = Math.max(a[i], b[i]);
            double range = max - min;

            double low = min - alpha * range;
            double high = max + alpha * range;

            c1[i] = low + rand.nextDouble() * (high - low);
            c2[i] = low + rand.nextDouble() * (high - low);
        }
        return new Chromosome[]{ new Chromosome(c1), new Chromosome(c2) };
    }

    public void mutateGaussian(Random rand, double mutationProbability, int num_cameras, int N, int M, double sigmaX, double sigmaY, double sigmaTheta) {
        double[] g = this.genes;

        for (int i = 0; i < num_cameras; i++) {
            int base = i * 3;

            if (rand.nextDouble() < mutationProbability) {
                g[base] += rand.nextGaussian() * sigmaX;
                g[base] = clamp(g[base], 0.0, Math.nextDown((double) N)); // [0, N)
            }

            if (rand.nextDouble() < mutationProbability) {
                g[base + 1] += rand.nextGaussian() * sigmaY;
                g[base + 1] = clamp(g[base + 1], 0.0, Math.nextDown((double) M)); // [0, M)
            }

            if (rand.nextDouble() < mutationProbability) {
                g[base + 2] += rand.nextGaussian() * sigmaTheta;
                g[base + 2] = wrapAngle360(g[base + 2]);
            }
        }
    }

    public void mutateGeneLevel(Random rand, double pm, int numCameras, int N, int M) {
        if (rand.nextDouble() >= pm) return;

        // Elegimos un gen cualquiera del cromosoma
        int totalGenes = 3 * numCameras;
        int idx = rand.nextInt(totalGenes);

        int mod = idx % 3; // 0=x, 1=y, 2=theta
        if (mod == 0) {
            genes[idx] = rand.nextDouble() * N;
        } else if (mod == 1) {
            genes[idx] = rand.nextDouble() * M;
        } else {
            genes[idx] = rand.nextDouble() * 360.0;
        }
    }

    // utilidades

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private static double wrapAngle360(double deg) {
        deg = deg % 360.0;
        if (deg < 0) deg += 360.0;
        return deg;
    }
    */

}