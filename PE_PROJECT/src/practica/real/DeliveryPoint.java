package practica.real;

public class DeliveryPoint extends Vertex {
    private final int waitingTime;

    public DeliveryPoint(Vertex edge, int waitingTime) {
        super(edge.getId(), edge.getX(), edge.getY());
        this.waitingTime = waitingTime;
    }

    public int getWaitingTime() { return waitingTime; }
}