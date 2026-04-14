package practica.ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.plot.XYPlot;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import practica.real.Board;

public class Graphic extends JFrame {

    // --- COLORES TEMA LUNAR ---
    private final Color BG_DARK = new Color(15, 15, 15);
    private final Color PANEL_DARK = new Color(28, 28, 28);
    private final Color TEXT_LIGHT = new Color(210, 210, 210);
    private final Color ACCENT_CYAN = new Color(0, 255, 255);

    // --- COMPONENTES UI ---
    private final JSpinner spPop = new JSpinner(new SpinnerNumberModel(100, 2, 5000, 10));
    private final JSpinner spGen = new JSpinner(new SpinnerNumberModel(200, 1, 10000, 10));
    private final JSpinner spPc = new JSpinner(new SpinnerNumberModel(0.6, 0.0, 1.0, 0.05));
    private final JSpinner spPm = new JSpinner(new SpinnerNumberModel(0.1, 0.0, 1.0, 0.01));
    private final JSpinner spElit = new JSpinner(new SpinnerNumberModel(0.15, 0.0, 1.0, 0.05));
    
    private final JSpinner spMaxDepth = new JSpinner(new SpinnerNumberModel(5, 2, 20, 1));
    private final JSpinner spBloat = new JSpinner(new SpinnerNumberModel(0.01, 0.0, 1.0, 0.001));
    private final JSpinner spSeed = new JSpinner(new SpinnerNumberModel(3000, 0, Integer.MAX_VALUE, 1));

    private final JComboBox<String> selMethod = new JComboBox<>(new String[]{"ROULETTE", "TOURNAMENT", "RANKING"});
    private final JComboBox<String> crossMethod = new JComboBox<>(new String[]{"SUBTREE", "ONE-POINT"});
    private final JComboBox<String> mutMethod = new JComboBox<>(new String[]{"PUNTUAL", "SUBTREE", "PERMUTATION"});

    private final JButton btnRun = new JButton("RUN EVOLUTION");
    private final JLabel lblBest = new JLabel("Best Fitness: -");
    private final JTextPane txtPhenotype = new JTextPane();

    private final XYSeries sBestGen = new XYSeries("Mejor gen", false, true);
    private final XYSeries sAvg = new XYSeries("Media", false, true);
    
    private Board board;
    private BoardPanel boardPanel;

