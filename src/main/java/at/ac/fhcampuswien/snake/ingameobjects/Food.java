package at.ac.fhcampuswien.snake.ingameobjects;

import at.ac.fhcampuswien.snake.util.Constants;
import at.ac.fhcampuswien.snake.util.StateManager;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Food {

    Position position;

    String foodType;

    int scoreValue;

    private final static String[] FOOD_TYPES = new String[]{"1.png", "2.png", "3.png",
            "4.png", "5.png", "6.png", "7.png", "8.png", "9.png", "10.png",
            "11.png", "12.png", "13.png", "14.png", "15.png"};

    public final static int SCORE_VALUE = 1;

    void initialiseFood(Snake snake, Wall wall, Food currentlyExistingRegularFood, String previousFoodType, String[] eligibleFoodTypes, int scoreWeight)
    {
        this.foodType = calculateFoodType(previousFoodType, eligibleFoodTypes);

        this.position = calculateFreeRandomPositionOnGameBoard(snake, wall, currentlyExistingRegularFood);

        this.scoreValue =  calculateScoreValue(scoreWeight);
    }

    //MM20250106: dummy, so we can write SpecialFood more elegantly
    Food() {};

    public Food(Snake snake, Wall wall, Food currentlyExistingRegularFood, String previousFoodType) {
        initialiseFood(snake, wall, currentlyExistingRegularFood, previousFoodType, this.FOOD_TYPES, this.SCORE_VALUE);
    }

    public Position getLocation() {
        return position;
    }

    public String getFoodType() {
        return foodType;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public boolean isSpecialFood() {
        return false;
    }

    public int getSpecialFoodTimeToLive() {
        return -1;
    }

    public void decreaseSpecialFoodTimeToLive() {
        return;
    }

    Position calculateFreeRandomPositionOnGameBoard(Snake snake, Wall wall, Food currentlyExistingRegularFood) {
        boolean isTargetFieldFree = true;
        Position currentPosition;

        do {
            currentPosition = new Position();

            // Check if calculated Position is inhibited by the snake
            isTargetFieldFree = checkPositionFree(snake.getSegments(), currentPosition);

            // Check if calculated Position is inhibited by the wall
            if (isTargetFieldFree) {
                isTargetFieldFree = checkPositionFree(wall.getSegments(), currentPosition);
            }

            // Check if currently existing regular Food is on desired Position
            if (isSpecialFood() && currentlyExistingRegularFood != null && isTargetFieldFree && currentlyExistingRegularFood.position.equals(currentPosition)) {
                    isTargetFieldFree = false;
            }

        } while (!isTargetFieldFree);

        return currentPosition;
    }

    boolean checkPositionFree(List<Position> objectSegments, Position currentPosition) {
        for (int i = 0; i < objectSegments.size(); i++) {
            if (objectSegments.get(i).equals(currentPosition)) {
                return false; // conflict found, not free
            }
        }
        return true;
    }

    int translateIntoFoodValue(Constants.Difficulty difficulty) {
        switch (difficulty) {
            case EASY:
                return 1;
            case MEDIUM:
                return 2;
            case HARD:
                return 3;
            default:
                throw new IllegalStateException("Unexpected value: " + StateManager.difficulty);
        }
    }

    String calculateFoodType(String previousFoodType, String[] eligibleFoodTypes)
    {
        String foodType = "";
        do {
            int randomFoodTypeNumber = new Random().nextInt(eligibleFoodTypes.length);
            foodType = eligibleFoodTypes[randomFoodTypeNumber];
        } while (Objects.equals(foodType, previousFoodType));

        return foodType;
    }

    int calculateScoreValue(int scoreWeight) {
        return scoreWeight * translateIntoFoodValue(StateManager.difficulty);
    }
}
