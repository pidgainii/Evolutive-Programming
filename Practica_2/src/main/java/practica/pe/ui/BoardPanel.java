package practica.pe.ui;

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
                g.setColor(switch (v) {
                    case 1 -> Color.DARK_GRAY; // muro
                    case 2 -> Color.ORANGE;    // cámara
                    case 3 -> Color.CYAN;      // visto (si lo implementas)
                    default -> Color.WHITE;
                });
                g.fillRect(j * cw, i * ch, cw, ch);
                g.setColor(Color.BLACK);
                g.drawRect(j * cw, i * ch, cw, ch);
            }
        }
    }
}