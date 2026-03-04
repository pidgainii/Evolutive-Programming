package practica.ui;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {
    private int[][] board;

    public BoardPanel(int[][] board) {
        this.board = board;
        setPreferredSize(new Dimension(300, 300));
    }

    public void setBoard(int[][] board) {
        this.board = board;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (board == null) return;

        int rows = board.length;
        int cols = board[0].length;
        int cw = getWidth() / cols;
        int ch = getHeight() / rows;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int v = board[i][j];

                Color color;

                // marcas del render (prioridad alta)
                if (v == 2) {
                    color = Color.ORANGE; // cámara
                } else if (v == 3) {
                    color = Color.CYAN;   // iluminado/visto
                } else {
                    // mapa base con valores (0=muro, >0 transitable con importancia)
                    if (v == 0) color = Color.DARK_GRAY;          // muro
                    else if (v >= 20) color = new Color(255, 80, 80);  // muy valioso (20)
                    else if (v >= 15) color = new Color(255, 140, 0);  // hotspot 15
                    else if (v >= 10) color = new Color(255, 215, 0);  // joya 10
                    else if (v >= 5)  color = new Color(220, 220, 220); // pasillo 5
                    else              color = Color.WHITE;        // libre 1
                }

                g.setColor(color);
                g.fillRect(j * cw, i * ch, cw, ch);
                g.setColor(Color.BLACK);
                g.drawRect(j * cw, i * ch, cw, ch);
            }
        }
    }
}