package practica.ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import practica.GARunner;
import practica.real.Contexto;
import practica.real.Chromosome;
import practica.real.Pair;
import practica.enums.Sensor;

public class Graphic extends JFrame {

    private final Color BG_DARK = new Color(15, 15, 15);
    private final Color PANEL_DARK = new Color(28, 28, 28);
    private final Color TEXT_LIGHT = new Color(210, 210, 210);
    private final Color ACCENT_CYAN = new Color(0, 255, 255);

    private final JSpinner spPop = new JSpinner(new SpinnerNumberModel(300, 2, 5000, 10));
    private final JSpinner spGen = new JSpinner(new SpinnerNumberModel(300, 1, 10000, 10));
    private final JSpinner spPc = new JSpinner(new SpinnerNumberModel(0.6, 0.0, 1.0, 0.05));
    private final JSpinner spPm = new JSpinner(new SpinnerNumberModel(0.1, 0.0, 1.0, 0.01));
    private final JSpinner spElit = new JSpinner(new SpinnerNumberModel(0.15, 0.0, 1.0, 0.05));
    private final JSpinner spMaxDepth = new JSpinner(new SpinnerNumberModel(5, 2, 20, 1));
    private final JSpinner spBloat = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 5.0, 0.1));
    private final JSpinner spSeed = new JSpinner(new SpinnerNumberModel(3000, 0, Integer.MAX_VALUE, 1));

    private final JComboBox<String> mutMethod = new JComboBox<>(new String[]{"ALEATORIA", "SUBTREE", "PUNTUAL", "HOIST"});
    private final JButton btnGenMap = new JButton("GENERAR MAPA");
    private final JButton btnEvolve = new JButton("EVOLUCIONAR Y EJECUTAR");
    private final JLabel lblBest = new JLabel("Best Fitness: -");
    private final JTextPane txtPhenotype = new JTextPane();

    private final XYSeries sBestGen = new XYSeries("Mejor Gen", false, true);
    private final XYSeries sAvg = new XYSeries("Media", false, true);
    
    private Contexto c1, c2, c3;
    private ContextPanel contextPanel;
    private Timer animationTimer;

    public Graphic() {
        super("Lunar Rover GP - Progra Evolutiva 25/26");
        setupTheme();
        this.contextPanel = new ContextPanel(null);

        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        getContentPane().setBackground(BG_DARK);

        add(buildLeftConfigPanel(), BorderLayout.WEST);
        add(buildCenterAnalysisPanel(), BorderLayout.CENTER);
        add(buildRightContextPanel(), BorderLayout.EAST);

        btnGenMap.addActionListener(e -> generateMaps());
        btnEvolve.addActionListener(e -> runEvolutionAndExecution());

        setSize(1400, 850);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void setupTheme() {
        UIManager.put("Panel.background", PANEL_DARK);
        UIManager.put("Label.foreground", TEXT_LIGHT);
        btnGenMap.setBackground(new Color(60, 60, 35));
        btnGenMap.setForeground(Color.YELLOW);
        btnEvolve.setBackground(new Color(35, 60, 60));
        btnEvolve.setForeground(ACCENT_CYAN);
    }

    private JPanel buildLeftConfigPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setPreferredSize(new Dimension(280, 0));

        JPanel pGA = new JPanel(new GridLayout(0, 2, 5, 8));
        pGA.setBorder(createTitledBorder("Parámetros Algoritmo"));
        pGA.add(new JLabel("Población:")); pGA.add(spPop);
        pGA.add(new JLabel("Generaciones:")); pGA.add(spGen);
        pGA.add(new JLabel("Pc (Cruce):")); pGA.add(spPc);
        pGA.add(new JLabel("Pm (Mut):")); pGA.add(spPm);
        pGA.add(new JLabel("Elitismo:")); pGA.add(spElit);

        JPanel pProb = new JPanel(new GridLayout(0, 2, 5, 8));
        pProb.setBorder(createTitledBorder("Configuración GP"));
        pProb.add(new JLabel("Prof. Max:")); pProb.add(spMaxDepth);
        pProb.add(new JLabel("Bloating:")); pProb.add(spBloat);
        pProb.add(new JLabel("Semilla:")); pProb.add(spSeed);

        JPanel pOps = new JPanel(new GridLayout(0, 1, 5, 5));
        pOps.setBorder(createTitledBorder("Operadores"));
        pOps.add(new JLabel("Estrategia Mutación:")); pOps.add(mutMethod);

        JPanel pBtn = new JPanel(new GridLayout(3, 1, 5, 5));
        pBtn.setOpaque(false);
        pBtn.add(btnGenMap);
        pBtn.add(btnEvolve);
        lblBest.setHorizontalAlignment(SwingConstants.CENTER);
        pBtn.add(lblBest);

        container.add(pGA); container.add(Box.createVerticalStrut(10));
        container.add(pProb); container.add(Box.createVerticalStrut(10));
        container.add(pOps); container.add(Box.createVerticalGlue());
        container.add(pBtn);
        return container;
    }

    private JPanel buildCenterAnalysisPanel() {
        JPanel container = new JPanel(new BorderLayout());
        ChartPanel chartPanel = new ChartPanel(buildChart());
        chartPanel.setBorder(createTitledBorder("Evolución del Fitness"));
        
        txtPhenotype.setBackground(new Color(10, 12, 12));
        txtPhenotype.setForeground(new Color(150, 255, 150)); 
        txtPhenotype.setEditable(false);
        txtPhenotype.setFont(new Font("Consolas", Font.PLAIN, 12));
        
        JScrollPane scroll = new JScrollPane(txtPhenotype);
        scroll.setBorder(createTitledBorder("Mejor Programa (Fenotipo AST)"));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chartPanel, scroll);
        split.setDividerLocation(400);
        container.add(split, BorderLayout.CENTER);
        return container;
    }

    private JPanel buildRightContextPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(createTitledBorder("Simulación en Tiempo Real"));
        container.setPreferredSize(new Dimension(500, 0));
        container.add(contextPanel, BorderLayout.CENTER);
        return container;
    }

    private JFreeChart buildChart() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(sBestGen);
        dataset.addSeries(sAvg);
        JFreeChart chart = ChartFactory.createXYLineChart(null, "Generación", "Fitness", dataset, PlotOrientation.VERTICAL, true, true, false);
        chart.setBackgroundPaint(PANEL_DARK);
        chart.getXYPlot().setBackgroundPaint(BG_DARK);
        return chart;
    }

    private TitledBorder createTitledBorder(String title) {
        TitledBorder tb = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY), title);
        tb.setTitleColor(ACCENT_CYAN);
        return tb;
    }

    private void generateMaps() {
        int seed = (Integer) spSeed.getValue();
        this.c1 = new Contexto(seed, 15, 15);
        this.c2 = new Contexto(seed + 1, 15, 15);
        this.c3 = new Contexto(seed + 2, 15, 15);
        contextPanel.setContext(this.c1);
        contextPanel.updateTrail(new ArrayList<>());
        btnEvolve.setEnabled(true);
    }

    private void runEvolutionAndExecution() {
        if (c1 == null) return;
        if (animationTimer != null) animationTimer.stop();

        sBestGen.clear(); sAvg.clear();
        btnEvolve.setEnabled(false);
        
        new Thread(() -> {
            GAResult result = GARunner.run(
                c1, c2, c3, (Integer)spPop.getValue(), (Integer)spGen.getValue(), 
                (Double)spPc.getValue(), (Double)spPm.getValue(), (Double)spElit.getValue(), 
                (String)mutMethod.getSelectedItem(), (Integer)spMaxDepth.getValue(), (Double)spBloat.getValue(),
                (gen, bGen, bEver, avg, bestChr) -> {
                    SwingUtilities.invokeLater(() -> {
                        sBestGen.addOrUpdate(gen, bGen);
                        sAvg.addOrUpdate(gen, avg);
                        lblBest.setText(String.format("Mejor: %.2f", bEver));
                    });
                }
            );

            Chromosome winner = (Chromosome) result.getBest();
            SwingUtilities.invokeLater(() -> {
                txtPhenotype.setText(winner.getTree().toString());
                startSimulation(winner);
                btnEvolve.setEnabled(true);
            });
        }).start();
    }

    private void startSimulation(Chromosome best) {
        // Reset a fresh context for visual display
        Contexto simCtx = new Contexto((Integer) spSeed.getValue(), 15, 15);
        contextPanel.setContext(simCtx);
        List<Pair> trail = new ArrayList<>();
        
        animationTimer = new Timer(100, e -> {
            if (simCtx.leerSensor(Sensor.NIVEL_ENERGIA) <= 0 || simCtx.getTicks() >= 150) {
                ((Timer)e.getSource()).stop();
                return;
            }
            
            // Execute one step of the AST
            best.getTree().ejecutar(simCtx);
            trail.add(new Pair(simCtx.getCoordenadas().x(), simCtx.getCoordenadas().y()));
            
            contextPanel.updateTrail(trail);
            contextPanel.repaint();
        });
        animationTimer.start();
    }
}