package practica.real;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.ArrayList;

public class Dijkstra {


	public static List<Vertex> shortestPath(Board board, int from, int to) {
	    int n = board.getAllVertices().size();
	    double[] dist = new double[n];
	    int[] prev = new int[n];
	    Arrays.fill(dist, Double.POSITIVE_INFINITY);
	    Arrays.fill(prev, -1);
	    dist[from] = 0.0;

	    PriorityQueue<Integer> queue = new PriorityQueue<>(Comparator.comparingDouble(i -> dist[i]));
	    queue.add(from);

	    double[][] adj = board.getAdjacencyMatrix();

	    while (!queue.isEmpty()) {
	        int u = queue.poll();
	        if (u == to) break;
	        for (int v = 0; v < n; v++) {
	            if (adj[u][v] != Double.POSITIVE_INFINITY) {
	                double alt = dist[u] + adj[u][v];
	                if (alt < dist[v]) {
	                    dist[v] = alt;
	                    prev[v] = u;
	                    queue.add(v);
	                }
	            }
	        }
	    }
	    // reconstruct path from 'from' to 'to'
	    List<Vertex> path = new ArrayList<>();
	    int curr = to;
	    if (prev[curr] != -1 || curr == from) {
	        while (curr != -1) {
	            path.add(0, board.getAllVertices().get(curr));
	            curr = prev[curr];
	        }
	    }
	    return path;
	}

	public static double shortestPathCost(Board board, int from, int to) {
        int n = board.getAllVertices().size();
        double[] dist = new double[n];
        boolean[] visited = new boolean[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        dist[from] = 0.0;

        PriorityQueue<Integer> queue = new PriorityQueue<>((i, j) -> Double.compare(dist[i], dist[j]));
        queue.add(from);

        double[][] adj = board.getAdjacencyMatrix();

        while (!queue.isEmpty()) {
            int u = queue.poll();
            if (u == to) break;
            if (visited[u]) continue;
            visited[u] = true;

            for (int v = 0; v < n; v++) {
                if (adj[u][v] != Double.POSITIVE_INFINITY && !visited[v]) {
                    double alt = dist[u] + adj[u][v];
                    if (alt < dist[v]) {
                        dist[v] = alt;
                        queue.add(v);
                    }
                }
            }
        }
        return dist[to];
    }
}
