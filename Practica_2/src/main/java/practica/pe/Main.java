package practica.pe;

import practica.pe.real.Chromosome;
import practica.pe.real.Evolution;
import practica.pe.real.Fitness;
import practica.pe.real.Population;
import practica.pe.ui.Graphic;

import javax.swing.*;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Graphic().setVisible(true));
    }

}