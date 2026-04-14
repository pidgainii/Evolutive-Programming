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
	
	private Pair base;
	private int[] costesBaseCam;
	private int[] costesCamBase;
	private ArrayList<Pair>[] routesBaseCam;
	private ArrayList<Pair>[] routesCamBase;
	
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
		
		
		this.routes = new ArrayList[NUM_CAMARAS][NUM_CAMARAS];

		for (int i = 0; i < NUM_CAMARAS; i++) {
		    for (int j = i; j < NUM_CAMARAS; j++) {

		        if (i == j) {
		        	this.routes[i][j] = new ArrayList<>();
		        	continue;
		        }

		        Pair c1 = posicionesCamaras.get(i);
		        Pair c2 = posicionesCamaras.get(j);

		        /*
		         * Esto nos devuelve la ruta completa, con la casilla de inicio y fin incluidas
		         * Necesitamos quitar la casilla de inicio, SOLO incluir la de fin
		         */
		        ArrayList<Pair> path = AStar.a_star(this.map, c1, c2, setPosicionesCamaras);
		        
		        this.routes[i][j] = new ArrayList<Pair>(path);
		        // Le quitamos la primera
		        this.routes[i][j].remove(0);
		        
		        
		        // Aqui la ruta no es la misma, es la contraria (simétrica)
		        ArrayList<Pair> reversed = new ArrayList<>(path);
		        java.util.Collections.reverse(reversed);

		        this.routes[j][i] = reversed;
		        // Le quitamos la primera
		        this.routes[j][i].remove(0);
		        
		        int coste1 = this.computeCost(routes[i][j]);
		        costes[i][j] = coste1;
		        costes[j][i] = coste1;
		    }
		}
		
		this.routesBaseCam = new ArrayList[NUM_CAMARAS];
		this.costesBaseCam = new int[NUM_CAMARAS];
		this.routesCamBase= new ArrayList[NUM_CAMARAS];
		this.costesCamBase = new int[NUM_CAMARAS];
		
		for (int i = 0; i < NUM_CAMARAS; i++) {
		    Pair cam = posicionesCamaras.get(i);

		    ArrayList<Pair> path = AStar.a_star(this.map, this.base, cam, setPosicionesCamaras);

		    this.routesBaseCam[i] = new ArrayList<>(path);
		    this.routesBaseCam[i].remove(0); // remove base
		    
            costesBaseCam[i] = computeCost(routesBaseCam[i]);
            
	        ArrayList<Pair> reversed = new ArrayList<>(path);
	        java.util.Collections.reverse(reversed);
	        
		    this.routesCamBase[i] = new ArrayList<>(reversed);
		    this.routesCamBase[i].remove(0); // remove first cam
		    
            costesCamBase[i] = costesBaseCam[i];

		}



	}
	
	private int computeCost(ArrayList<Pair> path) {
	    int cost = 0;
	    for (int i = 0; i < path.size() - 1; i++) {   // <- NO incluir destino
	        Pair p = path.get(i);
	        cost += this.getCellCost(p);
	    }
	    return cost;
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
		return costes[i-1][j-1];
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
	    return this.costesBaseCam[cam - 1];
	}
	
	public ArrayList<Pair> getRouteBaseCam(int camId1Based) {
	    return routesBaseCam[camId1Based - 1];
	}
	
	public int getCosteCamBase(int cam) {
	    return this.costesCamBase[cam - 1];
	}
	
	public ArrayList<Pair> getRouteCamBase(int camId1Based) {
	    return routesCamBase[camId1Based - 1];
	}
}
