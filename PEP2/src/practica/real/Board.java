package practica.real;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Board {
	
	
	private final int[][] map;
	private int seed;
	private final int NUM_CAMARAS;
	private final int N;
    private final int M;
    
    // Este array nos servira para poder "identificar" camaras
    // (el indice+1 en el que se encuentre una camara será su identificador
	private ArrayList<Pair> posicionesCamaras;
	
	// Este set nos servira para saber si hay camara en cierta posicion en tiempo cte.
	private Set<Pair> setPosicionesCamaras;
	
	public Board(int[][] map, int seed, int numCamaras) {
		this.map = map;
		this.seed = seed;
		this.NUM_CAMARAS = numCamaras;
		this.N = map.length;
		this.M = map[0].length;
		
		this.setPosicionesCamaras = new HashSet<Pair>();
		this.posicionesCamaras = new ArrayList<Pair>();
		
		Random rand = new Random(this.seed);
		
		while (posicionesCamaras.size() < this.NUM_CAMARAS) {
			int x = rand.nextInt(this.N);
			int y = rand.nextInt(this.M);
			
			Pair posicion = new Pair(x,y);
			
			// Comprobamos que la camara no exista ya,
			// y que dicha posicion no este ocupada por un muro
			if (!setPosicionesCamaras.contains(posicion) && map[x][y] != 0) {
				// Añadimos la camara al array
				posicionesCamaras.add(posicion);
				
				// Añadimos la posicion de la camara al set
				setPosicionesCamaras.add(posicion);	
			}	
		}
	}
	
	public int[][] getMap() {
		return this.map;
	}
	
	public int N() {
		return this.N;
	}
	
	public int M() {
		return this.M;
	}
	
	public ArrayList<Pair> getCamaras() {
		return this.posicionesCamaras;
	}
	
	public Board copy() {
	    int[][] newMap = new int[N][M];

	    // copiar mapa
	    for (int i = 0; i < N; i++) {
	        System.arraycopy(this.map[i], 0, newMap[i], 0, M);
	    }

	    Board copy = new Board(newMap, this.seed, this.NUM_CAMARAS);

	    // copiar cámaras
	    copy.posicionesCamaras = new ArrayList<>();
	    copy.setPosicionesCamaras = new HashSet<>();

	    for (Pair p : this.posicionesCamaras) {
	        Pair np = new Pair(p.x(), p.y());
	        copy.posicionesCamaras.add(np);
	        copy.setPosicionesCamaras.add(np);
	    }

	    return copy;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
