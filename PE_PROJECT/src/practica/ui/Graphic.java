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
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Arrays;

import practica.real.Pair; // Puedes quitar esto si ya no usas Pair
import practica.GARunner;
import practica.ui.GAResult; // Asumiendo que tienes esta clase
import practica.real.Board;
import practica.real.Chromosome;

public class Graphic extends JFrame {
	
    // ELEMENTOS VISUALES
    private final JSpinner spPop = new JSpinner(new SpinnerNumberModel(100, 2, 5000, 10));
    private final JSpinner spGen = new JSpinner(new SpinnerNumberModel(200, 1, 100000, 10));
    private final JSpinner spPc  = new JSpinner(new SpinnerNumberModel(0.60, 0.0, 1.0, 0.01));
    private final JSpinner spPm  = new JSpinner(new SpinnerNumberModel(0.1, 0.0, 1.0, 0.001));
    
    // Nuevos parámetros para el grafo
    private final JSpinner spNPoints = new JSpinner(new SpinnerNumberModel(50, 5, 500, 1));
    private final JSpinner spNDeliveries = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
    private final JSpinner spMapSeed = new JSpinner(new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1));
    private final JSpinner spDeliverySeed = new JSpinner(new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1));
    
    private final JSpinner spNVans  = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));

    private final JComboBox<String> selMethod = new JComboBox<>(new String[]{"ROULETTE", "TOURNAMENT", "STOCHASTIC", "TRUNCATION", "REMAINDERS", "RANKING"});
    private final JComboBox<String> crossMethod = new JComboBox<>(new String[]{"PMX", "OX", "OXPP", "CX", "CO", "ERX"});
    private final JComboBox<String> mutMethod = new JComboBox<>(new String[]{"INSERTION", "SWAP", "INVERSION", "HEURISTIC", "BALANCE_MOVE"});
    private final JSpinner spElit = new JSpinner(new SpinnerNumberModel(0.15, 0.0, 1.0, 0.05));

    private final JButton btnRun = new JButton("Run");
    private final JLabel lblBest = new JLabel("Best: -");
    private final JTextPane txt = new JTextPane();

    // series para la gráfica (IMPORTANT: allowDuplicateXValues=true)
    private final XYSeries sBestGen = new XYSeries("Mejor gen (rojo)", false, true);
    private final XYSeries sBestEver = new XYSeries("Mejor histórico (azul)", false, true);
    private final XYSeries sAvg = new XYSeries("Media (verde)", false, true);
	
    // BoardPanel renders the board
    private BoardPanel boardPanel;
	
    // board contiene los vértices, matriz de adyacencia y puntos de entrega
    private Board board;
    
    // CONSTRUCTOR
    public Graphic() {
        super("Práctica 2 - Furgonetas y Grafos");
        
        // Inicializar el tablero por primera vez
        updateBoardPreview();
        boardPanel = new BoardPanel(board);

        // Actualizar el mapa visualmente si el usuario cambia los parámetros del grafo antes de ejecutar
        ChangeListener boardUpdater = e -> {
            updateBoardPreview();
            boardPanel.setBoard(this.board);
        };
        spNPoints.addChangeListener(boardUpdater);
        spNDeliveries.addChangeListener(boardUpdater);
        spMapSeed.addChangeListener(boardUpdater);
        spDeliverySeed.addChangeListener(boardUpdater);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        add(buildLeftPanel(), BorderLayout.WEST);
        add(buildRightMainPanel(), BorderLayout.CENTER);

        btnRun.addActionListener(e -> run());

        setSize(1300, 800);
        setLocationRelativeTo(null);
    }
    
    // Función para crear/actualizar el board basado en la UI
    private void updateBoardPreview() {
        int nPoints = (Integer) spNPoints.getValue();
        int nDeliveries = (Integer) spNDeliveries.getValue();
        long mapSeed = ((Integer) spMapSeed.getValue()).longValue();
        long delivSeed = ((Integer) spDeliverySeed.getValue()).longValue();
        
        // Evitamos que haya más entregas que puntos totales
        if (nDeliveries > nPoints) {
            spNDeliveries.setValue(nPoints);
            nDeliveries = nPoints;
        }

        this.board = new Board(nPoints, nDeliveries, mapSeed, delivSeed);
    }
    
    // METODO RUN
    private void run() {
        btnRun.setEnabled(false);
        txt.setText("");
        lblBest.setText("Best: ejecutando...");

        sBestGen.clear(); sBestEver.clear(); sAvg.clear();
        
        int popSize = (Integer) spPop.getValue();
        int gens = (Integer) spGen.getValue();
        double pc = (Double) spPc.getValue();
        double pm = (Double) spPm.getValue();
        double elit = (Double) spElit.getValue();
        int num_vans = (Integer) spNVans.getValue();

        String selectionMethod = (String) selMethod.getSelectedItem();
        String crossoverMethod = (String) crossMethod.getSelectedItem();
        String mutationMethod = (String) mutMethod.getSelectedItem();
        
        // Asegurarnos de que el board está sincronizado con la UI actual
        updateBoardPreview();
        boardPanel.setBoard(this.board);
        
        // Listener para la GUI
        EvolutionListener listener = (gen, bestGen, bestEver, avg, bestChrObj) -> SwingUtilities.invokeLater(() -> {
            sBestGen.addOrUpdate(gen, bestGen);
            sBestEver.addOrUpdate(gen, bestEver);
            sAvg.addOrUpdate(gen, avg);
            lblBest.setText(String.format("Best: %.6f", bestEver));

            // Aquí podrías enviar el cromosoma al panel si quieres previsualizar las rutas en vivo
            // boardPanel.setChromosome((Chromosome) bestChrObj);
            boardPanel.setBoard(this.board);
        });
        
        new Thread(() -> {
            try {
                // NOTA: GARunner debe estar adaptado para recibir el nuevo Board en lugar del mapa antiguo
                GAResult result = GARunner.run(board, popSize, gens, pc, pm, elit, selectionMethod, crossoverMethod, mutationMethod, num_vans, listener);
                		
                Object bestObj = result.getBest();

                SwingUtilities.invokeLater(() -> {
                    btnRun.setEnabled(true);

                    Chromosome best = (Chromosome) bestObj;

                    clearLog();
                    append("Makespan: " + best.getFitness() + "\n");
                    append("Semilla Mapa: " + spMapSeed.getValue() + " | Semilla Entregas: " + spDeliverySeed.getValue() + "\n");
                    printColoredVanTimes(best, num_vans);
                    printColoredChromosome(best);
                    
                    // Aquí mandaremos las rutas finales para ser dibujadas
                    // this.boardPanel.setChromosome(best);
                    this.boardPanel.startSimulation(board, best, num_vans);
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    btnRun.setEnabled(true);
                    lblBest.setText("Best: error");
                    txt.setText("Error: " + ex);
                    ex.printStackTrace();
                });
            }
        }).start();
    }
    
    // LEFT PANEL (VERTICAL PARAMETERS)
    private JPanel buildLeftPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        container.setPreferredSize(new Dimension(300, 500));

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
        mid.setBorder(BorderFactory.createTitledBorder("Graph & Problem"));

        mid.add(new JLabel("Total Nodos:"));
        mid.add(spNPoints);
        mid.add(new JLabel("Puntos Entrega:"));
        mid.add(spNDeliveries);
        mid.add(new JLabel("Nº Furgonetas:"));
        mid.add(spNVans);
        mid.add(new JLabel("Seed Mapa:"));
        mid.add(spMapSeed);
        mid.add(new JLabel("Seed Entregas:"));
        mid.add(spDeliverySeed);

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

        boardPanel.setPreferredSize(new Dimension(800, 800)); 
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
        plot.getRenderer().setSeriesPaint(0, Color.RED);    // Mejor generación
        plot.getRenderer().setSeriesPaint(1, Color.BLUE);   // Mejor histórico
        plot.getRenderer().setSeriesPaint(2, Color.GREEN);  // Media
    
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
   
    private void clearLog() {
        txt.setText("");
    }

    private void append(String s) {
        try {
            StyledDocument doc = txt.getStyledDocument();
            doc.insertString(doc.getLength(), s, null);
        } catch (BadLocationException ignored) {}
    }

    private void appendColored(String s, Color color, boolean bold) {
        try {
            StyledDocument doc = txt.getStyledDocument();
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setForeground(attrs, color);
            StyleConstants.setBold(attrs, bold);
            doc.insertString(doc.getLength(), s, attrs);
        } catch (BadLocationException ignored) {}
    }
    
    private void printColoredChromosome(Chromosome best) {
        if (best == null || board == null) return;

        // Ahora evaluamos usando la lista de puntos de entrega del Board
        int numDeliveries = board.getDeliveryPoints().size();
        Color[] colors = {Color.GREEN, Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.PINK};

        append("\n--- Best chromosome ---\n");

        int van = 0;
        for (int g : best.getGenes()) {
            // Asumo que tu separador en el cromosoma sigue siendo mayor que el número de entregas (antes cámaras)
            if (g > numDeliveries) {
                appendColored(" | ", Color.DARK_GRAY, true);
                van++;
                continue;
            }

            Color c = colors[Math.min(van, colors.length - 1)];
            appendColored(String.valueOf(g), c, true);
            append(" ");
        }
        append("\n");
    }
    
    private void printColoredVanTimes(Chromosome best, int num_vans) {

        practica.real.Fitness fitnessDbg =
                new practica.real.Fitness(this.board, num_vans);

        practica.real.FitnessBreakdown bd =
                fitnessDbg.evaluateBreakdown(best);

        Color[] colors = {
                Color.GREEN,
                Color.MAGENTA,
                Color.CYAN,
                Color.ORANGE,
                Color.PINK
        };

        double[] vAll = {
                1.5, 1.0, 0.7, 1.2, 0.5,
                1.1, 0.9, 1.3, 0.8, 1.4
        };

        append("\n--- Vans ---\n");

        for (int i = 0; i < bd.times().length; i++) {

            Color c = colors[i % colors.length];

            String part = String.format(
                    "Van %d (x%.1f) -> Time: %.3f",
                    i + 1,
                    vAll[i],
                    bd.times()[i]
            );

            appendColored(part, c, true);
            append("\n");
        }

        append("\n");

        appendColored(
                "Late deliveries: " + bd.lateDeliveries(),
                Color.RED,
                true
        );

        append("\n");

        appendColored(
                "Undelivered packages: " + bd.undeliveredPackages(),
                Color.RED,
                true
        );

        append("\n");
    }
    
}