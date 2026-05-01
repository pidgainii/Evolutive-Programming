package practica.ui;

import practica.real.*;

import java.util.ArrayList;
import java.util.List;

public class RouteDecoder {
	public static List<List<Vertex>> decodeRoutes(
	        Board board,
	        Chromosome chromosome,
	        int numVans
	) {
	    List<List<Vertex>> routes = new ArrayList<>();
	    for (int i = 0; i < numVans; i++) {
	        routes.add(new ArrayList<>());
	    }
	    List<List<Integer>> stops = new ArrayList<>();
	    for (int i = 0; i < numVans; i++) stops.add(new ArrayList<>());
	    int currentVan = 0;
	    stops.get(currentVan).add(board.getDepot().getId());
	    for (int g : chromosome.getGenes()) {
	        if (g > board.getDeliveryPoints().size()) {
	            // separator
	            stops.get(currentVan).add(board.getDepot().getId());
	            currentVan++;
	            if (currentVan >= numVans) break;
	            stops.get(currentVan).add(board.getDepot().getId());
	        } else {
	            DeliveryPoint dp = board.getDeliveryPoints().get(g - 1);
	            stops.get(currentVan).add(dp.getId());
	        }
	    }
	    for (int i = 0; i < numVans; i++) {
	        List<Integer> vanStops = stops.get(i);
	        if (vanStops.get(vanStops.size() - 1) != 0) vanStops.add(0);
	    }

	    for (int i = 0; i < numVans; i++) {
	        List<Vertex> fullRoute = new ArrayList<>();
	        List<Integer> vanStops = stops.get(i);
	        for (int j = 0; j < vanStops.size() - 1; j++) {
	            int from = vanStops.get(j);
	            int to = vanStops.get(j + 1);
	            List<Vertex> path = Dijkstra.shortestPath(board, from, to);
	            if (!fullRoute.isEmpty() && path.size() > 1) {

	            	path = path.subList(1, path.size());
	            }
	            fullRoute.addAll(path);
	        }
	        routes.set(i, fullRoute);
	    }
	    return routes;
	}
}