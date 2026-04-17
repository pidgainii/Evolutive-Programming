package practica.ast;

import java.util.ArrayList;
import java.util.Random;

import practica.enums.Accion;
import practica.enums.Direccion;
import practica.enums.Sensor;
import practica.real.Pair;

public class Contexto {

	// AGENTE
	private Pair coordenadas;
	private Direccion direccion;
	private int energia;
	private boolean vivo;
	// este array nos será útil para saber si el rover está "mareado"
	private ArrayList<Accion> ultimasAcciones;
	
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
	
	
	public Contexto(int seed, int ANCHO, int ALTO) {
		
		
		//////////////////////////// AGENTE ////////////////////////////
		this.coordenadas = new Pair(1,1);
		this.direccion = Direccion.ESTE;
		this.energia = 100;
		this.vivo = true;
		
		this.ultimasAcciones = new ArrayList<Accion>();
		
		
		
		
		//////////////////////////// ENTORNO ////////////////////////////
		this.ANCHO = ANCHO;
		this.ALTO = ALTO;
		
		
	 	map = new int[ANCHO][ALTO];
	 	visitado = new boolean[ANCHO][ALTO];
	 	
        Random rand = new Random(seed); 
        
        // 1  muro     2  arena   3  muestra
        for (int i = 0; i < ALTO; i++) {
            for (int j = 0; j < ANCHO; j++) {
            	map[i][j] = 0;
                if (i == 0 || i == ALTO - 1 || j == 0 || j == ANCHO - 1) map[i][j] = 1;
                else  if (rand.nextDouble() < 0.15 && (i != 1 || j != 1)) map[i][j] = 1;
                else  if (rand.nextDouble() < 0.15 && (i != 1 || j != 1)) map[i][j] = 3; 
                else  if (rand.nextDouble() < 0.08 && (i != 1 || j != 1)) map[i][j] = 2; 
                
                visitado[i][j] = false;
            }
        }
        
        
        
        ////////////////////////// ESTADISTICAS //////////////////////////
        this.ticks = 0;
        this.muestras = 0;
        this.colisiones = 0;
        this.exploracion = 0;
        this.recompensaVisual = 0;
        this.pisadasArena = 0;
        
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
		}
		
		this.ultimasAcciones.add(accion);
	}
	
	
	// TODO: Comprobar que esto se está haciendo bien
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
		if (casilla.x()<0 || casilla.x()>=this.map.length || casilla.y()<0 || casilla.y()>=this.map[0].length) {
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
}

















