package at.ac.fhcampuswien.snake.ingameobjects;

import at.ac.fhcampuswien.snake.util.Constants;

public class Position {
    private int x;
    private int y;

    // Konstruktor
    public Position(int x, int y) {
        this.x = alignToGrid(x);
        this.y = alignToGrid(y);
    }

    // Getter
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Hilfsmethode zur Rasterausrichtung
    private int alignToGrid(int coordinate) {
        return (coordinate / Constants.OBJECT_SIZE_MEDIUM) * Constants.OBJECT_SIZE_MEDIUM;
    }

    // Überschreibe die equals Methode
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position)) return false;
        Position other = (Position) obj;
        return this.x == other.x && this.y == other.y;
    }

    // Überschreibe die hashCode Methode
    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}