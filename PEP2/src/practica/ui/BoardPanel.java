package practica.ui;

import javax.swing.*;

import practica.real.Board;
import practica.real.Pair;
import java.awt.*;
import java.util.ArrayList;

public class BoardPanel extends JPanel {
    private Board board;

    public BoardPanel(Board board) {
    	this.board = board;
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

        // 1) Dibujar la BASE en rojo (si existe)
        Pair base = board.getBase();
        if (base != null) {
            int bx = base.y() * cw;
            int by = base.x() * ch;

            g.setColor(Color.RED);
            g.fillRect(bx, by, cw, ch);

            // opcional: borde para que destaque
            g.setColor(Color.BLACK);
            g.drawRect(bx, by, cw, ch);

            // opcional: una "B" encima
            g.setColor(Color.WHITE);
            String t = "B";
            FontMetrics fm = g.getFontMetrics();
            int tx = bx + (cw - fm.stringWidth(t)) / 2;
            int ty = by + (ch + fm.getAscent()) / 2 - 2;
            g.drawString(t, tx, ty);
        }

        // 2) Dibujar cámaras (círculos azules) + su ID (1..N)
        ArrayList<Pair> camaras = board.getCamaras();
        if (camaras != null) {
            for (int idx0 = 0; idx0 < camaras.size(); idx0++) {
                Pair p = camaras.get(idx0);

                int x = p.y() * cw; // column
                int y = p.x() * ch; // row

                g.setColor(Color.BLUE);
                g.fillOval(x + cw/4, y + ch/4, cw/2, ch/2);

                String idText = String.valueOf(idx0 + 1);
                g.setColor(Color.WHITE);

                FontMetrics fm = g.getFontMetrics();
                int tx = x + (cw - fm.stringWidth(idText)) / 2;
                int ty = y + (ch + fm.getAscent()) / 2 - 2;
                g.drawString(idText, tx, ty);
            }
        }
    }
}