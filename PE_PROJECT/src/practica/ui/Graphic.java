package practica.ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.plot.XYPlot;
import java.awt.BasicStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

import practica.GARunner;
import practica.real.Board;
import practica.real.Chromosome;
import practica.real.Fitness;
import practica.real.FitnessBreakdown;

public class Graphic extends JFrame {
    
    // ELEMENTOS VISUALES EXISTENTES
    private final JSpinner spPop = new JSpinner(new SpinnerNumberModel(100, 2, 5000, 10));
    private final JSpinner spGen = new JSpinner(new SpinnerNumberModel(200, 1, 100000, 10));
    private final JSpinner spPc  = new JSpinner(new SpinnerNumberModel(0.60, 0.0, 1.0, 0.01));
    private final JSpinner spPm  = new JSpinner(new SpinnerNumberModel(0.1, 0.0, 1.0, 0.001));
    private final JSpinner spNPoints = new JSpinner(new SpinnerNumberModel(100, 5, 500, 1));
    private final JSpinner spNDeliveries = new JSpinner(new SpinnerNumberModel(30, 1, 100, 1));
    private final JSpinner spMapSeed = new JSpinner(new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1));
    private final JSpinner spDeliverySeed = new JSpinner(new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1));
    private final JSpinner spNVans  = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));

    private final JComboBox<String> selMethod = new JComboBox<>(new String[]{"ROULETTE", "TOURNAMENT", "STOCHASTIC", "TRUNCATION", "REMAINDERS", "RANKING"});
    private final JComboBox<String> crossMethod = new JComboBox<>(new String[]{"PMX", "OX", "OXPP", "CX", "CO", "ERX"});
    private final JComboBox<String> mutMethod = new JComboBox<>(new String[]{"INSERTION", "SWAP", "INVERSION", "HEURISTIC", "BALANCE_MOVE"});
    private final JSpinner spElit = new JSpinner(new SpinnerNumberModel(0.15, 0.0, 1.0, 0.05));

    // NUEVOS ELEMENTOS DE CONTROL
    private final JButton btnRun = new JButton("Ejecutar Evolución");
    private final JButton btnSimulate = new JButton("Ejecutar Simulación");
    private final JSlider slSpeed = new JSlider(1, 20, 10); // Escala de velocidad
    
    private final JLabel lblBest = new JLabel("Mejor: -");
    private final JTextPane txt = new JTextPane();

    private final XYSeries sBestGen = new XYSeries("Mejor gen (rojo)", false, true);
    private final XYSeries sBestEver = new XYSeries("Mejor histórico (azul)", false, true);
    private final XYSeries sAvg = new XYSeries("Media (verde)", false, true);
    
    private BoardPanel boardPanel;
    private Board board;

    // Memoria para re-simular
    private Chromosome lastBestChromosome;
    private int lastNumVans;
    
    public Graphic() {
        super("Optimización para rutas logísticas");
        
        updateBoardPreview();
        boardPanel = new BoardPanel(board);

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

        // Listeners de botones
        btnRun.addActionListener(e -> run());
        
        btnSimulate.setEnabled(false);
        btnSimulate.addActionListener(e -> {
            if (lastBestChromosome != null) {
                boardPanel.startSimulation(board, lastBestChromosome, lastNumVans);
            }
        });

        slSpeed.addChangeListener(e -> {
            double multiplier = slSpeed.getValue() / 5.0; // 5 es el valor neutral (1.0x)
            boardPanel.setSimulationSpeed(multiplier);
        });

        setSize(1300, 800);
        setLocationRelativeTo(null);
    }
    
    private void updateBoardPreview() {
        int nPoints = (Integer) spNPoints.getValue();
        int nDeliveries = (Integer) spNDeliveries.getValue();
        long mapSeed = ((Integer) spMapSeed.getValue()).longValue();
        long delivSeed = ((Integer) spDeliverySeed.getValue()).longValue();
        
        if (nDeliveries > nPoints) {
            spNDeliveries.setValue(nPoints);
            nDeliveries = nPoints;
        }
        this.board = new Board(nPoints, nDeliveries, mapSeed, delivSeed);
    }
    
    private void run() {
        btnRun.setEnabled(false);
        btnSimulate.setEnabled(false);
        txt.setText("");
        lblBest.setText("Mejor: ejecutando...");

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
        
        updateBoardPreview();
        boardPanel.setBoard(this.board);
        
        EvolutionListener listener = (gen, bestGen, bestEver, avg, bestChrObj) -> SwingUtilities.invokeLater(() -> {
            sBestGen.addOrUpdate(gen, bestGen);
            sBestEver.addOrUpdate(gen, bestEver);
            sAvg.addOrUpdate(gen, avg);
            lblBest.setText(String.format("Mejor: %.6f", bestEver));
        });
        
        new Thread(() -> {
            try {
                GAResult result = GARunner.run(board, popSize, gens, pc, pm, elit, selectionMethod, crossoverMethod, mutationMethod, num_vans, listener);
                
                SwingUtilities.invokeLater(() -> {
                    btnRun.setEnabled(true);
                    btnSimulate.setEnabled(true);

                    lastBestChromosome = (Chromosome) result.getBest();
                    lastNumVans = num_vans;

                    clearLog();
                    append("Makespan: " + lastBestChromosome.getFitness() + "\n");
                    printColoredVanTimes(lastBestChromosome, num_vans);
                    printColoredChromosome(lastBestChromosome);
                    
                    this.boardPanel.startSimulation(board, lastBestChromosome, num_vans);
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    btnRun.setEnabled(true);
                    lblBest.setText("Best: error");
                    ex.printStackTrace();
                });
            }
        }).start();
    }
    
    private JPanel buildLeftPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        container.setPreferredSize(new Dimension(300, 500));

        container.add(buildTopPanel());

        // Panel de Controles de Simulación
        JPanel simPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        simPanel.setBorder(BorderFactory.createTitledBorder("Simulación"));
        simPanel.add(new JLabel("Velocidad:"));
        slSpeed.setMinorTickSpacing(1);
        slSpeed.setPaintTicks(true);
        simPanel.add(slSpeed);
        simPanel.add(btnSimulate);

        JPanel actions = new JPanel(new FlowLayout());
        actions.add(btnRun);
        actions.add(lblBest);

        container.add(Box.createVerticalStrut(10));
        container.add(simPanel);
        container.add(Box.createVerticalStrut(10));
        container.add(actions);

        return container;
    }

    private JPanel buildTopPanel() {
        JPanel left = new JPanel(new GridLayout(0,2,8,6));
        left.setBorder(BorderFactory.createTitledBorder("Parámetros"));
        left.add(new JLabel("Población:")); left.add(spPop);
        left.add(new JLabel("Generaciones:")); left.add(spGen);
        left.add(new JLabel("Pc:")); left.add(spPc);
        left.add(new JLabel("Pm:")); left.add(spPm);
        left.add(new JLabel("Elitismo:")); left.add(spElit);

        JPanel mid = new JPanel(new GridLayout(0,2,8,6));
        mid.setBorder(BorderFactory.createTitledBorder("Ajustes del escenario"));
        mid.add(new JLabel("Total Nodos:")); mid.add(spNPoints);
        mid.add(new JLabel("Puntos Entrega:")); mid.add(spNDeliveries);
        mid.add(new JLabel("Nº Furgonetas:")); mid.add(spNVans);
        mid.add(new JLabel("Semilla Mapa:")); mid.add(spMapSeed);
        mid.add(new JLabel("Semilla Entregas:")); mid.add(spDeliverySeed);

        JPanel right = new JPanel(new GridLayout(0,2,8,6));
        right.setBorder(BorderFactory.createTitledBorder("Operadores"));
        right.add(new JLabel("Selección:")); right.add(selMethod);
        right.add(new JLabel("Cruce:")); right.add(crossMethod);
        right.add(new JLabel("Mutación:")); right.add(mutMethod);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(left); panel.add(mid); panel.add(right);
        return panel;
    }

    private JPanel buildRightMainPanel() {
        JPanel main = new JPanel(new BorderLayout(10,10));
        main.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel leftSide = new JPanel();
        leftSide.setLayout(new BoxLayout(leftSide, BoxLayout.Y_AXIS));
        leftSide.setPreferredSize(new Dimension(400, 800));

        ChartPanel chart = buildCenterChart();
        chart.setPreferredSize(new Dimension(400, 300));
        chart.setBorder(BorderFactory.createTitledBorder("Evolución"));

        txt.setEditable(false);
        txt.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane log = new JScrollPane(txt);
        log.setPreferredSize(new Dimension(400, 150));
        log.setBorder(BorderFactory.createTitledBorder("Mejor Solución"));

        leftSide.add(chart);
        leftSide.add(Box.createVerticalStrut(10));
        leftSide.add(log);

        JPanel boardContainer = new JPanel(new BorderLayout());
        boardContainer.setBorder(BorderFactory.createTitledBorder("Mapa"));
        boardPanel.setPreferredSize(new Dimension(800, 800)); 
        boardContainer.add(boardPanel, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSide, boardContainer);
        split.setResizeWeight(0.3);
        split.setDividerLocation(400);

        main.add(split, BorderLayout.CENTER);
        return main;
    }

    private ChartPanel buildCenterChart() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(sBestGen); dataset.addSeries(sBestEver); dataset.addSeries(sAvg);

        JFreeChart chart = ChartFactory.createXYLineChart("Evolución", "Generación", "Fitness", dataset, PlotOrientation.VERTICAL, true, true, false);
        XYPlot plot = chart.getXYPlot();
        plot.getRenderer().setSeriesPaint(0, Color.RED);
        plot.getRenderer().setSeriesPaint(1, Color.BLUE);
        plot.getRenderer().setSeriesPaint(2, Color.GREEN);
        plot.setBackgroundPaint(Color.WHITE);
        return new ChartPanel(chart);
    }

    private void clearLog() { txt.setText(""); }

    private void append(String s) {
        try {
            txt.getStyledDocument().insertString(txt.getStyledDocument().getLength(), s, null);
        } catch (BadLocationException ignored) {}
    }

    private void appendColored(String s, Color color, boolean bold) {
        try {
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setForeground(attrs, color);
            StyleConstants.setBold(attrs, bold);
            txt.getStyledDocument().insertString(txt.getStyledDocument().getLength(), s, attrs);
        } catch (BadLocationException ignored) {}
    }

    private void printColoredChromosome(Chromosome best) {
        if (best == null || board == null) return;
        int numDeliveries = board.getDeliveryPoints().size();
        Color[] colors = {Color.GREEN, Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.PINK};
        append("\n--- Best chromosome ---\n");
        int van = 0;
        for (int g : best.getGenes()) {
            if (g > numDeliveries) {
                appendColored(" | ", Color.DARK_GRAY, true);
                van++; continue;
            }
            Color c = colors[Math.min(van, colors.length - 1)];
            appendColored(String.valueOf(g), c, true);
            append(" ");
        }
        append("\n");
    }

    private void printColoredVanTimes(Chromosome best, int num_vans) {
        Fitness fitnessDbg = new Fitness(this.board, num_vans);
        FitnessBreakdown bd = fitnessDbg.evaluateBreakdown(best);
        Color[] colors = {Color.GREEN, Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.PINK};
        double[] vAll = {1.5, 1.0, 0.7, 1.2, 0.5, 1.1, 0.9, 1.3, 0.8, 1.4};

        append("\n--- Vans ---\n");
        for (int i = 0; i < bd.times().length; i++) {
            Color c = colors[i % colors.length];
            String part = String.format("Van %d (x%.1f) -> Time: %.3f", i + 1, vAll[i], bd.times()[i]);
            appendColored(part, c, true);
            append("\n");
        }
        append("\n");
        appendColored("Late deliveries: " + bd.lateDeliveries(), Color.RED, true);
        append("\n");
        appendColored("Undelivered packages: " + bd.undeliveredPackages(), Color.RED, true);
        append("\n");
    }
}