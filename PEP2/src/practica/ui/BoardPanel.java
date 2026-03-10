package practica.ui;

import javax.swing.*;

import practica.real.Board;
import practica.real.Pair;
import java.awt.*;
import java.util.ArrayList;

public class BoardPanel extends JPanel {
    private Board board;

    public BoardPanel(Board board) {
        setPreferredSize(new Dimension(300, 300));
    }

    public void setBoard(Board board) {
        this.board = board;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (board == null) return;

        int rows = board.getMap().length;
        int cols = board.getMap()[0].length;
        int cw = getWidth() / cols;
        int ch = getHeight() / rows;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int v = board.getMap()[i][j];

                Color color;

                if (v == 0) color = Color.DARK_GRAY;
                else if (v >= 20) color = new Color(255, 80, 80);
                else if (v >= 15) color = new Color(255, 140, 0);
                else if (v >= 10) color = new Color(255, 215, 0);
                else if (v >= 5)  color = new Color(220, 220, 220);
                else              color = Color.WHITE;

                g.setColor(color);
                g.fillRect(j * cw, i * ch, cw, ch);
                g.setColor(Color.BLACK);
                g.drawRect(j * cw, i * ch, cw, ch);
            }
        }

        // Dibujar cámaras (círculos azules)
        
        ArrayList<Pair> camaras = board.getCamaras();
        
        if (camaras != null) {
            g.setColor(Color.BLUE);
            for (Pair p : camaras) {
                int x = p.x() * cw;
                int y = p.y() * ch;
                g.fillOval(x + cw/4, y + ch/4, cw/2, ch/2);
            }
        }
    }
}