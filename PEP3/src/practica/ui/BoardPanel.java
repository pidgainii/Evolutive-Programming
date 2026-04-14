package practica.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import practica.real.Board;
import practica.real.Chromosome;
import practica.real.Pair;

// --- PANEL DEL MAPA PERSONALIZADO ---
public class BoardPanel extends JPanel {
	
	private final Color BG_DARK = new Color(18, 18, 18);
    private final Color PANEL_DARK = new Color(30, 30, 30);
    private final Color TEXT_LIGHT = new Color(220, 220, 220);
    private final Color ACCENT_CYAN = new Color(0, 255, 255);
	
    private Board board;
    private List<Pair> trail = new ArrayList<>(); // Huellas exploradas

    public BoardPanel(Board b) {
        this.board = b;
        setBackground(BG_DARK);
    }

    public void setBoard(Board b) { 
        this.board = b; 
        repaint(); 
    }

    public void updateTrail(List<Pair> newPath) {
        this.trail = newPath;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (board == null) return;

        Graphics2D g2 = (Graphics2D) g;
        int rows = board.alto();
        int cols = board.ancho();
        int cellSize = Math.min(getWidth() / cols, getHeight() / rows);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int x = j * cellSize;
                int y = i * cellSize;
                int val = board.getMap()[i][j];

                // Dibujar celda base (Suelo oscuro)
                g2.setColor(new Color(25, 25, 25));
                g2.fillRect(x, y, cellSize, cellSize);
                
                if (val == 1) { // MURO (Rojo con Aspas)
                    g2.setColor(new Color(120, 0, 0));
                    g2.fillRect(x, y, cellSize, cellSize);
                    g2.setColor(new Color(200, 0, 0));
                    g2.drawRect(x, y, cellSize, cellSize);
                    g2.drawLine(x, y, x + cellSize, y + cellSize);
                    g2.drawLine(x + cellSize, y, x, y + cellSize);
                } else if (val == 2) { // ARENA (Naranja/Marrón)
                    g2.setColor(new Color(180, 110, 40));
                    g2.fillRect(x + 2, y + 2, cellSize - 4, cellSize - 4);
                } else if (val == 3) { // MUESTRA (Punto Amarillo)
                    g2.setColor(Color.YELLOW);
                    int r = cellSize / 3;
                    g2.fillOval(x + cellSize/2 - r/2, y + cellSize/2 - r/2, r, r);
                }
                
                // Dibujar rejilla tenue
                g2.setColor(new Color(40, 40, 40));
                g2.drawRect(x, y, cellSize, cellSize);
            }
        }

        // DIBUJAR RASTRO (HUELLAS)
        g2.setColor(new Color(0, 200, 200, 150));
        for (Pair p : trail) {
            int tx = p.y() * cellSize + cellSize/2 - 2;
            int ty = p.x() * cellSize + cellSize/2 - 2;
            g2.fillOval(tx, ty, 4, 4);
        }

        // DIBUJAR ROVER (Triángulo Cian)
        Pair pos = board.getBase(); // O la posición actual del rover
        g2.setColor(ACCENT_CYAN);
        int rx = pos.y() * cellSize;
        int ry = pos.x() * cellSize;
        int[] px = {rx + 5, rx + cellSize - 5, rx + cellSize / 2};
        int[] py = {ry + cellSize - 5, ry + cellSize - 5, ry + 5};
        g2.fillPolygon(px, py, 3);
    }
}