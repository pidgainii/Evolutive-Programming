package practica.pe;

public class Evolution {


    /*
    inicializa(); //crea población inicial de cromosomas
    evaluarPoblacion(); //evalúa los individuos y coge el mejor
    while (!terminado()) {
        numgeneracion++;
        seleccion();
        cruce();
        mutacion();
        evaluarPoblacion();
        . . .
    }
    devolver mejor;
     */


    // tablero del escenario 1
    int[][] tablero1 = new int[][]{
        {1, 1, 1, 1, 1, 0, 1, 1, 1, 1},
        {1, 0, 5, 5, 5, 1, 1, 1, 0, 1}, // Pasillo Norte
        {1, 1, 1, 0, 1, 1, 0, 1, 1, 1},
        {1, 1, 1, 1, 5, 5, 5, 1, 1, 1}, // Pasillo Centro
        {0, 1, 1, 1, 0, 10, 1, 1, 1, 1}, // <--- JOYA (10)
        {1, 1, 0, 1, 1, 5, 1, 0, 1, 1}, // Conexión
        {1, 1, 1, 1, 1, 5, 1, 1, 1, 1}, // Conexión
        {1, 1, 0, 1, 1, 1, 1, 1, 1, 1},
        {1, 1, 5, 5, 5, 0, 5, 5, 5, 1}, // Pasillo Sur
        {1, 1, 1, 1, 1, 1, 1, 0, 1, 1}
    };

}
