package practica.pe.real;

public record Pair(int x, int y) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair(int x1, int y1))) return false;
        return x == x1 && y == y1;
    }

}