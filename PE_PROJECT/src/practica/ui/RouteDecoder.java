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
            routes.get(i).add(board.getDepot());
        }

        int currentVan = 0;

        for (int g : chromosome.getGenes()) {

            if (g > board.getDeliveryPoints().size()) {

                routes.get(currentVan).add(board.getDepot());

                currentVan++;

                if (currentVan >= numVans) break;

                routes.get(currentVan).add(board.getDepot());

                continue;
            }

            DeliveryPoint dp =
                    board.getDeliveryPoints().get(g - 1);

            routes.get(currentVan).add(dp);
        }

        for (int i = 0; i < routes.size(); i++) {

            List<Vertex> r = routes.get(i);

            if (r.get(r.size() - 1).getId() != 0) {
                r.add(board.getDepot());
            }
        }

        return routes;
    }
}