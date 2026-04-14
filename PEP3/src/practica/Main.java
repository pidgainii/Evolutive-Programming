package practica;

import javax.swing.*;
import practica.ui.Graphic;


import practica.real.*;


public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new Graphic().setVisible(true));
	}

}
