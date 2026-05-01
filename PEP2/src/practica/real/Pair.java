package practica.real;

public record Pair(int x, int y) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair other = (Pair) o;
        return x == other.x() && y == other.y();
    }

}