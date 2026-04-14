package practica.real;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import practica.AStar;
import practica.Maps;

public class Board {
	
	
	private final int[][] map;
	private int seed;
	private final int ANCHO;
    private final int ALTO;


	
	
	private Pair base;


	
	public Board(int seed, int ANCHO, int ALTO) {
		this.seed = seed;
		this.ANCHO = ANCHO;
		this.ALTO = ALTO;
		
		
		this.base = new Pair(1,1);
		
		
		
	 	map = new int[ANCHO][ALTO];
        Random rand = new Random(seed); 
        
        // 1  muro     2  arena   3  muestra
        for (int i = 0; i < ALTO; i++) {
            for (int j = 0; j < ANCHO; j++) {
                if (i == 0 || i == ALTO - 1 || j == 0 || j == ANCHO - 1) map[i][j] = 1;
                else  if (rand.nextDouble() < 0.15 && (i != 1 || j != 1)) map[i][j] = 1;
                else  if (rand.nextDouble() < 0.15 && (i != 1 || j != 1)) map[i][j] = 3; 
                else  if (rand.nextDouble() < 0.08 && (i != 1 || j != 1)) map[i][j] = 2; 
            }
        }
        
        /* ????
        visitado = new boolean[ALTO][ANCHO];
        visitado[y][x] = true;
		*/
		

	}

	
	public int[][] getMap() {
		return Maps.copy(this.map);
	}
	
	public int ancho() {
		return this.ANCHO;
	}
	
	public int alto() {
		return this.ALTO;
	}
	
	public int getCellCost(Pair coord) {
		return this.map[coord.x()][coord.y()];
	}
	
	
	/*
	public Board copy() {
	    int[][] newMap = new int[ANCHO][ALTO];

	    // copiar mapa
	    for (int i = 0; i < ANCHO; i++) {
	        System.arraycopy(this.map[i], 0, newMap[i], 0, ALTO);
	    }

	    Board copy = new Board(newMap, this.seed);

	    return copy;
	}
	*/
	
	
	
	public Pair getBase() {
		return this.base;
	}
}
