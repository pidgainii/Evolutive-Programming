package practica.real;

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
        
        // Penalización por tamaño del árbol para evitar que crezca sin control
        double bloating_penalization = individual.getTree().tam() * this.bloating * 7;
        
        double final_fitness =  ((f1 + f2 + f3) / 3.0) - bloating_penalization;
        
        return final_fitness;
    }
    
    public double evaluate_base(Chromosome individual, Contexto c_original) {
        Contexto c = c_original.copy();
        
        while (c.estaVivo() && c.getTicks() < 150) {
            int ticksAntes = c.getTicks();
            c.setAccionTomada(false);
            // Ejecuta la lógica del árbol (AST)
            individual.getTree().ejecutar(c);
            
            if (c.getTicks() == ticksAntes) {
                break;
            }
        }
        
        double fitness_base = c.getMuestras() * 500 + 
                             c.getExploradas() * 20 +
                             c.getRecompensaVisual() * 2 - 
                             c.getPisadasArena() * 30 - 
                             c.getColisiones() * 10;
        
        if (c.getExploradas() < 4) {
            fitness_base -= 1000;
        }
        
        return fitness_base;
    }
}