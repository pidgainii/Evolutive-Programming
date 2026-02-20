package practica.pe.ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Graphic extends JFrame {

    // parámetros mínimos del enunciado
    private final JComboBox<String> problemType = new JComboBox<>(new String[]{"BINARY", "REAL"});
    private final JSpinner spPop = new JSpinner(new SpinnerNumberModel(100, 2, 5000, 10));
    private final JSpinner spGen = new JSpinner(new SpinnerNumberModel(200, 1, 100000, 10));
    private final JSpinner spPc  = new JSpinner(new SpinnerNumberModel(0.60, 0.0, 1.0, 0.01));
    private final JSpinner spPm  = new JSpinner(new SpinnerNumberModel(0.05, 0.0, 1.0, 0.001));

    private final JComboBox<String> selMethod = new JComboBox<>(new String[]{"ROULETTE", "TOURNAMENT", "STOCHASTIC", "TRUNCATION", "REMAINDERS"});
    private final JComboBox<String> crossMethod = new JComboBox<>(new String[]{"ONE_POINT", "UNIFORM", "ARITHMETIC", "BLX_ALPHA"});
    private final JComboBox<String> mutMethod = new JComboBox<>(new String[]{"GAUSSIAN", "GENE"});
    private final JSpinner spElit = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.05));
    private final JCheckBox cbWeighted = new JCheckBox("Ponderado (bonus)");

    private final JButton btnRun = new JButton("Run");
    private final JLabel lblBest = new JLabel("Best: -");
    private final JTextArea txt = new JTextArea(6, 30);

    // series para la gráfica
    private final XYSeries sBestGen = new XYSeries("Mejor gen (rojo)");
    private final XYSeries sBestEver = new XYSeries("Mejor histórico (azul)");
    private final XYSeries sAvg = new XYSeries("Media (verde)");

    private final BoardPanel boardPanel;

    // escenario hardcoded (por ahora)
    private final int[][] map = {
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 1, 0, 0, 0, 0, 0, 0, 1, 0},
            {0, 0, 0, 1, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0}
    };

    public Graphic() {
        super("Práctica 1 - UI mínima");

        int[][] initialBoard = copyMap(map);
        boardPanel = new BoardPanel(initialBoard);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildCenterChart(), BorderLayout.CENTER);
        add(buildRightPanel(), BorderLayout.EAST);

        btnRun.addActionListener(e -> run());

        setSize(1050, 650);
        setLocationRelativeTo(null);
    }

    private JPanel buildTopPanel() {
        JPanel form = new JPanel(new GridLayout(0, 4, 12, 8)); // 0 filas, 4 columnas, gaps buenos
        form.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10)); // margen alrededor

        // fila 1
        form.add(new JLabel("Tipo:"));        form.add(problemType);
        form.add(new JLabel("Población:"));   form.add(spPop);

        // fila 2
        form.add(new JLabel("Generaciones:"));form.add(spGen);
        form.add(new JLabel("Pc:"));          form.add(spPc);

        // fila 3
        form.add(new JLabel("Pm:"));          form.add(spPm);
        form.add(new JLabel("Selección:"));   form.add(selMethod);

        // fila 4
        form.add(new JLabel("Cruce:"));       form.add(crossMethod);
        form.add(new JLabel("Mutación:"));    form.add(mutMethod);

        // fila 5
        form.add(new JLabel("Elitismo:"));    form.add(spElit);
        form.add(new JLabel(""));            form.add(cbWeighted);

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
        JPanel right = new JPanel(new BorderLayout(5,5));
        right.setPreferredSize(new Dimension(360, 600));

        txt.setEditable(false);
        txt.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        right.add(new JScrollPane(txt), BorderLayout.NORTH);
        right.add(boardPanel, BorderLayout.CENTER);
        return right;
    }

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
        String type = (String) problemType.getSelectedItem();
        String selectionMethod = (String) selMethod.getSelectedItem();
        String crossoverMethod = (String) crossMethod.getSelectedItem();
        String mutationMethod = (String) mutMethod.getSelectedItem();

        int N = map.length, M = map[0].length;

        EvolutionListener listener = (gen, bestGen, bestEver, avg, bestChrObj) -> SwingUtilities.invokeLater(() -> {
            sBestGen.add(gen, bestGen);
            sBestEver.add(gen, bestEver);
            sAvg.add(gen, avg);
            lblBest.setText("Best: " + bestEver);

            int[][] board;
            if ("BINARY".equals(type)) {
                board = renderBoardBinary(map, (practica.pe.binary.Chromosome) bestChrObj);
            } else {
                board = renderBoardReal(map, (practica.pe.real.Chromosome) bestChrObj);
            }
            boardPanel.setBoard(board);
        });

        new Thread(() -> {
            try {
                GAResult result;

                if ("BINARY".equals(type)) {
                    int NUM_CAMARAS = 1;   // ponlo como quieras (spinner o hardcoded)
                    int RANGO = 1;

                    practica.pe.binary.Fitness fitness = new practica.pe.binary.Fitness(map, NUM_CAMARAS, RANGO, N, M);

                    int xBits = Integer.toBinaryString(N - 1).length();
                    int yBits = Integer.toBinaryString(M - 1).length();
                    int totalBits = NUM_CAMARAS * (xBits + yBits);

                    practica.pe.binary.Population pop = new practica.pe.binary.Population(fitness, popSize, totalBits);
                    practica.pe.binary.Evolution evo = new practica.pe.binary.Evolution(fitness, popSize, totalBits);

                    result = evo.evolveWithListener(gens, pop, pc, pm, elit, selectionMethod, crossoverMethod, listener);

                } else {
                    int NUM_CAMARAS = 3;
                    int RANGO = 3;
                    double FOV = 60.0;

                    practica.pe.real.Fitness fitness = new practica.pe.real.Fitness(map, NUM_CAMARAS, RANGO, N, M, FOV);
                    practica.pe.real.Population pop = new practica.pe.real.Population(fitness, popSize, NUM_CAMARAS, N, M);
                    practica.pe.real.Evolution evo = new practica.pe.real.Evolution(fitness, popSize, NUM_CAMARAS, N, M);

                    result = evo.evolveWithListener(gens, pop, pc, pm, elit, selectionMethod, crossoverMethod, mutationMethod, listener);
                }

                Object bestObj = result.getBest();

                SwingUtilities.invokeLater(() -> {
                    btnRun.setEnabled(true);

                    if ("BINARY".equals(type)) {
                        practica.pe.binary.Chromosome best = (practica.pe.binary.Chromosome) bestObj;
                        txt.append("Best fitness: " + best.getFitness() + "\n");
                        txt.append("Best genes: " + best.getGenes() + "\n");
                    } else {
                        practica.pe.real.Chromosome best = (practica.pe.real.Chromosome) bestObj;
                        txt.append("Best fitness: " + best.getFitness() + "\n");
                        txt.append("Best genes: " + Arrays.toString(best.getGenes()) + "\n");
                    }
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    btnRun.setEnabled(true);
                    lblBest.setText("Best: error");
                    txt.setText("Error: " + ex);
                });
            }
        }).start();
    }

    private static int[][] copyMap(int[][] map) {
        int[][] out = new int[map.length][map[0].length];
        for (int i = 0; i < map.length; i++) {
            System.arraycopy(map[i], 0, out[i], 0, map[0].length);
        }
        return out;
    }

    private static int[][] renderBoardReal(int[][] map, practica.pe.real.Chromosome chr) {
        int N = map.length, M = map[0].length;
        int[][] out = copyMap(map);

        double[] g = chr.getGenes();
        for (int cam = 0; cam < g.length / 3; cam++) {
            int base = cam * 3;
            int x = (int) Math.floor(g[base]);
            int y = (int) Math.floor(g[base + 1]);
            if (x >= 0 && x < N && y >= 0 && y < M && out[x][y] == 0) out[x][y] = 2;
        }
        return out;
    }

    private static int[][] renderBoardBinary(int[][] map, practica.pe.binary.Chromosome chr) {
        int N = map.length, M = map[0].length;
        int[][] out = copyMap(map);

        String genes = chr.getGenes();

        int xBits = Integer.toBinaryString(N - 1).length();
        int yBits = Integer.toBinaryString(M - 1).length();
        int stride = xBits + yBits;
        int numCams = genes.length() / stride;

        for (int cam = 0; cam < numCams; cam++) {
            int start = cam * stride;

            int x = Integer.parseInt(genes.substring(start, start + xBits), 2);
            int y = Integer.parseInt(genes.substring(start + xBits, start + stride), 2);

            if (x >= 0 && x < N && y >= 0 && y < M && out[x][y] == 0) out[x][y] = 2;
        }

        return out;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Graphic().setVisible(true));
    }
}