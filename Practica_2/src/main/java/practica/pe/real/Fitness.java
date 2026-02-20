package practica.pe.real;

import java.util.HashSet;
import java.util.Set;

public class Fitness {

    private final int[][] map;
    private final int NUM_CAMARAS;
    private final int R;
    private final int N;
    private final int M;
    private final double FOV;

    public Fitness(int[][] map, int numCamaras, int R, int N, int M, double FOV) {
        this.map = map;
        this.NUM_CAMARAS = numCamaras;
        this.R = R;
        this.N = N;
        this.M = M;
        this.FOV = FOV;
    }

    public int evaluate(Chromosome individual) {

        double[] g = individual.getGenes();
        Set<Pair> seen = new HashSet<>();
        Set<Pair> cameraPositions = new HashSet<>();
        int result = 0;
        double half_fov = FOV / 2;

        for (int camera = 0; camera < NUM_CAMARAS; camera++) {
            int base = camera * 3;

            double x = g[base];
            double y = g[base + 1];
            double theta = wrapTo360(g[base + 2]);

            if (x < 0 || y < 0 || x >= N || y >= M ) {
                result -= 100;
                continue;
            }

            int cellX = (int) Math.floor(x);
            int cellY = (int) Math.floor(y);

            Pair position = new Pair(cellX, cellY);

            if (map[cellX][cellY] == 0 || cameraPositions.contains(position)) {
                result -= 100;
                continue;
            }
            else {
                cameraPositions.add(position);
            }

            int minX = Math.max(0, (int) Math.floor(x - R));
            int maxX = Math.min(N - 1, (int) Math.floor(x + R));
            int minY = Math.max(0, (int) Math.floor(y - R));
            int maxY = Math.min(M - 1, (int) Math.floor(y + R));

            for (int i = minX; i <= maxX; i++) {
                for (int j = minY; j <= maxY; j++) {

                    // calculamos el centro de la casilla objetivo
                    double ox = i + 0.5;
                    double oy = j + 0.5;

                    // calculamos la distancia
                    double dx = ox - x;
                    double dy = oy - y;
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    if (dist > R) continue;

                    // calculamos si está dentro del cono de visión
                    double phi = Math.toDegrees(Math.atan2(dy, dx));
                    double delta = wrapTo180(phi - theta);
                    if (Math.abs(delta) > half_fov) continue;

                    // calculamos la línea de visión
                    if (hasObstacleInSight(x, y, ox, oy)) continue;

                    Pair cell = new Pair(i, j);
                    if (!seen.contains(cell)) {
                        seen.add(cell);
                        result += map[i][j];
                    }

                }
            }
        }
        return result;
    }

    private boolean hasObstacleInSight(double x0, double y0, double x1, double y1) {

        double dx =  x1 - x0;
        double dy = y1 - y0;
        double dist = Math.sqrt(dx * dx + dy * dy);

        int steps = Math.max(1, (int) Math.ceil(dist / 0.1));
        double movX = dx / steps;
        double movY = dy / steps;

        double startX = x0;
        double startY = y0;

        for (int step = 0; step <= steps; step++) {

            int cellX = (int) Math.floor(startX);
            int cellY = (int) Math.floor(startY);

            if (cellX < 0 || cellY < 0 || cellX >= N || cellY >= M ) {
                return true;
            }

            if (map[cellX][cellY] == 0) {
                return true;
            }

            startX += movX;
            startY += movY;
        }

        return false;
    }

    private static double wrapTo360(double deg) {
        deg = deg % 360.0;
        if (deg < 0) deg += 360.0;
        return deg;
    }

    private static double wrapTo180(double deg) {
        deg = (deg + 180.0) % 360.0;
        if (deg < 0) deg += 360.0;
        return deg - 180.0;
    }
}
