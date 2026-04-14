package practica;

import java.util.*;

import practica.real.Pair;

public class AStar {
	
	static final int PENALTY = 500;

    public static ArrayList<Pair> a_star(int[][] map, Pair start, Pair goal, Set<Pair> cameraPositions) {

        int rows = map.length;
        int cols = map[0].length;

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        HashSet<Pair> closed = new HashSet<>();

        HashMap<Pair, Pair> cameFrom = new HashMap<>();
        HashMap<Pair, Integer> gScore = new HashMap<>();

        gScore.put(start, 0);
        open.add(new Node(start, heuristic(start, goal), 0));

        int[][] directions = {
            {-1,0}, {1,0}, {0,-1}, {0,1}
        };

        while (!open.isEmpty()) {

            Node currentNode = open.poll();
            Pair current = currentNode.pos;

            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }

            closed.add(current);

            for (int[] d : directions) {

                int nx = current.x() + d[0];
                int ny = current.y() + d[1];

                if (nx < 0 || ny < 0 || nx >= rows || ny >= cols)
                    continue;

                if (map[nx][ny] == 0)
                    continue;

                Pair neighbor = new Pair(nx, ny);

                if (closed.contains(neighbor))
                    continue;
                
                int stepCost = map[nx][ny];
                
                if (cameraPositions.contains(neighbor) && !neighbor.equals(goal)) {
                	stepCost += PENALTY;
                }

                
                int tentativeG = gScore.get(current) + stepCost;

                if (!gScore.containsKey(neighbor) || tentativeG < gScore.get(neighbor)) {

                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeG);

                    int f = tentativeG + heuristic(neighbor, goal);

                    open.add(new Node(neighbor, f, tentativeG));
                }
            }
        }

        return new ArrayList<>();
    }

    private static int heuristic(Pair a, Pair b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }

    private static ArrayList<Pair> reconstructPath(HashMap<Pair, Pair> cameFrom, Pair current) {

        ArrayList<Pair> path = new ArrayList<>();
        path.add(current);

        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, current);
        }

        return path;
    }

    private static class Node {
        Pair pos;
        int f;
        int g;

        Node(Pair pos, int f, int g) {
            this.pos = pos;
            this.f = f;
            this.g = g;
        }
    }
}