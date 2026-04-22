package practica.real;

import java.util.ArrayList;
import java.util.Random;

import practica.enums.Accion;
import practica.enums.Direccion;
import practica.enums.Sensor;

public class Contexto {

	// AGENTE
	private Pair coordenadas;
	private Direccion direccion;
	private int energia;
	private boolean vivo;
	// este array nos será útil para saber si el rover está "mareado"
	private ArrayList<Accion> ultimasAcciones;
	
	// este array nos será util para poder ejecutar un nodo bloque
	// si una accion ya se ha ejecutado, el nodo bloque añadira las acciones
	// que le queden por "ejecutar" a este array.
	// Asi, el contexto verá que hay acciones pendientes por ejecutar y las ejecutará en los siguientes ticks.
	private ArrayList<Accion> accionesPendientes;
	
	// ENTORNO
	private int map[][];
	private final int ANCHO;
	private final int ALTO;
	private boolean visitado[][];
	
	// ESTADISTICAS
	private int ticks;
	private int muestras;
	private int colisiones;
	private int exploracion;
	private int recompensaVisual;
	private int pisadasArena;
	
	// Accion tomada
	private boolean accionTomada;
	
	
	public Contexto(int seed, int ANCHO, int ALTO) {
		
		
		//////////////////////////// AGENTE ////////////////////////////
		this.coordenadas = new Pair(1,1);
		this.direccion = Direccion.ESTE;
		this.energia = 100;
		this.vivo = true;
		
		this.ultimasAcciones = new ArrayList<Accion>();
		this.accionesPendientes = new ArrayList<Accion>();
		
		
		
		//////////////////////////// ENTORNO ////////////////////////////
		this.ANCHO = ANCHO;
	    this.ALTO = ALTO;
	    
	    // map[width][height] -> map[x][y]
	    map = new int[ANCHO][ALTO];
	    visitado = new boolean[ANCHO][ALTO];
	    
	    Random rand = new Random(seed); 
	    
	    for (int x = 0; x < ANCHO; x++) {
	        for (int y = 0; y < ALTO; y++) {
	            map[y][x] = 0;
	            if (x == 0 || x == ANCHO - 1 || y == 0 || y == ALTO - 1) map[y][x] = 1;
	            else if (rand.nextDouble() < 0.15 && (x != 1 || y != 1)) map[y][x] = 1;
	            else if (rand.nextDouble() < 0.15 && (x != 1 || y != 1)) map[y][x] = 3; 
	            else if (rand.nextDouble() < 0.08 && (x != 1 || y != 1)) map[y][x] = 2; 
	            
	            visitado[y][x] = false;
	        }
	    }
        
	    this.visitado[1][1] = true;
        
        
        ////////////////////////// ESTADISTICAS //////////////////////////
        this.ticks = 0;
        this.muestras = 0;
        this.colisiones = 0;
        this.exploracion = 0;
        this.recompensaVisual = 0;
        this.pisadasArena = 0;
        
        this.accionTomada = false;
        
	}
	
	public int leerSensor(Sensor sensor) {

	    if (sensor == Sensor.NIVEL_ENERGIA) {
	        return this.energia;
	    }

	    Pair actual = this.coordenadas;
	    int distancia = 0;

	    while (true) {
	        actual = casillaContiguaDir(actual, this.direccion);
	        distancia++;

	        // fuera de mapa → como muro
	        if (!casillaValida(actual)) {
	            return 100;
	        }

	        int celda = this.map[actual.x()][actual.y()];

	        // muro
	        if (celda == 1) {
	            if (sensor == Sensor.DIST_OBSTACULO) {
	                return distancia;
	            } else {
	                return 100;
	            }
	        }

	        // muestra
	        if (celda == 3 && sensor == Sensor.DIST_MUESTRA) {
	            return distancia;
	        }

	        // arena
	        if (celda == 2 && sensor == Sensor.DIST_ARENA) {
	            return distancia;
	        }
	    }
	}

