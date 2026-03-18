package practica.ui;

import javax.swing.*;

import practica.real.Board;
import practica.real.Chromosome;
import practica.real.Pair;
import java.awt.*;
import java.util.ArrayList;

public class BoardPanel extends JPanel {
    private Board board;
    private Chromosome chromosome;
    private ArrayList<ArrayList<Pair>> routes;

    public BoardPanel(Board board) {
    	this.board = board;
        setPreferredSize(new Dimension(300, 300));
    }

    public void setBoard(Board board) {
        this.board = board;
        repaint();
    }
    
    public void setChromosome(Chromosome chromosome) {
    	this.chromosome = chromosome;
    	this.routes = getDrawableRoutes(chromosome);
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
    
    
    
    private ArrayList<ArrayList<Pair>> getDrawableRoutes(Chromosome chromosome) {

        ArrayList<ArrayList<Pair>> allRoutes = new ArrayList<>();
        if (chromosome == null) return allRoutes;

        ArrayList<Integer> genes = chromosome.getGenes();
        int numCams = board.getNumCamaras();

        ArrayList<Integer> currentCams = new ArrayList<>();

        for (int g : genes) {

            // Separator → close current drone route
            if (g > numCams) {

                allRoutes.add(buildRoute(currentCams));
                currentCams.clear();

            } else {
                currentCams.add(g);																		
            }
        }

        // last drone
        allRoutes.add(buildRoute(currentCams));

        return allRoutes;
    }

    private ArrayList<Pair> buildRoute(ArrayList<Integer> cams) {

        ArrayList<Pair> route = new ArrayList<>();

        if (cams.size() == 0) return route;

        // Optional: start from base → first camera
        Pair base = board.getBase();
        Pair firstCam = board.getCamaraById(cams.get(0));
        route.addAll(board.getRoute(
            board.getCamaras().indexOf(firstCam),
            board.getCamaras().indexOf(firstCam)
        ));

        // Between cameras
        for (int i = 0; i < cams.size() - 1; i++) {

            int c1 = cams.get(i) - 1;     // to 0-based
            int c2 = cams.get(i + 1) - 1;

            ArrayList<Pair> segment = board.getRoute(c1, c2);
            if (segment != null) {
                route.addAll(segment);
            }
        }

        return route;
    }
}