package practica.ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import java.awt.BasicStroke;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import practica.real.Pair;
import practica.GARunner;
import practica.Maps;
import practica.real.Board;




public class Graphic extends JFrame {
	
	// ELEMENTOS VISUALES
	private final JComboBox<String> cbScenario = new JComboBox<>(new String[]{"MAP 1 (10x10)", "MAP 2 (12x15)", "MAP 3 (18x20)"});
    private final JSpinner spPop = new JSpinner(new SpinnerNumberModel(100, 2, 5000, 10));
    private final JSpinner spGen = new JSpinner(new SpinnerNumberModel(200, 1, 100000, 10));
    private final JSpinner spPc  = new JSpinner(new SpinnerNumberModel(0.60, 0.0, 1.0, 0.01));
    private final JSpinner spPm  = new JSpinner(new SpinnerNumberModel(0.05, 0.0, 1.0, 0.001));
    private final JSpinner spNCam  = new JSpinner(new SpinnerNumberModel(40, 0, 50, 1));
    private final JSpinner spNDrones  = new JSpinner(new SpinnerNumberModel(3, 0, 5, 1));

    private final JComboBox<String> selMethod = new JComboBox<>(new String[]{"ROULETTE", "TOURNAMENT", "STOCHASTIC", "TRUNCATION", "REMAINDERS"});
    private final JComboBox<String> crossMethod = new JComboBox<>(new String[]{"ONE_POINT", "UNIFORM", "ARITHMETIC", "BLX_ALPHA"});
    private final JComboBox<String> mutMethod = new JComboBox<>(new String[]{"GAUSSIAN", "GENE"});
    private final JSpinner spElit = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.05));
    private final JSpinner spSeed = new JSpinner(
            new SpinnerNumberModel(3000, 0, Integer.MAX_VALUE, 1)
    );

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
	
	private int[][] map;
    
    
	
	
    
    // CONSTRUCTOR
    public Graphic() {
    	super("Práctica 2");
    	
    	this.map = Maps.MAP1;

        int seed = (Integer) spSeed.getValue();
        int num_camaras = (Integer) spNCam.getValue();

        board = new Board(Maps.MAP1, seed, num_camaras);
        boardPanel = new BoardPanel(board);

        cbScenario.addActionListener(e -> changeScenario());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        add(buildLeftPanel(), BorderLayout.WEST);
        add(buildRightMainPanel(), BorderLayout.CENTER);

        btnRun.addActionListener(e -> run());

        setSize(1200,800);
        setLocationRelativeTo(null);
    }
    
    // FUNCION PARA CAMBIAR DE ESCENARIO Y ACTUALIZAR MAP
    // this.board cogerá this.map cuando se llame al metodo run
    private void changeScenario() {
        this.map = switch (cbScenario.getSelectedIndex()) {
            case 0 -> Maps.MAP1;
            case 1 -> Maps.MAP2;
            case 2 -> Maps.MAP3;
            default -> Maps.MAP1;
        };
        
        int seed = (Integer) spSeed.getValue();
        int num_camaras = (Integer) spNCam.getValue();
        this.board = new Board(this.map, seed, num_camaras);
        this.boardPanel.setBoard(this.board);
    }
    
    
    // METODO RUN
    private void run() {
    	btnRun.setEnabled(false);
    	cbScenario.setEnabled(false);
        txt.setText("");
        lblBest.setText("Best: ejecutando...");

        sBestGen.clear(); sBestEver.clear(); sAvg.clear();
        
        int popSize = (Integer) spPop.getValue();
        int gens = (Integer) spGen.getValue();
        double pc = (Double) spPc.getValue();
        double pm = (Double) spPm.getValue();
        double elit = (Double) spElit.getValue();
        int seed = (Integer) spSeed.getValue();
        int num_camaras = (Integer) spNCam.getValue();

        String selectionMethod = (String) selMethod.getSelectedItem();
        String crossoverMethod = (String) crossMethod.getSelectedItem();
        String mutationMethod = (String) mutMethod.getSelectedItem();
        
        
        // Creamos un nuevo board con el numero de camaras y semilla indicados en la UI (y mapa actualizado)
        this.board = new Board(this.map, seed, num_camaras);
        boardPanel.setBoard(this.board);
        
        
        // Ahora tenemos que crear un listener para que la evolución vaya llamandole
        // y así se actualice la GUI (boardPanel)
        EvolutionListener listener = (gen, bestGen, bestEver, avg, bestChrObj) -> SwingUtilities.invokeLater(() -> {
            // IMPORTANT: addOrUpdate avoids SeriesException when X repeats
            sBestGen.addOrUpdate(gen, bestGen);
            sBestEver.addOrUpdate(gen, bestEver);
            sAvg.addOrUpdate(gen, avg);
            lblBest.setText(String.format("Best: %.6f", bestEver));


            // Que hay que dibujar del mapa?
            // De momento nada, ahora las camaras estan fijas
            // Mas adelante habrá que dibujar los cromosomas (rutas por las que van los drones)
            
            // ESTA ES LA LLAMADA CLAVE. Esto hace que se actualice el panel
            // Mas adelante, boardPanel dibujará también el cromosoma en el mapa (rutas)
            boardPanel.setBoard(this.board);
        });
        
        
        new Thread(() -> {
            try {

                GAResult result = GARunner.run(board, popSize, gens, pc, pm, elit, selectionMethod, crossoverMethod, mutationMethod, listener);
                		
                		
                Object bestObj = result.getBest();

                SwingUtilities.invokeLater(() -> {
                    btnRun.setEnabled(true);
                    cbScenario.setEnabled(true);

                    practica.real.Chromosome best = (practica.real.Chromosome) bestObj;
                    txt.append("Best fitness: " + best.getFitness() + "\n");
                    txt.append("Best genes: " + best.getGenes().toString() + "\n");
                    
                    // TEST TEST TEST
                    this.boardPanel.setChromosome(best);
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    btnRun.setEnabled(true);
                    cbScenario.setEnabled(true);
                    lblBest.setText("Best: error");
                    txt.setText("Error: " + ex);
                });
            }
        }).start();
        
        
        
    }
    
    
    
    
    
    
    // LEFT PANEL (VERTICAL PARAMETERS)
    private JPanel buildLeftPanel() {

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        container.setPreferredSize(new Dimension(280,500));

        container.add(buildTopPanel());

        JPanel actions = new JPanel(new FlowLayout());
        actions.add(btnRun);
        actions.add(lblBest);

        container.add(Box.createVerticalStrut(10));
        container.add(actions);

        return container;
    }

    // PARAMETERS PANEL
    private JPanel buildTopPanel() {

        JPanel left = new JPanel(new GridLayout(0,2,8,6));
        left.setBorder(BorderFactory.createTitledBorder("GA Parameters"));

        left.add(new JLabel("Escenario:"));
        left.add(cbScenario);

        left.add(new JLabel("Población:"));
        left.add(spPop);

        left.add(new JLabel("Generaciones:"));
        left.add(spGen);

        left.add(new JLabel("Pc:"));
        left.add(spPc);

        left.add(new JLabel("Pm:"));
        left.add(spPm);

        left.add(new JLabel("Elitismo:"));
        left.add(spElit);

        JPanel mid = new JPanel(new GridLayout(0,2,8,6));
        mid.setBorder(BorderFactory.createTitledBorder("Problem"));

        mid.add(new JLabel("Número de cámaras:"));
        mid.add(spNCam);

        mid.add(new JLabel("Número de drones:"));
        mid.add(spNDrones);

        mid.add(new JLabel("Semilla:"));
        mid.add(spSeed);

        JPanel right = new JPanel(new GridLayout(0,2,8,6));
        right.setBorder(BorderFactory.createTitledBorder("Operators"));

        right.add(new JLabel("Selección:"));
        right.add(selMethod);

        right.add(new JLabel("Cruce:"));
        right.add(crossMethod);

        right.add(new JLabel("Mutación:"));
        right.add(mutMethod);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(left);
        panel.add(mid);
        panel.add(right);

        return panel;
    }

    // RIGHT SIDE LAYOUT
    private JPanel buildRightMainPanel() {

        JPanel main = new JPanel(new BorderLayout(10,10));
        main.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // ================= LEFT SIDE (chart + log) =================
        JPanel leftSide = new JPanel();
        leftSide.setLayout(new BoxLayout(leftSide, BoxLayout.Y_AXIS));
        leftSide.setPreferredSize(new Dimension(400, 800));

        // Chart
        ChartPanel chart = buildCenterChart();
        chart.setPreferredSize(new Dimension(400, 300));
        chart.setBorder(BorderFactory.createTitledBorder("Evolution"));

        // Log
        txt.setEditable(false);
        txt.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane log = new JScrollPane(txt);
        log.setPreferredSize(new Dimension(400, 150));
        log.setBorder(BorderFactory.createTitledBorder("Execution Log"));

        leftSide.add(chart);
        leftSide.add(Box.createVerticalStrut(10));
        leftSide.add(log);

        // ================= RIGHT SIDE (BIG BOARD) =================
        JPanel boardContainer = new JPanel(new BorderLayout());
        boardContainer.setBorder(BorderFactory.createTitledBorder("Board"));

        boardPanel.setPreferredSize(new Dimension(800, 800)); // 🔥 BIG
        boardContainer.add(boardPanel, BorderLayout.CENTER);

        // ================= SPLIT =================
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                leftSide,
                boardContainer
        );

        split.setResizeWeight(0.3); // 30% left, 70% board
        split.setDividerLocation(400);

        main.add(split, BorderLayout.CENTER);

        return main;
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
        
        XYPlot plot = chart.getXYPlot();

	     // cambiar color de las series (opcional, más visible)
	     plot.getRenderer().setSeriesPaint(0, Color.RED);    // Mejor generación
	     plot.getRenderer().setSeriesPaint(1, Color.BLUE);   // Mejor histórico
	     plot.getRenderer().setSeriesPaint(2, Color.GREEN);  // Media
	
	     // aumentar grosor de las líneas
	     plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
	     plot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
	     plot.getRenderer().setSeriesStroke(2, new BasicStroke(1.0f));

	     plot.setBackgroundPaint(Color.WHITE);
	     plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
	     plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
	     chart.getTitle().setFont(new Font("Arial", Font.BOLD, 16));
	     chart.getLegend().setItemFont(new Font("Arial", Font.PLAIN, 12));

        return new ChartPanel(chart);
    }
   
    
    
    
    
    
    
    
    
    
    
    
    
    
}