	private Pair casillaContiguaDir(Pair origen, Direccion dir) {
	    if (dir == Direccion.NORTE) {
	        return new Pair(origen.x(), origen.y() - 1);
	    } else if (dir == Direccion.SUR) {
	        return new Pair(origen.x(), origen.y() + 1);
	    } else if (dir == Direccion.ESTE) {
	        return new Pair(origen.x() + 1, origen.y());
	    } else {
	        return new Pair(origen.x() - 1, origen.y());
	    }
	}

	public void ejecutarAccion(Accion accion) {
		if (!accionesPendientes.isEmpty()) {
			// FIFO
			accion = accionesPendientes.get(0);
			accionesPendientes.remove(0);
		}
		
		
		if (accion == Accion.AVANZAR) {
			Pair casillaContigua = this.casillaContigua(this.direccion);
			if (this.casillaValida(casillaContigua)) {
				
				// MURO
				if (this.map[casillaContigua.x()][casillaContigua.y()] == 1) {
					this.colisiones += 1;
					this.energia -= 2;
				}
				// ARENA
				else if (this.map[casillaContigua.x()][casillaContigua.y()] == 2) {
					this.coordenadas = casillaContigua;
					this.pisadasArena += 1;
					this.energia -= 10;
				}
				// MUESTRA
				else if (this.map[casillaContigua.x()][casillaContigua.y()] == 3) {
					this.map[casillaContigua.x()][casillaContigua.y()] = 0;
					this.coordenadas = casillaContigua;
					this.muestras += 1;
					this.energia -= 1;
				}
				// SUELO NORMAL
				else {
					/*
					 * Zona libre. Moverse cuesta –1 de energía. Pisarla por
					 * primera vez suma exploración.
					 */
					this.coordenadas = casillaContigua;
					this.energia -= 1;
				}
				
				// Añadiendo a exploración
				if (!this.visitado[this.coordenadas.x()][this.coordenadas.y()]) {
					this.visitado[this.coordenadas.x()][this.coordenadas.y()] = true;
					this.exploracion += 1;
				}
				
			}
		}
		// accion es GIRAR_IZQ o GIRAR_DER
		else {
			if (this.mareado(accion)){
				this.energia -= 20;
			}
			this.girarAgente(accion);
			this.energia -= 1;
		}
		
		this.ultimasAcciones.add(accion);
		
		this.ticks++;
		
		if (this.energia<=0) this.vivo = false;
	}
	
	private Pair casillaContigua(Direccion direccion) {
		if (direccion == Direccion.NORTE) {
			return new Pair(this.coordenadas.x(), this.coordenadas.y()-1);
		}
		else if (direccion == Direccion.SUR) {
			return new Pair(this.coordenadas.x(), this.coordenadas.y()+1);
		}
		else if (direccion == Direccion.ESTE) {
			return new Pair(this.coordenadas.x()+1, this.coordenadas.y());
		}
		else {
			return new Pair(this.coordenadas.x()-1, this.coordenadas.y());
		}
	}
	
	private boolean casillaValida(Pair casilla) {
	    // x debe compararse con el primer índice (Ancho)
	    // y debe compararse con el segundo índice (Alto)
	    if (casilla.x() < 0 || casilla.x() >= this.ANCHO || 
	        casilla.y() < 0 || casilla.y() >= this.ALTO) {
	        return false;
	    }
	    return true;
	}
	
	private boolean mareado(Accion accion) {
	    if (this.ultimasAcciones.size() < 3) {
	        return false; // not enough elements
	    }

	    for (int i = this.ultimasAcciones.size() - 1; i >= this.ultimasAcciones.size() - 3; i--) {
	        if (this.ultimasAcciones.get(i) != accion) {
	            return false; // one doesn't match → fail immediately
	        }
	    }

	    return true; // all last 3 match
	}
	
