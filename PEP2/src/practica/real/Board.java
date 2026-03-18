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
	private final int NUM_CAMARAS;
	private final int N;
    private final int M;
    
    // Este array nos servira para poder "identificar" camaras
    // (el indice+1 en el que se encuentre una camara será su identificador
	private ArrayList<Pair> posicionesCamaras;
	
	// Este set nos servira para saber si hay camara en cierta posicion en tiempo cte.
	private Set<Pair> setPosicionesCamaras;
	
	// Rutas precalculadas para mejor rendimiento
	private ArrayList<Pair>[][] routes;
	
	// Calcular coste directamente
	private int[][] costes;
	
	private final Pair base;
	private int[] costesBaseCam;
	
	public Board(int[][] map, int seed, int numCamaras) {
		this.map = map;
		this.seed = seed;
		this.NUM_CAMARAS = numCamaras;
		this.N = map.length;
		this.M = map[0].length;
		this.costes = new int[numCamaras][numCamaras];
		
		this.setPosicionesCamaras = new HashSet<Pair>();
		this.posicionesCamaras = new ArrayList<Pair>();
		
		this.base = this.calcularBase();
		
		Random rand = new Random(this.seed);
		
		while (posicionesCamaras.size() < this.NUM_CAMARAS) {
			int y = rand.nextInt(this.M);
			int x = rand.nextInt(this.N);
			
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
		
		
		routes = new ArrayList[NUM_CAMARAS][NUM_CAMARAS];

		for (int i = 0; i < NUM_CAMARAS; i++) {
		    for (int j = i; j < NUM_CAMARAS; j++) {

		        if (i == j) {
		        	routes[i][j] = new ArrayList<>();
		        	continue;
		        }

		        Pair c1 = posicionesCamaras.get(i);
		        Pair c2 = posicionesCamaras.get(j);

		        routes[i][j] = AStar.a_star(this.map, c1, c2, setPosicionesCamaras);
		        routes[j][i] = routes[i][j];
		        
		        int coste = this.costeDistancia(i, j);
		        costes[i][j] = coste;
		        costes[j][i] = coste;
		        
		    }
		}
		
        this.costesBaseCam = new int[NUM_CAMARAS];

        for (int i = 0; i < NUM_CAMARAS; i++) {
            Pair cam = posicionesCamaras.get(i);

            ArrayList<Pair> rutaBaseCam = AStar.a_star(this.map, this.base, cam, setPosicionesCamaras);

            int coste = 0;
    		for (Pair p: rutaBaseCam) {
    			coste += this.getCellCost(p);
    		}
            costesBaseCam[i] = coste;
        }
	}
	
	public int[][] getMap() {
		return Maps.copy(this.map);
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
	
	public int getNumCamaras() {
		return this.NUM_CAMARAS;
	}
	
	public Set<Pair> getCamarasSet() {
		return this.setPosicionesCamaras;
	}
	
	public int getCoste(int i, int j) {
		return costes[i][j];
	}
	
	public Pair getCamara(int index) {
		return this.posicionesCamaras.get(index-1);
	}
	
	public int getCellCost(Pair coord) {
		return this.map[coord.x()][coord.y()];
	}
	
	public ArrayList<Pair> getRoute(int cam1, int cam2) {
	    return routes[cam1][cam2];
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
	
	private int costeDistancia(int camara1, int camara2) {

		
		int coste = 0;
		
		// funcion que nos devuelve la ruta de una camara a otra (sin pasar por muros)
		ArrayList<Pair> ruta = this.getRoute(camara1, camara2);
		
		
		// Vamos sumando el coste de cada celda
		for (int i = 1; i < ruta.size(); i++) {
		    coste += this.getCellCost(ruta.get(i));
		}
		
		return coste;
	}
	
	public Pair getCamaraById(int camId1Based) {
	    if (camId1Based < 1 || camId1Based > NUM_CAMARAS) {
	        throw new IllegalArgumentException("Camera id out of range: " + camId1Based);
	    }
	    return posicionesCamaras.get(camId1Based - 1);
	}
	
	private Pair calcularBase() {
		
		int i = 0;
		int j = 0;
		
		while (i < this.N) {
			j = 0;
			while (j < this.M) {
				
				if (this.map[i][j] != 0) {
					return new Pair(i, j);
				}
				j++;
			}
			i++;
		}
		
		return new Pair(0, 0);
	}
	
	public Pair getBase() {
		return this.base;
	}
	
	public int getCosteBaseCam(int cam) {
	    return this.costesBaseCam[cam];
	}
}
