package practica.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import practica.real.Board;
import practica.real.Chromosome;
import practica.real.Vertex; 
import practica.real.DeliveryPoint;

public class BoardPanel extends JPanel {
    private Board board;
    
    private List<List<Vertex>> vanRoutes;
    private double[] vanProgress;
    private Timer simulationTimer;

    private final Color[] VAN_COLORS = {
            Color.GREEN,
            Color.MAGENTA,
            Color.CYAN,
            Color.ORANGE,
            Color.PINK,
            Color.BLUE
    };

    private final int MARGIN = 40;

    public BoardPanel(Board board) {
        this.board = board;
        this.setPreferredSize(new Dimension(700, 700));
        this.setBackground(new Color(245, 245, 245));
    }

    public void setBoard(Board board) {
        this.board = board;
        repaint();
    }
    
    public void startSimulation(Board board, Chromosome chromosome, int numVans) {

        this.board = board;

        vanRoutes = RouteDecoder.decodeRoutes(board, chromosome, numVans);

        vanProgress = new double[vanRoutes.size()];

        if (simulationTimer != null) {
            simulationTimer.stop();
        }

        simulationTimer = new Timer(10, e -> {

            boolean finished = true;

            for (int i = 0; i < vanProgress.length; i++) {

                if (vanProgress[i] < vanRoutes.get(i).size() - 1) {
                    vanProgress[i] += 0.01;
                    finished = false;
                }
            }

            repaint();

            if (finished) {
                simulationTimer.stop();
            }
        });

        simulationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (board == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        List<Vertex> vertices = board.getAllVertices();
        
        // 1. Dibujar Conexiones (Caminos)
        g2.setStroke(new BasicStroke(1f));
        g2.setColor(new Color(200, 200, 200)); 
        
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = i + 1; j < vertices.size(); j++) {
                double cost = board.getCost(i, j);
                if (cost != Double.POSITIVE_INFINITY) {
                    drawConnection(g2, vertices.get(i), vertices.get(j));
                }
            }
        }

        // 2. Dibujar Vértices estándar (Nodos de tránsito)
        for (Vertex v : vertices) {
            // No dibujamos aquí ni el Depot (ID 0) ni los puntos de entrega
            if (v.getId() != 0 && !isDeliveryPoint(v)) {
                drawVertex(g2, v, 8, new Color(70, 130, 180), false);
            }
        }

        // 3. Dibujar Puntos de Entrega (Rojos)
        for (DeliveryPoint dp : board.getDeliveryPoints()) {
            drawVertex(g2, dp, 12, new Color(220, 20, 60), true);
            
            Point p = worldToScreen(dp);
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("SansSerif", Font.ITALIC, 11));
            g2.drawString("Wait: " + dp.getWaitingTime() + "m", p.x + 10, p.y - 10);
        }

        // 4. Dibujar el DEPOT (ID 0) - Lo dibujamos al final para que esté encima de todo
        Vertex depot = board.getDepot();
        if (depot != null) {
            drawDepot(g2, depot);
        }
        
        drawVans(g2);
    }
    
    private void drawVans(Graphics2D g2) {

        if (vanRoutes == null) return;

        for (int i = 0; i < vanRoutes.size(); i++) {

            List<Vertex> route = vanRoutes.get(i);

            if (route.size() < 2) continue;

            double progress = vanProgress[i];

            int segment = (int) progress;

            if (segment >= route.size() - 1) {
                segment = route.size() - 2;
            }

            double localT = progress - segment;

            Vertex v1 = route.get(segment);
            Vertex v2 = route.get(segment + 1);

            double x = v1.getX() + (v2.getX() - v1.getX()) * localT;
            double y = v1.getY() + (v2.getY() - v1.getY()) * localT;

            Vertex interpolated = new Vertex(-1, x, y);

            Point p = worldToScreen(interpolated);

            g2.setColor(VAN_COLORS[i % VAN_COLORS.length]);

            g2.fillOval(p.x - 8, p.y - 8, 16, 16);

            g2.setColor(Color.BLACK);

            g2.drawOval(p.x - 8, p.y - 8, 16, 16);
        }
    }

    private void drawDepot(Graphics2D g2, Vertex v) {
        Point p = worldToScreen(v);
        int size = 16; // Más grande que los demás
        
        // Sombra/Borde exterior
        g2.setColor(Color.BLACK);
        g2.fillRect(p.x - size / 2 - 1, p.y - size / 2 - 1, size + 2, size + 2);
        
        // Cuerpo del Depot (Cuadrado Naranja)
        g2.setColor(new Color(255, 140, 0)); // Dark Orange
        g2.fillRect(p.x - size / 2, p.y - size / 2, size, size);
        
        // Etiqueta
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
    }

    private void drawVertex(Graphics2D g2, Vertex v, int size, Color color, boolean isDelivery) {
        Point p = worldToScreen(v);
        
        g2.setColor(color);
        g2.fillOval(p.x - size / 2, p.y - size / 2, size, size);
        
        g2.setColor(Color.DARK_GRAY);
        g2.drawOval(p.x - size / 2, p.y - size / 2, size, size);

        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2.drawString(String.valueOf(v.getId()), p.x + 6, p.y + 6);
    }

    private void drawConnection(Graphics2D g2, Vertex v1, Vertex v2) {
        Point p1 = worldToScreen(v1);
        Point p2 = worldToScreen(v2);
        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
    }

    private boolean isDeliveryPoint(Vertex v) {
        for (DeliveryPoint dp : board.getDeliveryPoints()) {
            if (dp.getId() == v.getId()) return true;
        }
        return false;
    }

    private Point worldToScreen(Vertex v) {
        int drawWidth = getWidth() - (MARGIN * 2);
        int drawHeight = getHeight() - (MARGIN * 2);

        int px = MARGIN + (int) ((v.getX() / 100.0) * drawWidth);
        int py = MARGIN + (int) ((v.getY() / 100.0) * drawHeight);

        return new Point(px, py);
    }
}