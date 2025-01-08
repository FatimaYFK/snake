package at.ac.fhcampuswien.snake.ingameobjects;

import java.util.ArrayList;
import java.util.List;

import static at.ac.fhcampuswien.snake.util.Constants.OBJECT_SIZE_MEDIUM;

public class Wall {

    private final List<Position> segments = new ArrayList<>();

    /**
     * This Constructor creates a wall with a specified length
     * It requires the orientation, the length and the starting points
     *
     * @param isHorizontal      specifies if the wall is horizontal or not. Depending on this, one of the coordinates is constant.
     * @param length            The length of the wall
     */
    public Wall(boolean isHorizontal, Position startingPosition, int length) {
        if (isHorizontal) {
            for (int i = 0; i < length; i++) {
                segments.add(new Position(startingPosition.getX() + i * OBJECT_SIZE_MEDIUM, startingPosition.getY()));
            }
        } else {
            for (int i = 0; i < length; i++) {
                segments.add(new Position(startingPosition.getX(), startingPosition.getY() + i * OBJECT_SIZE_MEDIUM));
            }
        }
    }

    public List<Position> getSegments() {
        return segments;
    }
}
