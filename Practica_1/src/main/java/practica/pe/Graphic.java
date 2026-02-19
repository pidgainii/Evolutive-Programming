package practica.pe;

import org.jfree.chart.*;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.*;
import javax.swing.*;
import java.awt.*;

public class Graphic extends JFrame {

    private XYSeries mejorGen = new XYSeries("Mejor generación");
    private XYSeries mejorHistorico = new XYSeries("Mejor histórico");
    private XYSeries media = new XYSeries("Media");

    private int bestEver = Integer.MIN_VALUE;

    private Evolution evolute;
    private BoardPanel panel;

    // ----- Inputs -----

    private int populationSize;
    private int boardSizeX;
    private int boardSizeY;
    private int nCameras;
    private int cameraRange;
    private int[][] board;
    private int crossingProb;
    private int mutationProb;
    private int elitePerc;
    private int nGenerations;

    private String selectionMethod;
    private String crossoverMethod;
    private String mutationMethod;


    public Graphic(Evolution evolution, int boardSizeX, int boardSizeY,
                   int nCameras, int cameraRange, int[][] board) {
        this.evolute = evolution;
        this.boardSizeX = boardSizeX;
        this.boardSizeY = boardSizeY;
        this.nCameras = nCameras;
        this.cameraRange = cameraRange;
        this.board = board;


        // ---------------- GRÁFICA ----------------
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(mejorGen);
        dataset.addSeries(mejorHistorico);
        dataset.addSeries(media);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Evolution of Genetic Algorithm",
                "Generation",
                "Fitness",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        // ----- ESTILO GENERAL -----
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 18));

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(new Color(220, 220, 220));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setOutlineVisible(false);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesStroke(0, new BasicStroke(3f));
        renderer.setSeriesStroke(1, new BasicStroke(3f));
        renderer.setSeriesStroke(2, new BasicStroke(3f));
        renderer.setSeriesPaint(0, new Color(220, 50, 47));
        renderer.setSeriesPaint(1, new Color(38, 139, 210));
        renderer.setSeriesPaint(2, new Color(133, 153, 0));
        plot.setRenderer(renderer);

        Font axisFont = new Font("SansSerif", Font.PLAIN, 12);
        plot.getDomainAxis().setLabelFont(new Font("SansSerif", Font.BOLD, 14));
        plot.getRangeAxis().setLabelFont(new Font("SansSerif", Font.BOLD, 14));
        plot.getDomainAxis().setTickLabelFont(axisFont);
        plot.getRangeAxis().setTickLabelFont(axisFont);
        plot.setAxisOffset(new RectangleInsets(10, 10, 10, 10));

        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.getLegend().setItemFont(new Font("SansSerif", Font.PLAIN, 20));
        chart.setAntiAlias(true);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setFillZoomRectangle(true);
        chartPanel.setBackground(Color.WHITE);

        // --- PANEL DERECHO: Configuración + Tablero ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(260, 600));

        // Panel de configuración
        JPanel configPanel = createInfoPanel();

        this.panel = new BoardPanel(this.board);

        // JSplitPane vertical
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, configPanel, panel);
        splitPane.setDividerLocation(300);   // altura inicial del panel de configuración
        splitPane.setResizeWeight(0);        // la parte superior mantiene tamaño fijo al redimensionar
        splitPane.setDividerSize(4);

        rightPanel.add(splitPane, BorderLayout.CENTER);

        // AGREGAR A LA VENTANA
        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        // ---------------- CONFIGURACIÓN FINAL VENTANA ----------------
        setSize(900, 600);
        setTitle("Genetic Algorithm Progress");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void resetGraph() {
        SwingUtilities.invokeLater(() -> {
            mejorGen.clear();
            mejorHistorico.clear();
            media.clear();
            bestEver = Integer.MIN_VALUE;
        });
    }




    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(245, 245, 245));

        // --- Inputs ---
        JSpinner spPoblacion = new JSpinner(new SpinnerNumberModel(100, 2, 1000, 1));
        JSpinner spGeneraciones = new JSpinner(new SpinnerNumberModel(200, 1, 10000, 1));
        JSpinner spCruce = new JSpinner(new SpinnerNumberModel(60, 0, 100, 1));
        JSpinner spMutacion = new JSpinner(new SpinnerNumberModel(5, 0, 100, 1));
        JSpinner spElitismo = new JSpinner(new SpinnerNumberModel(2, 0, 100, 1));
        JComboBox<String> cbSeleccion = new JComboBox<>(new String[]{"Torneo", "Ruleta", "Ranking"});
        JComboBox<String> cbCruce = new JComboBox<>(new String[]{"1 Punto", "2 Puntos", "Uniforme", "PMX"});
        JComboBox<String> cbMutacion = new JComboBox<>(new String[]{"Intercambio", "Inserción", "Inversión"});

        panel.add(new JLabel("Población:")); panel.add(spPoblacion);
        panel.add(new JLabel("Generaciones:")); panel.add(spGeneraciones);
        panel.add(new JLabel("% Cruce:")); panel.add(spCruce);
        panel.add(new JLabel("% Mutación:")); panel.add(spMutacion);
        panel.add(new JLabel("Elitismo:")); panel.add(spElitismo);
        panel.add(new JLabel("Selección:")); panel.add(cbSeleccion);
        panel.add(new JLabel("Tipo Cruce:")); panel.add(cbCruce);
        panel.add(new JLabel("Tipo Mutación:")); panel.add(cbMutacion);

        // --- Run Button ---
        JButton btnRun = new JButton("Run");
        panel.add(btnRun);
        panel.add(new JLabel("")); // empty cell

        // --- Button Action ---
        btnRun.addActionListener(e -> {
            resetGraph();
            // Update variables in Graphic
            this.populationSize = (int) spPoblacion.getValue();
            this.nGenerations = (int) spGeneraciones.getValue();
            this.crossingProb = (int) spCruce.getValue();
            this.mutationProb = (int) spMutacion.getValue();
            this.elitePerc = (int) spElitismo.getValue();
            this.selectionMethod = (String) cbSeleccion.getSelectedItem();
            this.crossoverMethod = (String) cbCruce.getSelectedItem();
            this.mutationMethod = (String) cbMutacion.getSelectedItem();

            // Trigger Evolution
            if (this.evolute != null) {
                this.evolute.setParameters(this.populationSize, this.boardSizeX,
                        this.boardSizeY, this.nCameras, this.cameraRange,
                        this.board, this.crossingProb,
                        this.mutationProb, this.elitePerc, this.nGenerations,
                        this.selectionMethod);
                new Thread(() -> this.evolute.evolute(this, this.panel)).start(); // run in a separate thread
            }
        });

        return panel;
    }




    public void newGeneration(int generacion, int[] fitness) {
        int suma = 0;
        int best = Integer.MIN_VALUE;

        for (int f : fitness) {
            suma += f;
            if (f > best) best = f;
        }

        double mediaGen = (double)suma / fitness.length;

        if (best > bestEver)
            bestEver = best;


        final int fBest = best;
        final int fBestEver = bestEver;
        final double fMedia = mediaGen;

        SwingUtilities.invokeLater(() -> {
            mejorGen.add(generacion, fBest);
            mejorHistorico.add(generacion, fBestEver);
            media.add(generacion, fMedia);
        });
    }

}