    public Graphic() {
        super("Lunar Rover GP - Genetic Programming");
        setupTheme();

        this.board = new Board((Integer) spSeed.getValue(), 15, 15);
        this.boardPanel = new BoardPanel(board);

        // Layout Principal: BorderLayout con espaciado
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        getContentPane().setBackground(BG_DARK);

        // 1. PANEL IZQUIERDO: Parámetros (fijo a la izquierda)
        add(buildLeftConfigPanel(), BorderLayout.WEST);

        // 2. PANEL CENTRAL: Gráfica + Fenotipo (reemplazando el Log)
        add(buildCenterAnalysisPanel(), BorderLayout.CENTER);

        // 3. PANEL DERECHO: Board (Mapa)
        add(buildRightBoardPanel(), BorderLayout.EAST);

        btnRun.addActionListener(e -> run());

        setSize(1400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void setupTheme() {
        UIManager.put("Panel.background", PANEL_DARK);
        UIManager.put("Label.foreground", TEXT_LIGHT);
        UIManager.put("ComboBox.background", Color.DARK_GRAY);
        UIManager.put("ComboBox.foreground", Color.WHITE);
        btnRun.setBackground(new Color(35, 60, 60));
        btnRun.setForeground(ACCENT_CYAN);
        btnRun.setFont(new Font("SansSerif", Font.BOLD, 12));
    }

    private JPanel buildLeftConfigPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setPreferredSize(new Dimension(280, 0));

        // GA Parameters
        JPanel pGA = new JPanel(new GridLayout(0, 2, 5, 8));
        pGA.setBorder(createTitledBorder("GA Parameters"));
        pGA.add(new JLabel("Población:")); pGA.add(spPop);
        pGA.add(new JLabel("Generaciones:")); pGA.add(spGen);
        pGA.add(new JLabel("Pc:")); pGA.add(spPc);
        pGA.add(new JLabel("Pm:")); pGA.add(spPm);
        pGA.add(new JLabel("Elitismo:")); pGA.add(spElit);

        // Problem / GP Specific
        JPanel pProb = new JPanel(new GridLayout(0, 2, 5, 8));
        pProb.setBorder(createTitledBorder("Problem / GP"));
        pProb.add(new JLabel("Prof. Max:")); pProb.add(spMaxDepth);
        pProb.add(new JLabel("Bloating:")); pProb.add(spBloat);
        pProb.add(new JLabel("Semilla:")); pProb.add(spSeed);

        // Operators
        JPanel pOps = new JPanel(new GridLayout(0, 1, 5, 5));
        pOps.setBorder(createTitledBorder("Operators"));
        pOps.add(new JLabel("Selección:")); pOps.add(selMethod);
        pOps.add(new JLabel("Cruce:")); pOps.add(crossMethod);
        pOps.add(new JLabel("Mutación:")); pOps.add(mutMethod);

        // Botón Run & Status
        JPanel pBtn = new JPanel(new BorderLayout(5, 5));
        pBtn.setOpaque(false);
        pBtn.add(btnRun, BorderLayout.NORTH);
        lblBest.setHorizontalAlignment(SwingConstants.CENTER);
        pBtn.add(lblBest, BorderLayout.SOUTH);

        container.add(pGA);
        container.add(Box.createVerticalStrut(10));
        container.add(pProb);
        container.add(Box.createVerticalStrut(10));
        container.add(pOps);
        container.add(Box.createVerticalGlue());
        container.add(pBtn);

        return container;
    }

    private JPanel buildCenterAnalysisPanel() {
        JPanel container = new JPanel(new BorderLayout());

        // Parte superior: Gráfica
        ChartPanel chartPanel = buildChart();
        chartPanel.setBorder(createTitledBorder("Evolución del Fitness"));
        
        // Parte inferior: Fenotipo (Donde antes estaba el Log)
        txtPhenotype.setBackground(new Color(10, 12, 12));
        txtPhenotype.setForeground(new Color(150, 255, 150)); // Verde Matrix/Código
        txtPhenotype.setEditable(false);
        txtPhenotype.setFont(new Font("Consolas", Font.PLAIN, 13));
        
        JScrollPane scrollPhenotype = new JScrollPane(txtPhenotype);
        scrollPhenotype.setBorder(createTitledBorder("Best Strategy (Phenotype)"));

        // Split vertical para ajustar tamaños entre gráfica y código
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chartPanel, scrollPhenotype);
        split.setDividerLocation(400);
        split.setResizeWeight(0.5);
        split.setOpaque(false);
        split.setBorder(null);

        container.add(split, BorderLayout.CENTER);
        return container;
    }

    private JPanel buildRightBoardPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(createTitledBorder("Board Explorer"));
        container.setPreferredSize(new Dimension(600, 0));
        
        // El BoardPanel ocupará el centro
        container.add(boardPanel, BorderLayout.CENTER);
        return container;
    }

    private ChartPanel buildChart() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(sBestGen);
        dataset.addSeries(sAvg);

        JFreeChart chart = ChartFactory.createXYLineChart(null, "Generación", "Fitness", dataset, PlotOrientation.VERTICAL, true, true, false);
        styleChart(chart);
        return new ChartPanel(chart);
    }

    private void styleChart(JFreeChart chart) {
        chart.setBackgroundPaint(PANEL_DARK);
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(BG_DARK);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.getRenderer().setSeriesPaint(0, Color.RED);
        plot.getRenderer().setSeriesPaint(1, new Color(0, 200, 0));
        chart.getLegend().setBackgroundPaint(PANEL_DARK);
        chart.getLegend().setItemPaint(TEXT_LIGHT);
    }

    private TitledBorder createTitledBorder(String title) {
        TitledBorder tb = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY), title);
        tb.setTitleColor(ACCENT_CYAN);
        tb.setTitleFont(new Font("SansSerif", Font.BOLD, 11));
        return tb;
    }

    private void run() {
        // Simulación de inicio
        sBestGen.clear();
        sAvg.clear();
        txtPhenotype.setText("// Analizando población...\n// Generando AST...");
        lblBest.setText("Best Fitness: calculando...");
    }
}