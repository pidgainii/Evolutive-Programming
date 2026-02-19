package main.java.practica.pe;

import main.java.practica.pe.Population;

import java.util.*;

public class Evolution {

    int populationSize = 100;

    int[][] map = new int[][]{
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 1, 0, 0, 0, 0, 0, 0, 1, 0},
            {0, 0, 0, 1, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0}
    };

    int NUM_CAMARAS = 2;
    int RANGO_VISION = 3;
    int N = 10;
    int M = 10;

    int xBits = Integer.toBinaryString(N - 1).length();
    int yBits = Integer.toBinaryString(M - 1).length();
    int totalBits = NUM_CAMARAS * (xBits + yBits);


    Population population = new Population(populationSize, totalBits);
    Fitness fitness = new Fitness(map, NUM_CAMARAS, RANGO_VISION, N, M);


}
