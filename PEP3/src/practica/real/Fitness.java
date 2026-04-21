package practica.real;

import java.util.ArrayList;
import java.util.Arrays;

public class Fitness {

	private Contexto c1;
	private Contexto c2;
	private Contexto c3;
	private double bloating;
	
    public Fitness(Contexto c1, Contexto c2, Contexto c3, double bloating) {
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
        this.bloating = bloating;
    }

    public double evaluate_final(Chromosome individual) {
        double f1 = this.evaluate_base(individual, this.c1);
        double f2 = this.evaluate_base(individual, this.c2);
        double f3 = this.evaluate_base(individual, this.c3);
        
        double bloating_penalization = individual.getTree().tam() * this.bloating;
        
        return (f1+f2+f3) / 3.0 - bloating_penalization;
    }
    
    public double evaluate_base(Chromosome individual, Contexto c) {
    	// Tenemos que "ejecutar" el cromosoma sobre ese contexto
    	individual.getTree().ejecutar(c);
    	
    	double fitness_base = c.getMuestras()*500 + c.getExploradas()*20 +
    			c.getRecompensaVisual()*2 - c.getPisadasArena()*30 - c.getColisiones()*10;
    	
    	// Penalización por pereza
    	if (c.getExploradas() < 4) {
    		fitness_base -= 1000;
    	}
    	
    	return fitness_base;
    }

}