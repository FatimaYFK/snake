package at.ac.fhcampuswien.snake.ingameobjects;

import at.ac.fhcampuswien.snake.util.Constants;

public class Position {
    private int x;
    private int y;

    public Position() {
        int segmentNumberX = Constants.NUMBER_OF_ROWS_AND_COLS - 2;
        int segmentNumberY = Constants.NUMBER_OF_ROWS_AND_COLS - 3;
        // We reduce "3" from segmentNumberY, because there are two rows being used for the outer walls.
        // Additionally there is 1 row used for the status bar at the top.
        x = (int) (Math.random() * segmentNumberX) + 1;
        y = (int) (Math.random() * segmentNumberY) + 1;

        // Since the Location of the Elements of the Snake is in PX, we need to multiply
        // the row and column number by the Object Size in PX.
        scale(Constants.OBJECT_SIZE_MEDIUM);
    }

    public Position(int x, int y) {
        this.x = alignToGrid(x);
        this.y = alignToGrid(y);
    }

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof Position)) return false;

        Position other = (Position) obj;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    public void scale(int factor)
    {
        this.x *= factor;
        this.y *= factor;
    }
}