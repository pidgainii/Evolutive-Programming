package practica.real;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {
    private final List<Vertex> allVertices;
    private final List<DeliveryPoint> deliveryPoints;
    private final double[][] adjacencyMatrix;
    private Vertex depot; // The starting point for all vans
    
    private final int NUM_TOTAL_POINTS;
    private final int NUM_DELIVERY_POINTS;
    private final double MAP_LIMIT = 100.0;
    
    private final double MAX_DISTANCE = 30.0;           
    private final double CONNECTION_PROBABILITY = 0.1; 

    public Board(int numPoints, int numDeliveries, long mapSeed, long deliverySeed) {
        // We add +1 to numPoints to account for the Depot
        this.NUM_TOTAL_POINTS = numPoints + 1;
        this.NUM_DELIVERY_POINTS = numDeliveries;
        this.allVertices = new ArrayList<>();
        this.deliveryPoints = new ArrayList<>();
        this.adjacencyMatrix = new double[NUM_TOTAL_POINTS][NUM_TOTAL_POINTS];

        for (int i = 0; i < NUM_TOTAL_POINTS; i++) {
            for (int j = 0; j < NUM_TOTAL_POINTS; j++) {
                adjacencyMatrix[i][j] = (i == j) ? 0 : Double.POSITIVE_INFINITY;
            }
        }

        // 1. Create the Depot first at the center
        generateDepot();

        // 2. Generate the rest of the points around it
        generateEquidistantPoints(mapSeed);
        
        // 3. Connect everything ensuring reachability
        calculateConnectedSparseMatrix(mapSeed); 
        
        // 4. Select delivery points (Depot is excluded from being a delivery target)
        selectDeliveryPoints(deliverySeed);
    }

    private void generateDepot() {
        // Depot is always ID 0 and located at the center (50, 50)
        this.depot = new Vertex(0, MAP_LIMIT / 2.0, MAP_LIMIT / 2.0);
        allVertices.add(this.depot);
    }

    private void generateEquidistantPoints(long seed) {
        Random rand = new Random(seed);
        // We adjust grid calculation to fill the space for the remaining points
        int gridSize = (int) Math.ceil(Math.sqrt(NUM_TOTAL_POINTS - 1));
        double cellSize = MAP_LIMIT / gridSize;

        int pointsCreated = 1; // Start at 1 because 0 is the Depot
        for (int row = 0; row < gridSize && pointsCreated < NUM_TOTAL_POINTS; row++) {
            for (int col = 0; col < gridSize && pointsCreated < NUM_TOTAL_POINTS; col++) {
                double baseX = col * cellSize;
                double baseY = row * cellSize;
                
                // Jitter
                double jitterX = rand.nextDouble() * cellSize * 0.8;
                double jitterY = rand.nextDouble() * cellSize * 0.8;

                allVertices.add(new Vertex(pointsCreated, baseX + jitterX, baseY + jitterY));
                pointsCreated++;
            }
        }
    }

    private void calculateConnectedSparseMatrix(long seed) {
        Random rand = new Random(seed + 1);

        // 1. Mandatory nearest-neighbor connection
        for (int i = 0; i < allVertices.size(); i++) {
            int closestIdx = -1;
            double minDist = Double.MAX_VALUE;

            for (int j = 0; j < allVertices.size(); j++) {
                if (i == j) continue;

                double dist = calculateEuclideanDistance(
                        allVertices.get(i),
                        allVertices.get(j)
                );

                if (dist < minDist) {
                    minDist = dist;
                    closestIdx = j;
                }
            }

            if (closestIdx != -1) {
                adjacencyMatrix[i][closestIdx] = minDist;
                adjacencyMatrix[closestIdx][i] = minDist;
            }
        }

        // 2. Extra random sparse connections
        for (int i = 0; i < allVertices.size(); i++) {
            for (int j = i + 1; j < allVertices.size(); j++) {

                if (adjacencyMatrix[i][j] == Double.POSITIVE_INFINITY) {

                    double dist = calculateEuclideanDistance(
                            allVertices.get(i),
                            allVertices.get(j)
                    );

                    if (dist <= MAX_DISTANCE &&
                            rand.nextDouble() < CONNECTION_PROBABILITY) {

                        adjacencyMatrix[i][j] = dist;
                        adjacencyMatrix[j][i] = dist;
                    }
                }
            }
        }

        // 3. Ensure full connectivity
        ensureConnectedGraph();
    }

    private double calculateEuclideanDistance(Vertex v1, Vertex v2) {
        return Math.sqrt(Math.pow(v1.getX() - v2.getX(), 2) + Math.pow(v1.getY() - v2.getY(), 2));
    }

    private void selectDeliveryPoints(long seed) {
        Random rand = new Random(seed);
        // We create a pool starting from index 1 to avoid picking the Depot as a delivery
        List<Vertex> pool = new ArrayList<>(allVertices.subList(1, allVertices.size()));
        
        for (int i = 0; i < NUM_DELIVERY_POINTS && !pool.isEmpty(); i++) {
            Vertex selected = pool.remove(rand.nextInt(pool.size()));
            deliveryPoints.add(new DeliveryPoint(selected, 5 + rand.nextInt(26)));
        }
    }
    
    private void ensureConnectedGraph() {

        boolean[] visited = new boolean[NUM_TOTAL_POINTS];
        dfs(0, visited);

        while (!allVisited(visited)) {

            int disconnectedNode = -1;

            for (int i = 0; i < visited.length; i++) {
                if (!visited[i]) {
                    disconnectedNode = i;
                    break;
                }
            }

            // Find closest visited node
            int closestConnected = -1;
            double minDist = Double.MAX_VALUE;

            for (int i = 0; i < visited.length; i++) {

                if (visited[i]) {

                    double dist = calculateEuclideanDistance(
                            allVertices.get(i),
                            allVertices.get(disconnectedNode)
                    );

                    if (dist < minDist) {
                        minDist = dist;
                        closestConnected = i;
                    }
                }
            }

            // Connect component to graph
            adjacencyMatrix[disconnectedNode][closestConnected] = minDist;
            adjacencyMatrix[closestConnected][disconnectedNode] = minDist;

            // Recalculate reachability
            visited = new boolean[NUM_TOTAL_POINTS];
            dfs(0, visited);
        }
    }

    private void dfs(int node, boolean[] visited) {

        visited[node] = true;

        for (int i = 0; i < NUM_TOTAL_POINTS; i++) {

            if (!visited[i] &&
                    adjacencyMatrix[node][i] != Double.POSITIVE_INFINITY) {

                dfs(i, visited);
            }
        }
    }

    private boolean allVisited(boolean[] visited) {

        for (boolean v : visited) {
            if (!v) {
                return false;
            }
        }

        return true;
    }

    // --- Getters ---
    public Vertex getDepot() { return depot; }
    public List<Vertex> getAllVertices() { return allVertices; }
    public List<DeliveryPoint> getDeliveryPoints() { return deliveryPoints; }
    public double getCost(int idFrom, int idTo) { return adjacencyMatrix[idFrom][idTo]; }
    public double[][] getAdjacencyMatrix() { return adjacencyMatrix; }
}