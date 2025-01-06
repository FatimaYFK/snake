package at.ac.fhcampuswien.snake.ingameobjects;

import at.ac.fhcampuswien.snake.util.Constants;
import at.ac.fhcampuswien.snake.util.StateManager;

public class SpecialFood extends Food {

    public final static String[] FOOD_TYPES = new String[]{"B1.png", "B2.png", "B3.png", "B4.png", "B5.png", "B6.png"};

    public static final int SCORE_VALUE = 3;

    private int specialFoodTimeToLive = calculateFoodLifetime();

    public SpecialFood(Snake snake, Wall wall, Food currentlyExistingRegularFood, String previousFoodType) {
        super();

        initialiseFood(snake, wall, currentlyExistingRegularFood, previousFoodType, this.FOOD_TYPES, this.SCORE_VALUE);
    }

    @Override
    public boolean isSpecialFood() {
        return true;
    }

    @Override
    public int getSpecialFoodTimeToLive() {
        return this.specialFoodTimeToLive;
    }

    @Override
    public void decreaseSpecialFoodTimeToLive() {
        specialFoodTimeToLive--;
    }

    // range: 18 - 36
    private int calculateFoodLifetime() {
        return (int) (18 + (Math.random() * 18));
    }
}