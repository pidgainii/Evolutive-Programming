package practica.pe;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {

    private int[][] tablero;

    public BoardPanel(int[][] tablero) {
        this.tablero = tablero;
        int filas = tablero.length;
        int columnas = tablero[0].length;
        setLayout(new GridLayout(filas, columnas));
        setPreferredSize(new Dimension(columnas * 40, filas * 40)); // 40px por celda
        rebuildGrid();
    }

    private void rebuildGrid() {
        removeAll(); // limpiar si se va a actualizar
        for (int[] fila : tablero) {
            for (int celda : fila) {
                JPanel cell = new JPanel();
                switch (celda) {
                    case 0: cell.setBackground(Color.WHITE); break;     // vacío
                    case 1: cell.setBackground(Color.DARK_GRAY); break; // muro
                    case 2: cell.setBackground(Color.ORANGE); break;    // cámara
                    case 3: cell.setBackground(Color.CYAN); break;      // visto
                }
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                add(cell);
            }
        }
        revalidate();
        repaint();
    }

    // Para actualizar el tablero dinámicamente
    public void setTablero(int[][] tablero) {
        this.tablero = tablero;
        rebuildGrid();
    }
}
