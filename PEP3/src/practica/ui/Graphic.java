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
import practica.ast.NodoAST;
import practica.ast.NodoAccion;
import practica.ast.NodoBloque;
import practica.ast.NodoCondicional;
import practica.real.Contexto;
import practica.real.Chromosome;
import practica.real.Pair;
import practica.enums.Sensor;
import practica.enums.SelectionMethod;

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

    // NUEVO: Combo box para el método de selección
    private final JComboBox<String> selMethod = new JComboBox<>(new String[]{"ROULETTE", "TOURNAMENT", "STOCHASTIC", "TRUNCATION", "REMAINDERS", "RANKING"});
    private final JComboBox<String> mutMethod = new JComboBox<>(new String[]{"SUBARBOL", "FUNCIONAL", "TERMINAL", "HOIST", "ALEATORIA"});
    
    private final JButton btnGenMap = new JButton("GENERAR MAPA");
    private final JButton btnEvolve = new JButton("EVOLUCIONAR"); 
    private final JButton btnRunSim = new JButton("EJECUTAR SIMULACIÓN");
    private final JLabel lblBest = new JLabel("Best Fitness: -");
    private final JTextPane txtPhenotype = new JTextPane();

    private final XYSeries sBestGen = new XYSeries("Mejor Gen", false, true);
    private final XYSeries sAvg = new XYSeries("Media", false, true);
    
    private Contexto c1, c2, c3;
    private ContextPanel contextPanel;
    private Timer animationTimer;
    
    private Chromosome bestChromosome = null;

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
        btnEvolve.addActionListener(e -> runEvolution());
        btnRunSim.addActionListener(e -> runSimulation());

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
        btnRunSim.setBackground(new Color(60, 35, 60)); 
        btnRunSim.setForeground(new Color(255, 100, 255));
        
        btnEvolve.setEnabled(false);
        btnRunSim.setEnabled(false);
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

        // MODIFICADO: Dos columnas para organizar mejor los operadores
        JPanel pOps = new JPanel(new GridLayout(0, 2, 5, 5));
        pOps.setBorder(createTitledBorder("Operadores"));
        pOps.add(new JLabel("Selección:")); pOps.add(selMethod); // Añadido selector
        pOps.add(new JLabel("Mutación:")); pOps.add(mutMethod);

        JPanel pBtn = new JPanel(new GridLayout(4, 1, 5, 5)); 
        pBtn.setOpaque(false);
        pBtn.add(btnGenMap);
        pBtn.add(btnEvolve);
        pBtn.add(btnRunSim);
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
    
    private String formatAST(NodoAST nodo, int nivel) {
        String indent = "  ".repeat(nivel);

        // --- CASO 1: BLOQUE DE CÓDIGO ---
        if (nodo instanceof NodoBloque bloque) {
            StringBuilder sb = new StringBuilder();
            sb.append(indent).append("{\n"); 

            for (NodoAST hijo : bloque.hijos) {
                sb.append(formatAST(hijo, nivel + 1));
            }

            sb.append(indent).append("}\n");
            return sb.toString();
        }

        // --- CASO 2: CONDICIONAL (IF-ELSE) ---
        if (nodo instanceof NodoCondicional cond) {
            StringBuilder sb = new StringBuilder();
            
            // Cabecera del IF (indented)
            sb.append(indent).append("IF ( ")
              .append(cond.sensor).append(" > ").append(cond.umbral)
              .append(" )\n");

            sb.append(formatAST(cond.getHijoIzquierdo(), nivel));

            sb.append(indent).append("ELSE\n");

            sb.append(formatAST(cond.getHijoDerecho(), nivel));

            return sb.toString();
        }

        // --- CASO 3: ACCIÓN SIMPLE (HOJA) ---
        if (nodo instanceof NodoAccion acc) {
            return indent + acc.getAccion() + "();\n";
        }

        return "";
    }

    private void generateMaps() {
        if (animationTimer != null) animationTimer.stop(); 
        
        int seed = (Integer) spSeed.getValue();
        this.c1 = new Contexto(seed, 15, 15);
        this.c2 = new Contexto(seed + 1, 15, 15);
        this.c3 = new Contexto(seed + 2, 15, 15);
        contextPanel.setContext(this.c1);
        contextPanel.updateTrail(new ArrayList<>());
        
        btnEvolve.setEnabled(true);
        btnRunSim.setEnabled(false);
        bestChromosome = null; 
    }

    private void runEvolution() {
        int seed = (Integer) spSeed.getValue();
        this.c1 = new Contexto(seed, 15, 15);
        this.c2 = new Contexto(seed + 1, 15, 15);
        this.c3 = new Contexto(seed + 2, 15, 15);

        if (animationTimer != null) animationTimer.stop();

        sBestGen.clear(); sAvg.clear();
        btnEvolve.setEnabled(false);
        btnRunSim.setEnabled(false);
        
        new Thread(() -> {
            GAResult result = GARunner.run(
                c1, c2, c3, (Integer)spPop.getValue(), (Integer)spGen.getValue(), 
                (Double)spPc.getValue(), (Double)spPm.getValue(), (Double)spElit.getValue(), 
                (String)selMethod.getSelectedItem(), // NUEVO: Pasamos el método de selección
                (String)mutMethod.getSelectedItem(), (Integer)spMaxDepth.getValue(), (Double)spBloat.getValue(),
                (gen, bGen, bEver, avg, bestChr) -> {
                    SwingUtilities.invokeLater(() -> {
                        sBestGen.addOrUpdate(gen, bGen);
                        sAvg.addOrUpdate(gen, avg);
                        lblBest.setText(String.format("Mejor: %.2f", bEver));
                    });
                }
            );

            bestChromosome = (Chromosome) result.getBest();
            
            SwingUtilities.invokeLater(() -> {
            	String ast = formatAST(bestChromosome.getTree(), 0);
            	double fitness = bestChromosome.getFitness();

            	int tamanoAST = bestChromosome.getTree().tam();
            	
            	txtPhenotype.setText(
            		"FITNESS: " + fitness + "\n" +
            		"TAMAÑO AST: " + tamanoAST + " nodos\n\n" +
            	    ast
            	);
                btnEvolve.setEnabled(true);
                btnRunSim.setEnabled(true); 
            });
        }).start();
    }

    private void runSimulation() {
        if (bestChromosome == null) return;
        if (animationTimer != null) animationTimer.stop();
        
        startSimulation(bestChromosome);
    }

    private void startSimulation(Chromosome best) {
        Contexto simCtx = new Contexto((Integer) spSeed.getValue(), 15, 15);
        contextPanel.setContext(simCtx);
        List<Pair> trail = new ArrayList<>();
        trail.add(new Pair(simCtx.getCoordenadas().x(), simCtx.getCoordenadas().y()));
        
        animationTimer = new Timer(400, e -> {
            if (!simCtx.estaVivo() || simCtx.getTicks() >= 150) {
                ((Timer)e.getSource()).stop();

                JOptionPane.showMessageDialog(
                    this,
                    "Simulación finalizada",
                    "Fin",
                    JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            simCtx.setAccionTomada(false);

            best.getTree().ejecutar(simCtx);
            trail.add(new Pair(simCtx.getCoordenadas().x(), simCtx.getCoordenadas().y()));

            contextPanel.updateTrail(trail);
            contextPanel.repaint();
        });
        animationTimer.start();
    }
}