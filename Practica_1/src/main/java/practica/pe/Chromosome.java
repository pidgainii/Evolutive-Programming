package practica.pe;

import java.util.Random;
import java.util.ArrayList;


public class Chromosome {

    private final StringBuilder genotype;
    private final ArrayList<Pair> fenotype;
    private final int xCoordinateSize;
    private final int yCoordinateSize;
    private final int chromosomeSize;
    private int cameraRange;

    public Chromosome(int boardSizeX, int boardSizeY, int nCameras) {
        // Creating a random chromosome
        this.xCoordinateSize = (int)(Math.log(boardSizeX) / Math.log(2)) + 1;
        this.yCoordinateSize = (int)(Math.log(boardSizeY) / Math.log(2)) + 1;

        this.chromosomeSize = (this.xCoordinateSize + this.yCoordinateSize) * nCameras;

        // Inicializando de forma random el genotipo (las posiciones de las camaras)
        Random random = new Random();

        this.genotype = new StringBuilder();
        for (int i = 0; i < chromosomeSize; i++) {
            this.genotype.append(random.nextBoolean() ? '1' : '0');
        }

        this.fenotype = new ArrayList<Pair>();
        for (int i = 0; i < this.chromosomeSize; i += (this.xCoordinateSize + this.yCoordinateSize))
        {
            String xCoord = this.genotype.substring(i, i+this.xCoordinateSize);
            String yCoord = this.genotype.substring(i+this.xCoordinateSize, i+this.xCoordinateSize+yCoordinateSize);

            int x = Integer.parseInt(xCoord, 2);
            int y = Integer.parseInt(yCoord, 2);

            this.fenotype.add(new Pair(x,y));
        }
    }

    public StringBuilder getGenotype() {
        return this.genotype;
    }

    public ArrayList<Pair> getFenotype() {
        return this.fenotype;
    }
}
