package practica.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import practica.real.Contexto;
import practica.real.Pair;
import practica.enums.Direccion; // Asegúrate de importar el Enum

public class ContextPanel extends JPanel {
    
    private final Color BG_DARK = new Color(18, 18, 18);
    private final Color ACCENT_CYAN = new Color(0, 255, 255);
    
    private Contexto contexto;
    private List<Pair> trail = new ArrayList<>();

    public ContextPanel(Contexto c) {
        this.contexto = c;
        setBackground(BG_DARK);
    }

    public void setContext(Contexto c) { 
        this.contexto = c; 
        repaint(); 
    }

    public void updateTrail(List<Pair> newPath) {
        this.trail = newPath;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (contexto == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // USAR x para Ancho e y para Alto
        int ancho = contexto.getAncho();
        int alto = contexto.getAlto();
        int[][] map = contexto.getMap(); // Ahora es [ancho][alto]
        
        int cellSize = Math.min(getWidth() / ancho, getHeight() / alto);

        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {
                int drawX = x * cellSize;
                int drawY = y * cellSize;
                int val = map[x][y]; // Acceso correcto [x][y]

                // Dibujar celda base
                g2.setColor(new Color(25, 25, 25));
                g2.fillRect(drawX, drawY, cellSize, cellSize);
                
                if (val == 1) { // MURO
                    g2.setColor(new Color(120, 0, 0));
                    g2.fillRect(drawX, drawY, cellSize, cellSize);
                    g2.setColor(new Color(200, 0, 0));
                    g2.drawRect(drawX, drawY, cellSize, cellSize);
                } else if (val == 2) { // ARENA
                    g2.setColor(new Color(180, 110, 40));
                    g2.fillRect(drawX + 2, drawY + 2, cellSize - 4, cellSize - 4);
                } else if (val == 3) { // MUESTRA
                    g2.setColor(Color.YELLOW);
                    int r = cellSize / 3;
                    g2.fillOval(drawX + cellSize/2 - r/2, drawY + cellSize/2 - r/2, r, r);
                }
                
                g2.setColor(new Color(40, 40, 40));
                g2.drawRect(drawX, drawY, cellSize, cellSize);
            }
        }

        // DIBUJAR RASTRO
        g2.setColor(new Color(0, 255, 255, 100));
        for (Pair p : trail) {
            // p.x() es la columna, p.y() es la fila
            int tx = p.x() * cellSize + cellSize/2 - 2;
            int ty = p.y() * cellSize + cellSize/2 - 2;
            g2.fillOval(tx, ty, 4, 4);
        }

        // DIBUJAR ROVER ORIENTADO
        Pair pos = contexto.getCoordenadas(); 
        practica.enums.Direccion dir = contexto.getDireccion();

        if (pos != null && dir != null) {
            g2.setColor(ACCENT_CYAN);
            int rx = pos.x() * cellSize;
            int ry = pos.y() * cellSize;
            
            int p = 5; // padding
            int[] px = new int[3];
            int[] py = new int[3];

            // Definir vértices según dirección
            switch (dir) {
                case NORTE -> {
                    px = new int[]{rx + p, rx + cellSize - p, rx + cellSize / 2};
                    py = new int[]{ry + cellSize - p, ry + cellSize - p, ry + p};
                }
                case SUR -> {
                    px = new int[]{rx + p, rx + cellSize - p, rx + cellSize / 2};
                    py = new int[]{ry + p, ry + p, ry + cellSize - p};
                }
                case ESTE -> {
                    px = new int[]{rx + p, rx + p, rx + cellSize - p};
                    py = new int[]{ry + p, ry + cellSize - p, ry + cellSize / 2};
                }
                case OESTE -> {
                    px = new int[]{rx + cellSize - p, rx + cellSize - p, rx + p};
                    py = new int[]{ry + p, ry + cellSize - p, ry + cellSize / 2};
                }
            }
            g2.fillPolygon(px, py, 3);
        }
    }
}