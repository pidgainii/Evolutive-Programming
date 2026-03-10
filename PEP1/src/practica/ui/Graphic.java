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

public class Graphic extends JFrame {

    // parámetros mínimos del enunciado
    private final JComboBox<String> cbScenario = new JComboBox<>(new String[]{"MAP 1 (10x10)", "MAP 2 (12x15)", "MAP 3 (18x20)"});
    private final JComboBox<String> problemType = new JComboBox<>(new String[]{"BINARY", "REAL"});
    private final JSpinner spPop = new JSpinner(new SpinnerNumberModel(100, 2, 5000, 10));
    private final JSpinner spGen = new JSpinner(new SpinnerNumberModel(200, 1, 100000, 10));
    private final JSpinner spPc  = new JSpinner(new SpinnerNumberModel(0.60, 0.0, 1.0, 0.01));
    private final JSpinner spPm  = new JSpinner(new SpinnerNumberModel(0.05, 0.0, 1.0, 0.001));

    private final JComboBox<String> selMethod = new JComboBox<>(new String[]{"ROULETTE", "TOURNAMENT", "STOCHASTIC", "TRUNCATION", "REMAINDERS"});
    private final JComboBox<String> crossMethod = new JComboBox<>(new String[]{"ONE_POINT", "UNIFORM", "ARITHMETIC", "BLX_ALPHA"});
    private final JComboBox<String> mutMethod = new JComboBox<>(new String[]{"BIT", "GAUSSIAN", "GENE"});
    private final JSpinner spElit = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.05));
    private final JCheckBox cbWeighted = new JCheckBox("Ponderado (bonus)");

    private final JButton btnRun = new JButton("Run");
    private final JLabel lblBest = new JLabel("Best: -");
    private final JTextArea txt = new JTextArea(6, 30);

    // series para la gráfica (IMPORTANT: allowDuplicateXValues=true)
    private final XYSeries sBestGen = new XYSeries("Mejor gen (rojo)", false, true);
    private final XYSeries sBestEver = new XYSeries("Mejor histórico (azul)", false, true);
    private final XYSeries sAvg = new XYSeries("Media (verde)", false, true);

    private final BoardPanel boardPanel;
    private boolean ponderado;


    // MAP 1
    private final int[][] map1 = {
        {1, 1, 1, 1, 1, 0, 1, 1, 1, 1},
        {1, 0, 5, 5, 5, 1, 1, 1, 0, 1}, // Pasillo Norte
        {1, 1, 1, 0, 1, 1, 0, 1, 1, 1},
        {1, 1, 1, 1, 5, 5, 5, 1, 1, 1}, // Pasillo Centro
        {0, 1, 1, 1, 0, 10, 1, 1, 1, 1}, // <--- JOYA (10)
        {1, 1, 0, 1, 1, 5, 1, 0, 1, 1}, // Conexión
        {1, 1, 1, 1, 1, 5, 1, 1, 1, 1}, // Conexión
        {1, 1, 0, 1, 1, 1, 1, 1, 1, 1},
        {1, 1, 5, 5, 5, 0, 5, 5, 5, 1}, // Pasillo Sur
        {1, 1, 1, 1, 1, 1, 1, 0, 1, 1}
    };

    // MAP 2
    // TODO: Añadir tambien el mapa sin ponderacion
    private final int[][] map2 = {
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0,20, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1,20, 0},
        {0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0},
        {0, 1, 1, 1, 1, 1, 1, 5, 1, 1, 1, 1, 1, 1, 0},
        {0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0},
        {0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0},
        {0, 1, 0, 1, 0, 0, 0,20, 0, 0, 0, 1, 0, 1, 0},
        {0, 1, 1, 5, 1, 1, 1, 1, 1, 1, 1, 5, 1, 1, 0},
        {0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0},
        {0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0},
        {0,20, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0,20, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };

    // MAP 3
    private final int[][] map3 = {
	        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	        {0,1,1,1,1,1,5,5,5,5,5,5,1,1,1,1,1,1,1,0},
	        {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
	        {0,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,1,0},
	        {0,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,1,0},
	        {0,1,0,0,15,1,0,0,1,1,0,0,1,1,0,0,1,1,1,0},
	        {0,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,1,0},
	        {0,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,1,0},
	        {0,1,5,5,5,5,5,5,1,1,5,5,5,5,5,5,5,1,1,0},
	        {0,1,5,5,5,5,5,5,1,1,5,5,5,5,5,5,5,1,1,0},
	        {0,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,1,0},
	        {0,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,1,0},
	        {0,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,1,0},
	        {0,1,0,0,15,1,0,0,1,1,0,0,1,1,0,0,1,1,1,0},
	        {0,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,1,0},
	        {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
	        {0,1,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,1,0},
	        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
    };

    private int[][] map = map1; // por defecto, mapa 1

    public Graphic() {
        super("Práctica 1 - UI mínima");

        // Usamos el map[1] para dibujarlo, que es el que contiene info sobre las joyas
        boardPanel = new BoardPanel(copyMap(map));
        
        cbScenario.addActionListener(e -> {
            map = switch (cbScenario.getSelectedIndex()) {
                case 0 -> map1;
                case 1 -> map2;
                case 2 -> map3;
                default -> map1;
            };
            boardPanel.setBoard(copyMap(map));
        });
        
        cbWeighted.addActionListener(e -> {
            ponderado = cbWeighted.isSelected();
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildCenterChart(), BorderLayout.CENTER);
        add(buildRightPanel(), BorderLayout.EAST);

        btnRun.addActionListener(e -> run());

        setSize(1050, 650);
        setLocationRelativeTo(null);
    }

    private JPanel buildTopPanel() {
        JPanel form = new JPanel(new GridLayout(0, 4, 12, 8));
        form.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        form.add(new JLabel("Tipo:"));        form.add(problemType);
        form.add(new JLabel("Escenario:"));   form.add(cbScenario);

        form.add(new JLabel("Población:"));   form.add(spPop);
        form.add(new JLabel("Generaciones:"));form.add(spGen);

        form.add(new JLabel("Pc:"));          form.add(spPc);
        form.add(new JLabel("Pm:"));          form.add(spPm);

        form.add(new JLabel("Selección:"));   form.add(selMethod);
        form.add(new JLabel("Cruce:"));       form.add(crossMethod);

        form.add(new JLabel("Mutación:"));    form.add(mutMethod);
        form.add(new JLabel("Elitismo:"));    form.add(spElit);

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

        String type = (String) problemType.getSelectedItem();
        String selectionMethod = (String) selMethod.getSelectedItem();
        String crossoverMethod = (String) crossMethod.getSelectedItem();
        String mutationMethod = (String) mutMethod.getSelectedItem();

        int NUM_CAMARAS;
        int RANGO;
        double FOV;

        switch (cbScenario.getSelectedIndex()) {
            case 0 -> { NUM_CAMARAS = 4; RANGO = 3; FOV = 60.0; }
            case 1 -> { NUM_CAMARAS = 7; RANGO = 5; FOV = 90.0; }
            case 2 -> { NUM_CAMARAS = 8; RANGO = 7; FOV = 70.0; }
            default -> { NUM_CAMARAS = 4; RANGO = 3; FOV = 60.0; }
        }

        int N = map.length, M = map[0].length;

        EvolutionListener listener = (gen, bestGen, bestEver, avg, bestChrObj) -> SwingUtilities.invokeLater(() -> {
            // IMPORTANT: addOrUpdate avoids SeriesException when X repeats
            sBestGen.addOrUpdate(gen, bestGen);
            sBestEver.addOrUpdate(gen, bestEver);
            sAvg.addOrUpdate(gen, avg);
            lblBest.setText("Best: " + bestEver);

            int[][] board = "BINARY".equals(type)
                    ? renderBoardBinary(map, (practica.binary.Chromosome) bestChrObj, RANGO)
                    : renderBoardReal(map, (practica.real.Chromosome) bestChrObj, RANGO, FOV);

            boardPanel.setBoard(board);
        });

        new Thread(() -> {
            try {
                GAResult result;

                if ("BINARY".equals(type)) {
                    practica.binary.Fitness fitness = new practica.binary.Fitness(map, NUM_CAMARAS, RANGO, N, M, ponderado);

                    int xBits = Integer.toBinaryString(N - 1).length();
                    int yBits = Integer.toBinaryString(M - 1).length();
                    int totalBits = NUM_CAMARAS * (xBits + yBits);

                    practica.binary.Population pop = new practica.binary.Population(fitness, popSize, totalBits, ponderado);
                    practica.binary.Evolution evo = new practica.binary.Evolution(fitness, popSize, totalBits, ponderado);

                    result = evo.evolveWithListener(gens, pop, pc, pm, elit, selectionMethod, crossoverMethod, listener);

                } else {
                    practica.real.Fitness fitness = new practica.real.Fitness(map, NUM_CAMARAS, RANGO, N, M, FOV);
                    practica.real.Population pop = new practica.real.Population(fitness, popSize, NUM_CAMARAS, N, M, ponderado);
                    practica.real.Evolution evo = new practica.real.Evolution(fitness, popSize, NUM_CAMARAS, N, M, ponderado);

                    
                    result = evo.evolveWithListener(gens, pop, pc, pm, elit, selectionMethod, crossoverMethod, mutationMethod, listener);
                }

                Object bestObj = result.getBest();

                SwingUtilities.invokeLater(() -> {
                    btnRun.setEnabled(true);
                    cbScenario.setEnabled(true);

                    if ("BINARY".equals(type)) {
                        practica.binary.Chromosome best = (practica.binary.Chromosome) bestObj;
                        txt.append("Best fitness: " + best.getFitness() + "\n");
                        txt.append("Best genes: " + best.getGenes() + "\n");
                    } else {
                        practica.real.Chromosome best = (practica.real.Chromosome) bestObj;
                        txt.append("Best fitness: " + best.getFitness() + "\n");
                        txt.append("Best genes: " + Arrays.toString(best.getGenes()) + "\n");
                    }
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

    private static int[][] copyMap(int[][] map) {
        int[][] out = new int[map.length][map[0].length];
        for (int i = 0; i < map.length; i++) {
            System.arraycopy(map[i], 0, out[i], 0, map[0].length);
        }
        return out;
    }

    private static int[][] renderBoardReal(int[][] map,
                                           practica.real.Chromosome chr,
                                           int range,
                                           double fovDeg) {
        int N = map.length, M = map[0].length;
        int[][] out = copyMap(map);

        double[] g = chr.getGenes();
        int numCams = g.length / 3;

        // coloca cámaras: SOLO en celdas transitables (valor > 0)
        for (int cam = 0; cam < numCams; cam++) {
            int base = cam * 3;
            int x = (int) Math.floor(g[base]);
            int y = (int) Math.floor(g[base + 1]);

            if (x >= 0 && x < N && y >= 0 && y < M && map[x][y] != 0) {
                out[x][y] = 2;
            }
        }

        int rays = Math.max(5, (int) Math.round(fovDeg));
        for (int cam = 0; cam < numCams; cam++) {
            int base = cam * 3;
            double cx = g[base];
            double cy = g[base + 1];
            double theta = g[base + 2];

            int startCellX = (int) Math.floor(cx);
            int startCellY = (int) Math.floor(cy);

            if (startCellX < 0 || startCellX >= N || startCellY < 0 || startCellY >= M) continue;

            // ANTES: muro == 1
            // AHORA: muro == 0
            if (map[startCellX][startCellY] == 0) continue;

            double startAng = theta - fovDeg / 2.0;
            double step = (rays <= 1) ? 0.0 : (fovDeg / (rays - 1));

            for (int ri = 0; ri < rays; ri++) {
                double angDeg = startAng + ri * step;
                double ang = Math.toRadians(angDeg);

                double dx = Math.cos(ang);
                double dy = Math.sin(ang);

                double x = cx + 0.5;
                double y = cy + 0.5;

                for (int s = 0; s < range * 10; s++) {
                    x += dx * 0.1;
                    y += dy * 0.1;

                    int ix = (int) Math.floor(x);
                    int iy = (int) Math.floor(y);

                    if (ix < 0 || ix >= N || iy < 0 || iy >= M) break;

                    double dist = Math.hypot(x - (cx + 0.5), y - (cy + 0.5));
                    if (dist > range) break;

                    // muro corta
                    if (map[ix][iy] == 0) break;

                    if (out[ix][iy] != 2) out[ix][iy] = 3;
                }
            }
        }

        return out;
    }

    private static int[][] renderBoardBinary(int[][] map,
                                             practica.binary.Chromosome chr,
                                             int range) {
        int N = map.length, M = map[0].length;
        int[][] out = copyMap(map);

        String genes = chr.getGenes();

        int xBits = Integer.toBinaryString(N - 1).length();
        int yBits = Integer.toBinaryString(M - 1).length();
        int stride = xBits + yBits;
        int numCams = genes.length() / stride;

        boolean[][] camAt = new boolean[N][M];
        int[][] cams = new int[numCams][2];
        int camCount = 0;

        for (int cam = 0; cam < numCams; cam++) {
            int start = cam * stride;
            int x = Integer.parseInt(genes.substring(start, start + xBits), 2);
            int y = Integer.parseInt(genes.substring(start + xBits, start + stride), 2);

            // ANTES: map[x][y] == 0 era libre
            // AHORA: map[x][y] != 0 es transitable/valiosa
            if (x >= 0 && x < N && y >= 0 && y < M && map[x][y] != 0 && !camAt[x][y]) {
                camAt[x][y] = true;
                cams[camCount][0] = x;
                cams[camCount][1] = y;
                camCount++;
                out[x][y] = 2; // cámara
            }
        }

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        for (int i = 0; i < camCount; i++) {
            int x = cams[i][0];
            int y = cams[i][1];

            for (int dir = 0; dir < 4; dir++) {
                for (int r = 1; r <= range; r++) {
                    int nx = x + dx[dir] * r;
                    int ny = y + dy[dir] * r;

                    if (nx < 0 || nx >= N || ny < 0 || ny >= M) break;
                    if (camAt[nx][ny]) break;

                    // ANTES: muro era 1
                    // AHORA: muro es 0
                    if (map[nx][ny] == 0) break;

                    // marca como iluminada si es una celda normal (no sobrescribas cámaras)
                    if (out[nx][ny] != 2) out[nx][ny] = 3;
                }
            }
        }

        return out;
    }
}