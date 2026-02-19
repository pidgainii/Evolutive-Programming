package practica.pe;

import java.util.ArrayList;
import java.util.Random;

public class Crossing {

    public static ArrayList<Chromosome> crossing(ArrayList<Chromosome> population, int prob)
    {
        ArrayList<Chromosome> selected = new ArrayList<Chromosome>();
        ArrayList<Chromosome> newPopulation = Utils.copyPopulation(population);

        double probability = (double)(prob) / 100.0;
        // Selecting only some chromosomes to apply crossing
        for (Chromosome c: newPopulation) {
            if (Math.random() < probability) {
                selected.add(c);
            }
        }


        Random random = new Random();

        for (int i = 0; i < selected.size()-1; i+=2) {
            Chromosome p1 = selected.get(i);
            Chromosome p2 = selected.get(i+1);

            int size = p1.getChromosomeSize();
            int crossingPoint = random.nextInt(size);

            StringBuilder g1 = new StringBuilder(p1.getGenotype().substring(0, crossingPoint));
            g1.append(p2.getGenotype().substring(crossingPoint, size));

            StringBuilder g2 = new StringBuilder(p2.getGenotype().substring(0, crossingPoint));
            g2.append(p1.getGenotype().substring(crossingPoint, size));

            // Creating child chromosomes
            Chromosome child1 = new Chromosome(p1.getBoardSizeX(), p1.getBoardSizeY(), p1.getnCameras(), g1);
            Chromosome child2 = new Chromosome(p1.getBoardSizeX(), p1.getBoardSizeY(), p1.getnCameras(), g2);

            newPopulation.set(newPopulation.indexOf(p1), child1);
            newPopulation.set(newPopulation.indexOf(p2), child2);
        }
        return newPopulation;
    }
}
