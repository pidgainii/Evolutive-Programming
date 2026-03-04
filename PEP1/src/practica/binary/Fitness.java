package practica.binary;

import java.util.HashSet;
import java.util.Set;

public class Fitness {

        private final int[][] map;
        private final int NUM_CAMARAS;
        private final int RANGO_VISION;
        private final int N;
        private final int M;

        public Fitness(int[][] map, int numCamaras, int rangoVision, int N, int M) {
            this.map = map;
            this.NUM_CAMARAS = numCamaras;
            this.RANGO_VISION = rangoVision;
            this.N = N;
            this.M = M;
        }


        public int evaluate(Chromosome individual) {

            String genes = individual.getGenes();
            Set<String> set = new HashSet<>();
            Set<String> cameraPositions = new HashSet<>();
            int result = 0;

            int xBits = Integer.toBinaryString(N - 1).length();
            int yBits = Integer.toBinaryString(M - 1).length();

            for (int camera = 0; camera < NUM_CAMARAS; camera++) {

                int initial = camera * (xBits + yBits);

                String xBinary = genes.substring(initial, initial + xBits);
                String yBinary = genes.substring(initial + xBits, initial + xBits + yBits);

                int x = Integer.parseInt(xBinary, 2);
                int y = Integer.parseInt(yBinary, 2);

                    if (x < 0 || y < 0 || x >= N || y >= M || map[x][y] == 0) {
                    result -= 100;
                    continue;
                }

                String posCam = x + "," + y;
                if (cameraPositions.contains(posCam)) {
                    result -= 100;
                    continue;
                } else {
                    cameraPositions.add(posCam);
                }

                if (!set.contains(posCam)) {
                    set.add(posCam);
                    result += map[x][y];
                }

                int[] dx = {-1, 1, 0, 0};
                int[] dy = {0, 0, -1, 1};

                for (int dir = 0; dir < 4; dir++) {
                    for (int r = 1; r <= RANGO_VISION; r++) {

                        int newX = x + dx[dir] * r;
                        int newY = y + dy[dir] * r;

                        if (newX < 0 || newX >= N || newY < 0 || newY >= M)
                            break;

                        String pos = newX + "," + newY;

                        if (cameraPositions.contains(pos)) {
                            break;
                        }

                        if (map[newX][newY] != 0) {
                            if (!set.contains(pos)) {
                                set.add(pos);
                                result += map[newX][newY];
                            }
                        } else {
                            break;
                        }
                    }
                }
            }

            return result;
        }

}
