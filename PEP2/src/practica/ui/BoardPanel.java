package practica.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import practica.real.Board;
import practica.real.Chromosome;
import practica.real.Pair;

public class BoardPanel extends JPanel {
    private Board board;
    private Chromosome chromosome;
    private ArrayList<ArrayList<Pair>> routes;

    public BoardPanel(Board board) {
        this.board = board;
        setPreferredSize(new Dimension(600, 600));
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

        // Draw map cells
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

        // Draw base
        Pair base = board.getBase();
        if (base != null) {
            int bx = base.y() * cw;
            int by = base.x() * ch;

            g.setColor(Color.RED);
            g.fillRect(bx, by, cw, ch);
            g.setColor(Color.BLACK);
            g.drawRect(bx, by, cw, ch);

            g.setColor(Color.WHITE);
            String t = "B";
            FontMetrics fm = g.getFontMetrics();
            int tx = bx + (cw - fm.stringWidth(t)) / 2;
            int ty = by + (ch + fm.getAscent()) / 2 - 2;
            g.drawString(t, tx, ty);
        }

        // Draw cameras
        ArrayList<Pair> camaras = board.getCamaras();
        if (camaras != null) {
            for (int idx = 0; idx < camaras.size(); idx++) {
                Pair p = camaras.get(idx);
                int x = p.y() * cw;
                int y = p.x() * ch;

                g.setColor(Color.BLUE);
                int diameter = Math.min(cw, ch) / 2;
                int cx = x + (cw - diameter) / 2;
                int cy = y + (ch - diameter) / 2;
                g.fillOval(cx, cy, diameter, diameter);

                g.setColor(Color.WHITE);
                String idText = String.valueOf(idx + 1);
                FontMetrics fm = g.getFontMetrics();
                int tx = x + (cw - fm.stringWidth(idText)) / 2;
                int ty = y + (ch + fm.getAscent()) / 2 - 2;
                g.drawString(idText, tx, ty);
            }
        }

        // Draw drone routes
        if (routes != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));

            Color[] colors = {Color.GREEN, Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.PINK};

            for (int r = 0; r < routes.size(); r++) {
                ArrayList<Pair> route = routes.get(r);
                if (route.isEmpty()) continue;

                g2.setColor(colors[r % colors.length]);

                Pair prev = base; // always start from base
                for (Pair p : route) {
                    int x1 = prev.y() * cw + cw / 2;
                    int y1 = prev.x() * ch + ch / 2;
                    int x2 = p.y() * cw + cw / 2;
                    int y2 = p.x() * ch + ch / 2;
                    g2.drawLine(x1, y1, x2, y2);
                    prev = p;
                }
            }
        }
    }

    // Build routes including return to base using real paths
    private ArrayList<ArrayList<Pair>> getDrawableRoutes(Chromosome chromosome) {
        ArrayList<ArrayList<Pair>> allRoutes = new ArrayList<>();
        if (chromosome == null) return allRoutes;

        ArrayList<Integer> genes = chromosome.getGenes();
        int numCams = board.getNumCamaras();
        ArrayList<Integer> currentDrone = new ArrayList<>();

        for (int g : genes) {
            if (g > numCams) { // separator → new drone
                allRoutes.add(buildRoute(currentDrone));
                currentDrone.clear();
            } else {
                currentDrone.add(g);
            }
        }

        // last drone
        allRoutes.add(buildRoute(currentDrone));

        return allRoutes;
    }

    private ArrayList<Pair> buildRoute(ArrayList<Integer> cams) {
        ArrayList<Pair> route = new ArrayList<>();
        if (cams.isEmpty()) return route;

        // base → first camera
        route.addAll(board.getRouteBaseCam(cams.get(0)));

        // between cameras
        for (int i = 0; i < cams.size() - 1; i++) {
            int c1 = cams.get(i) - 1;
            int c2 = cams.get(i + 1) - 1;
            route.addAll(board.getRoute(c1, c2));
        }

        // last camera → base (reverse base→camera path)
        ArrayList<Pair> backToBase = new ArrayList<>(board.getRouteBaseCam(cams.get(cams.size() - 1)));
        java.util.Collections.reverse(backToBase);
        route.addAll(backToBase);

        return route;
    }
}