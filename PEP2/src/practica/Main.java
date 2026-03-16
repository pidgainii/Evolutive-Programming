package practica;

import javax.swing.*;
import practica.ui.Graphic;


import practica.real.*;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// SwingUtilities.invokeLater(() -> new Graphic().setVisible(true));
		
		
		
		// TEMPORAL: JUST TO TEST CHROMOSOME CREATION AND FITNESS FUNCTION
		Board board = new Board(Maps.MAP1, 300033, 8);
		
		Fitness fitness = new Fitness(board);
		
		Population population = new Population(fitness, 100, 8, 3);
	}

}