	private void girarAgente(Accion accion) {
	    if (accion == Accion.GIRAR_IZQ) {
	        switch (this.direccion) {
	            case NORTE -> this.direccion = Direccion.OESTE;
	            case OESTE -> this.direccion = Direccion.SUR;
	            case SUR   -> this.direccion = Direccion.ESTE;
	            case ESTE  -> this.direccion = Direccion.NORTE;
	        }
	    } else if (accion == Accion.GIRAR_DER) {
	        switch (this.direccion) {
	            case NORTE -> this.direccion = Direccion.ESTE;
	            case ESTE  -> this.direccion = Direccion.SUR;
	            case SUR   -> this.direccion = Direccion.OESTE;
	            case OESTE -> this.direccion = Direccion.NORTE;
	        }
	    }
	}
	
    public Contexto copy() {
        Contexto copia = new Contexto(0, this.ANCHO, this.ALTO); // La semilla da igual aquí
        
        // Copiar AGENTE
        copia.coordenadas = new Pair(this.coordenadas.x(), this.coordenadas.y());
        copia.direccion = this.direccion;
        copia.energia = this.energia;
        copia.vivo = this.vivo;
        copia.ultimasAcciones = new ArrayList<>(this.ultimasAcciones);
        
        // Copiar ENTORNO (Deep copy de los arrays)
        for (int i = 0; i < ANCHO; i++) {
            copia.map[i] = this.map[i].clone();
            copia.visitado[i] = this.visitado[i].clone();
        }
        
        // Copiar ESTADISTICAS
        copia.ticks = this.ticks;
        copia.muestras = this.muestras;
        copia.colisiones = this.colisiones;
        copia.exploracion = this.exploracion;
        copia.recompensaVisual = this.recompensaVisual;
        copia.pisadasArena = this.pisadasArena;
        copia.accionTomada = this.accionTomada;
        
        return copia;
    }

    public void reset(int energiaInicial) {
        // Reset Agente
        this.coordenadas = new Pair(1, 1);
        this.direccion = Direccion.ESTE;
        this.energia = energiaInicial;
        this.vivo = true;
        this.ultimasAcciones.clear();
        
        // Reset Estadísticas
        this.ticks = 0;
        this.muestras = 0;
        this.colisiones = 0;
        this.exploracion = 1; // La casilla (1,1) ya cuenta como explorada al empezar
        this.recompensaVisual = 0;
        this.pisadasArena = 0;
        this.accionTomada = false;
        
        // Limpiar matriz de visitados
        for (int i = 0; i < ANCHO; i++) {
            for (int j = 0; j < ALTO; j++) {
                this.visitado[i][j] = (i == 1 && j == 1);
            }
        }
        
        this.visitado[1][1] = true;
    }



	// public getters
	public int getMuestras() {
		return this.muestras;
	}
	
	public int getExploradas() {
		return this.exploracion;
	}
	
	public int getRecompensaVisual() {
		return this.recompensaVisual;
	}
	
	public int getPisadasArena() {
		return this.pisadasArena;
	}
	
	public int getColisiones() {
		return this.colisiones;
	}
	
	public int getAlto() {
		return this.ALTO;
	}
	
	public int getAncho() {
		return this.ANCHO;
	}
	
	public int[][] getMap() {
		return this.map;
	}
	
	public Pair getCoordenadas() {
		return this.coordenadas;
	}
	
	public int getTicks() {
		return this.ticks;
	}
	
	public Direccion getDireccion() {
		return this.direccion;
	}
	
	
	public boolean getAccionTomada() {
		return this.accionTomada;
	}
	
	public void setAccionTomada(boolean accionTomada) {
		this.accionTomada = accionTomada;
	}
	
	public boolean estaVivo() {
		return this.vivo;
	}
	
	public void addAccion(Accion accion) {
		this.accionesPendientes.add(accion);
	}
}

















