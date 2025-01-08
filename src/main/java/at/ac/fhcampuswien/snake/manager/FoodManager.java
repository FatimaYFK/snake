package at.ac.fhcampuswien.snake.manager;

import at.ac.fhcampuswien.snake.ingameobjects.Food;
import at.ac.fhcampuswien.snake.ingameobjects.SpecialFood;
import at.ac.fhcampuswien.snake.ingameobjects.Snake;
import at.ac.fhcampuswien.snake.ingameobjects.Wall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static at.ac.fhcampuswien.snake.util.Constants.*;

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

    private static final Integer SPECIAL_FOOD_RANGE_MIN = 5;
    private static final Integer SPECIAL_FOOD_RANGE_MAX = 10;

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
        return new Random().nextInt((SPECIAL_FOOD_RANGE_MAX - SPECIAL_FOOD_RANGE_MIN) + 1 ) + SPECIAL_FOOD_RANGE_MIN;
    }

    /**
     * Generates a regular food item.
     */
    public void generateRegularFood() {
        this.regularFood = new Food(snake, innerWall, null, previousRegularFoodType);
        this.previousRegularFoodType = regularFood.getFoodType();
    }

    /**
     * Generates a special food item.
     */
    public void generateSpecialFood() {
        this.specialFood = new SpecialFood(snake, innerWall, regularFood, previousSpecialFoodType);
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