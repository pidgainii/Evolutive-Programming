package practica.pe;

import java.util.ArrayList;


public class Chromosome {

    private String genotype;
    private int xCoordinateSize;
    private int yCoordinateSize;

    public Chromosome(int boardSizeX, int boardSizeY, int nCameras) {
        // Creating a random chromosome
        int nBitsX = (int)(Math.log(boardSizeX) / Math.log(2)) + 1;
        int nBitsY = (int)(Math.log(boardSizeY) / Math.log(2)) + 1;

        int genotypeSize = (nBitsX + nBitsY) * nCameras;

        // Inicializando de forma random el genotipo (las posiciones de las camaras)
        this.genotype = java.util.stream.IntStream.range(0, genotypeSize)
                .mapToObj(i -> String.valueOf(new java.util.Random().nextInt(2)))
                .collect(java.util.stream.Collectors.joining());
    }

    public String getGenotype() {
        return this.genotype;
    }
}
