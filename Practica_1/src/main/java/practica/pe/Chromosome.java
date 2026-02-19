package practica.pe;

import java.util.Random;
import java.util.ArrayList;


public class Chromosome {

    private StringBuilder genotype;
    private ArrayList<Pair> fenotype;
    private final int chromosomeSize;
    private final int nCameras;
    private final int boardSizeX;
    private final int boardSizeY;


    public Chromosome(int boardSizeX, int boardSizeY, int nCameras) {
        this.boardSizeX = boardSizeX;
        this.boardSizeY = boardSizeY;
        this.nCameras = nCameras;
        // Creating a random chromosome
        int xCoordinateSize = (int)(Math.log(boardSizeX) / Math.log(2)) + 1;
        int yCoordinateSize = (int)(Math.log(boardSizeY) / Math.log(2)) + 1;

        this.chromosomeSize = (xCoordinateSize + yCoordinateSize) * this.nCameras;

        // Inicializando de forma random el genotipo (las posiciones de las camaras)
        Random random = new Random();

        this.genotype = new StringBuilder();
        for (int i = 0; i < chromosomeSize; i++) {
            this.genotype.append(random.nextBoolean() ? '1' : '0');
        }

        this.fenotype = new ArrayList<Pair>();
        for (int i = 0; i < this.chromosomeSize; i += (xCoordinateSize + yCoordinateSize))
        {
            String xCoord = this.genotype.substring(i, i+xCoordinateSize);
            String yCoord = this.genotype.substring(i+xCoordinateSize, i+xCoordinateSize+yCoordinateSize);

            int x = Integer.parseInt(xCoord, 2);
            int y = Integer.parseInt(yCoord, 2);

            if (x >= boardSizeX) x %= boardSizeX;
            if (y >= boardSizeY) y %= boardSizeY;

            this.fenotype.add(new Pair(x,y));
        }
    }

    public Chromosome(int boardSizeX, int boardSizeY, int nCameras, StringBuilder genotype) {
        this.boardSizeX = boardSizeX;
        this.boardSizeY = boardSizeY;
        this.nCameras = nCameras;

        int xCoordinateSize = (int)(Math.log(boardSizeX) / Math.log(2)) + 1;
        int yCoordinateSize = (int)(Math.log(boardSizeY) / Math.log(2)) + 1;

        this.chromosomeSize = (xCoordinateSize + yCoordinateSize) * this.nCameras;
        this.genotype = genotype;

        this.fenotype = new ArrayList<Pair>();
        for (int i = 0; i < this.chromosomeSize; i += (xCoordinateSize + yCoordinateSize))
        {
            String xCoord = this.genotype.substring(i, i+xCoordinateSize);
            String yCoord = this.genotype.substring(i+xCoordinateSize, i+xCoordinateSize+yCoordinateSize);

            int x = Integer.parseInt(xCoord, 2);
            int y = Integer.parseInt(yCoord, 2);

            if (x >= boardSizeX) x %= boardSizeX;
            if (y >= boardSizeY) y %= boardSizeY;

            this.fenotype.add(new Pair(x,y));
        }
    }

    public StringBuilder getGenotype() {
        return this.genotype;
    }

    public ArrayList<Pair> getFenotype() {
        return this.fenotype;
    }

    public int getnCameras() {
        return this.nCameras;
    }

    public int getBoardSizeX() {
        return this.boardSizeX;
    }

    public int getBoardSizeY() {
        return this.boardSizeY;
    }

    public int getChromosomeSize() {
        return this.chromosomeSize;
    }

    public void setGenotype(StringBuilder genotype) {
        // We will redo fenotype too
        this.genotype = genotype;

        this.fenotype = new ArrayList<Pair>();
        int xCoordinateSize = (int)(Math.log(boardSizeX) / Math.log(2)) + 1;
        int yCoordinateSize = (int)(Math.log(boardSizeY) / Math.log(2)) + 1;

        for (int i = 0; i < this.chromosomeSize; i += (xCoordinateSize + yCoordinateSize))
        {
            String xCoord = this.genotype.substring(i, i+xCoordinateSize);
            String yCoord = this.genotype.substring(i+xCoordinateSize, i+xCoordinateSize+yCoordinateSize);

            int x = Integer.parseInt(xCoord, 2);
            int y = Integer.parseInt(yCoord, 2);

            if (x >= boardSizeX) x %= boardSizeX;
            if (y >= boardSizeY) y %= boardSizeY;

            this.fenotype.add(new Pair(x,y));
        }
    }
}
