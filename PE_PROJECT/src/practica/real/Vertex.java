package practica.real;


public class Vertex {
    private final int id;
    private final double x;
    private final double y;

    public Vertex(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() { return id; }
    public double getX() { return x; }
    public double getY() { return y; }

    @Override
    public String toString() {
        return String.format("Punto %d (%.2f, %.2f)", id, x, y);
    }
}