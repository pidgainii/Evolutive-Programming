package practica.ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import practica.real.Pair;
import practica.Maps;
import practica.real.Board;




public class Graphic2 extends JFrame {
	
	// ELEMENTOS VISUALES
	private final JComboBox<String> cbScenario = new JComboBox<>(new String[]{"MAP 1 (10x10)", "MAP 2 (12x15)", "MAP 3 (18x20)"});
    private final JSpinner spPop = new JSpinner(new SpinnerNumberModel(100, 2, 5000, 10));
    private final JSpinner spGen = new JSpinner(new SpinnerNumberModel(200, 1, 100000, 10));
    private final JSpinner spPc  = new JSpinner(new SpinnerNumberModel(0.60, 0.0, 1.0, 0.01));
    private final JSpinner spPm  = new JSpinner(new SpinnerNumberModel(0.05, 0.0, 1.0, 0.001));

    private final JComboBox<String> selMethod = new JComboBox<>(new String[]{"ROULETTE", "TOURNAMENT", "STOCHASTIC", "TRUNCATION", "REMAINDERS"});
    private final JComboBox<String> crossMethod = new JComboBox<>(new String[]{"ONE_POINT", "UNIFORM", "ARITHMETIC", "BLX_ALPHA"});
    private final JComboBox<String> mutMethod = new JComboBox<>(new String[]{"GAUSSIAN", "GENE"});
    private final JSpinner spElit = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.05));
    private final JSpinner spSeed = new JSpinner(
            new SpinnerNumberModel(3000, 0, Integer.MAX_VALUE, 1)
    );
    private final JCheckBox cbWeighted = new JCheckBox("Ponderado (bonus)");

    private final JButton btnRun = new JButton("Run");
    private final JLabel lblBest = new JLabel("Best: -");
    private final JTextArea txt = new JTextArea(6, 30);

    // series para la gráfica (IMPORTANT: allowDuplicateXValues=true)
    private final XYSeries sBestGen = new XYSeries("Mejor gen (rojo)", false, true);
    private final XYSeries sBestEver = new XYSeries("Mejor histórico (azul)", false, true);
    private final XYSeries sAvg = new XYSeries("Media (verde)", false, true);
	
    
    // BoardPanel renders the board
    private BoardPanel boardPanel;
    
	// board contiene el mapa y las camaras
	private Board board;

	// En el constructor, se añade un listener para que esta variable cambie de valor
	// cuando se haga click en el elemento visual
	private boolean ponderado;
    
    
	
	
    
    // CONSTRUCTOR
    public Graphic2() {
    	super("Práctica 2");

    	
        int seed = (Integer) spSeed.getValue();
        board = new Board(Maps.MAP1, seed, 10);
        boardPanel = new BoardPanel(board);

        cbScenario.addActionListener(e -> changeScenario());
        cbWeighted.addActionListener(e -> ponderado = cbWeighted.isSelected());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildCenterChart(), BorderLayout.CENTER);
        add(buildRightPanel(), BorderLayout.EAST);

        btnRun.addActionListener(e -> run());

        setSize(1050,650);
        setLocationRelativeTo(null);
    }
    
    // FUNCION PARA CAMBIAR DE ESCENARIO Y ACTUALIZAR BOARD
    private void changeScenario() {
        int[][] map = switch (cbScenario.getSelectedIndex()) {
            case 0 -> Maps.MAP1;
            case 1 -> Maps.MAP2;
            case 2 -> Maps.MAP3;
            default -> Maps.MAP1;
        };

        board = new Board(map, (Integer)spSeed.getValue(), 10);
        boardPanel.setBoard(board);
    }
    
    
    // METODO RUN
    private void run() {
    	btnRun.setEnabled(false);
    }
    
    
    
    
    
    
    
    // METODOS PARA LA CONSTRUCCION DEL UI
    
    private JPanel buildTopPanel() {
        JPanel form = new JPanel(new GridLayout(0, 4, 12, 8));
        form.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        form.add(new JLabel("Escenario:"));   form.add(cbScenario);

        form.add(new JLabel("Población:"));   form.add(spPop);
        form.add(new JLabel("Generaciones:"));form.add(spGen);

        form.add(new JLabel("Pc:"));          form.add(spPc);
        form.add(new JLabel("Pm:"));          form.add(spPm);

        form.add(new JLabel("Selección:"));   form.add(selMethod);
        form.add(new JLabel("Cruce:"));       form.add(crossMethod);

        form.add(new JLabel("Mutación:"));    form.add(mutMethod);
        form.add(new JLabel("Elitismo:"));    form.add(spElit);
        form.add(new JLabel("Semilla:"));     form.add(spSeed);

        form.add(new JLabel(""));            form.add(cbWeighted);
        form.add(new JLabel(""));            form.add(new JLabel(""));

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setBorder(BorderFactory.createEmptyBorder(0, 10, 8, 10));
        actions.add(btnRun);
        actions.add(lblBest);
        top.add(actions, BorderLayout.SOUTH);

        return top;
    }
    
    private ChartPanel buildCenterChart() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(sBestGen);
        dataset.addSeries(sBestEver);
        dataset.addSeries(sAvg);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Evolución",
                "Generación",
                "Fitness",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        return new ChartPanel(chart);
    }
    
    private JPanel buildRightPanel() {
        JPanel right = new JPanel(new BorderLayout(5, 5));
        right.setPreferredSize(new Dimension(360, 600));

        txt.setEditable(false);
        txt.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        right.add(new JScrollPane(txt), BorderLayout.NORTH);
        right.add(boardPanel, BorderLayout.CENTER);
        return right;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
}