package at.ac.fhcampuswien.snake.ingameobjects;

import at.ac.fhcampuswien.snake.util.Constants;
import at.ac.fhcampuswien.snake.util.StateManager;

import java.util.Random;

public class SpecialFood extends Food {

    public final static String[] FOOD_TYPES = new String[]{"B1.png", "B2.png", "B3.png", "B4.png", "B5.png", "B6.png"};

    public static final int SCORE_VALUE = 3;

    private static final Integer LIVE_TIME_MIN = 18;
    private static final Integer LIVE_TIME_MAX = 36;

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

    private int calculateFoodLifetime() {
        return new Random().nextInt((LIVE_TIME_MAX - LIVE_TIME_MIN) + 1 ) + LIVE_TIME_MIN;
    }
}