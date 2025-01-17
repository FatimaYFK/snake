package at.ac.fhcampuswien.snake.manager;

import at.ac.fhcampuswien.snake.ingameobjects.Food;
import at.ac.fhcampuswien.snake.ingameobjects.Snake;
import at.ac.fhcampuswien.snake.ingameobjects.Wall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Random;

/**
 * Manages the creation and handling of food items in the game.
 */
public class FoodManager {

    private final static Logger LOG = LoggerFactory.getLogger(FoodManager.class);
    private final Snake snake;
    private final Wall innerWall;

    private Food regularFood;
    private Food specialFood;
    private String previousRegularFoodType;
    private String previousSpecialFoodType;
    private int foodsEatenSinceLastSpecialFood;
    private int foodsToEatUntilNextSpecialFood;

    private static final int MAX_HIGHSCORES = 5;

    public FoodManager(Snake snake, Wall innerWall) {
        this.snake = snake;
        this.innerWall = innerWall;
        this.foodsEatenSinceLastSpecialFood = 0;
        this.foodsToEatUntilNextSpecialFood = getRandomFoodsToEat();
        generateRegularFood();
    }

    /**
     * Generates a random number of foods to eat until the next special food appears.
     *
     * @return random integer between 5 and 10.
     */
    private int getRandomFoodsToEat() {
        return 5 + new Random().nextInt(6); // 5 to 10
    }

    /**
     * Generates a regular food item.
     */
    public void generateRegularFood() {
        this.regularFood = new Food(snake, innerWall, null, false, previousRegularFoodType);
        this.previousRegularFoodType = regularFood.getFoodType();
    }

    /**
     * Generates a special food item.
     */
    public void generateSpecialFood() {
        this.specialFood = new Food(snake, innerWall, regularFood, true, previousSpecialFoodType);
        this.previousSpecialFoodType = specialFood.getFoodType();
    }

    public Food getRegularFood() {
        return regularFood;
    }

    public Food getSpecialFood() {
        return specialFood;
    }

    /**
     * Handles the consumption of regular food.
     *
     * @param scoreValue The score value of the consumed food.
     */
    public void handleFoodConsumption(int scoreValue) {
        foodsEatenSinceLastSpecialFood++;
        foodsToEatUntilNextSpecialFood = getRandomFoodsToEat();
        this.regularFood = null;
    }

    /**
     * Resets the conditions for special food.
     */
    public void resetSpecialFood() {
        this.specialFood = null;
        foodsEatenSinceLastSpecialFood = 0;
        foodsToEatUntilNextSpecialFood = getRandomFoodsToEat();
    }

    /**
     * Determines if it's time to generate a special food.
     *
     * @return true if conditions are met, false otherwise.
     */
    public boolean shouldGenerateSpecialFood() {
        return foodsEatenSinceLastSpecialFood >= foodsToEatUntilNextSpecialFood && specialFood == null;
    }
}