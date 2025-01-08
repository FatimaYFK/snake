package at.ac.fhcampuswien.snake.ingameobjects;

import at.ac.fhcampuswien.snake.util.Constants;
import at.ac.fhcampuswien.snake.util.StateManager;

import java.util.Objects;

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

        this.scoreValue =  calculateScoreValue(StateManager.difficulty, scoreWeight);
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
        boolean isTargetFieldFree;
        Position currentPosition;

        do {
            isTargetFieldFree = true;
            currentPosition = new Position();

            // Check if calculated Position is inhibited by the snake
            int i = 0;
            do {
                if (snake.getSegments().get(i).equals(currentPosition)) {
                    isTargetFieldFree = false;
                }
                i++;
            } while (isTargetFieldFree && i < snake.getSegments().size());

            // Check if calculated Position is inhibited by the wall
            if (wall != null && isTargetFieldFree) {
                int j = 0;
                do {
                    if (wall.getSegments().get(j).equals(currentPosition)) {
                        isTargetFieldFree = false;
                    }
                    j++;
                } while (isTargetFieldFree && j < wall.getSegments().size());
            }

            // Check if currently existing regular Food is on desired Position
            if (isSpecialFood() && currentlyExistingRegularFood != null && isTargetFieldFree) {
                if (currentlyExistingRegularFood.position.equals(currentPosition)) {
                    isTargetFieldFree = false;
                }
            }

        } while (!isTargetFieldFree);

        return currentPosition;
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
            int randomFoodTypeNumber = (int) (Math.random() * eligibleFoodTypes.length);
            foodType = eligibleFoodTypes[randomFoodTypeNumber];
        } while (Objects.equals(foodType, previousFoodType));

        return foodType;
    }

    int calculateScoreValue(Constants.Difficulty difficulty, int scoreWeight) {
        return scoreWeight * translateIntoFoodValue(StateManager.difficulty);
    }
}
