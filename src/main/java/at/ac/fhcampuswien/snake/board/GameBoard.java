package at.ac.fhcampuswien.snake.board;

import at.ac.fhcampuswien.snake.ingameobjects.Food;
import at.ac.fhcampuswien.snake.ingameobjects.Position;
import at.ac.fhcampuswien.snake.ingameobjects.Snake;
import at.ac.fhcampuswien.snake.ingameobjects.Wall;
import at.ac.fhcampuswien.snake.manager.FoodManager;
import at.ac.fhcampuswien.snake.manager.PauseManager;
import at.ac.fhcampuswien.snake.service.HighscoreService;
import at.ac.fhcampuswien.snake.util.Player;
import at.ac.fhcampuswien.snake.util.SoundFX;
import at.ac.fhcampuswien.snake.util.StateManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static at.ac.fhcampuswien.snake.util.Constants.Direction.*;
import static at.ac.fhcampuswien.snake.util.Constants.*;

/**
 * Represents the game board where the Snake game is played.
 */
public class GameBoard {

    private final static Logger LOG = LoggerFactory.getLogger(GameBoard.class);

    private final GraphicsContext gc;
    private final Canvas gameBoardCanvas;
    private final Timeline timeline;
    private final PauseManager pauseManager;

    private boolean isGamePaused = false;

    private Snake snake;
    private Wall innerWall;
    private FoodManager foodManager;

    private int score;
    private Food regularFood;

    public int getScore() {
        return score;
    }

    private final Image snakeHeadUp;
    private final Image snakeHeadDown;
    private final Image snakeHeadLeft;
    private final Image snakeHeadRight;
    private final Image snakeBody;
    private final Image wallPattern;

    /**
     * Constructor for GameBoard.
     *
     * @param gameBoardCanvas Canvas to draw on
     * @param difficulty      Game difficulty level
     */
    public GameBoard(Canvas gameBoardCanvas, Difficulty difficulty) {
        this.gameBoardCanvas = gameBoardCanvas;
        this.gameBoardCanvas.requestFocus();
        this.gc = gameBoardCanvas.getGraphicsContext2D();
        this.score = 0;

        this.snakeHeadUp = new Image("graphics/snake/head_up.png");
        this.snakeHeadDown = new Image("graphics/snake/head_down.png");
        this.snakeHeadLeft = new Image("graphics/snake/head_left.png");
        this.snakeHeadRight = new Image("graphics/snake/head_right.png");
        this.snakeBody = new Image("graphics/snake/body.png");
        this.wallPattern = new Image("graphics/wall/wall_pattern.png");

        this.timeline = new Timeline(new KeyFrame(Duration.millis(difficulty.getRefreshTime()), e -> refreshGameBoard()));
        this.timeline.setCycleCount(Animation.INDEFINITE);

        this.pauseManager = new PauseManager();
    }

    /**
     * Starts a new game and a timer to refresh the game board.
     */
    public void startGame() {
        pauseManager.pauseGame(() -> timeline.play());

        initializeBoardObjects();
        initializeEvents();

        gameBoardCanvas.requestFocus();

        this.score = 0;
        StateManager.getScoreBoard().drawCountdownTimer();
        StateManager.getScoreBoard().drawScoreBoard(this.getScore());

        SoundFX.playIntroSound();

        timeline.pause();
        pauseManager.pauseGame(() -> timeline.play());
    }

    /**
     * Stops the timer which refreshes the game board.
     */
    public void stopAnimation() {
        timeline.stop();
    }

    /**
     * Ends the current game, prompts for highscore if applicable.
     */
    public void endCurrentGame() {
        pauseManager.pauseGame(() -> {
            try {
                timeline.play();
                StateManager.switchToGameOverView();
            } catch (IOException ex) {
                LOG.error("Error switching to the GameOver view", ex);
            }
        });

        SoundFX.playGameOverSound();
        this.stopAnimation();

        if (score != 0) {
            promptUserForInput();
            try {
                timeline.play();
                StateManager.switchToGameOverView();
            } catch (IOException ex) {
                LOG.error("Error switching to the GameOver view", ex);
            }
        } else {
            pauseManager.pauseGame(() -> timeline.play());
        }
    }

    /**
     * Initializes all game objects.
     */
    private void initializeBoardObjects() {
        snake = new Snake(INITIAL_SIZE, INITIAL_DIRECTION);
        innerWall = generateRandomWall();
        foodManager = new FoodManager(snake, innerWall);
        regularFood = foodManager.getRegularFood();
        drawGameBoard(gc);
        drawWalls(gc);
        drawSnake(gc);
        drawFood(gc, regularFood);
    }

    /**
     * Generates a random wall inside the gameboard.
     *
     * @return a Wall object or null if no wall is generated
     */
    private Wall generateRandomWall() {
        Random rand = new Random();
        int wallLength = rand.nextInt(5);
        if (wallLength == 0) return null;

        int randomX = getRandomWallPosition(rand, wallLength, true);
        int randomY = getRandomWallPosition(rand, wallLength, false);

        return new Wall(rand.nextBoolean(), randomX, randomY, wallLength);
    }

    /**
     * Generates a random position for a wall.
     *
     * @param rand        Random instance
     * @param wallLength  Length of the wall
     * @param isHorizontal Determines if the wall is horizontal
     * @return a valid random position
     */
    private int getRandomWallPosition(Random rand, int wallLength, boolean isHorizontal) {
        int range = GAME_BOARD_SIZE_MEDIUM - OBJECT_SIZE_MEDIUM * (wallLength + 2);
        Set<Integer> exclusions = new HashSet<>();

        for (Position segment : snake.getSegments()) {
            int segmentPosition = isHorizontal ? segment.getX() : segment.getY();
            exclusions.add(segmentPosition);
            for (int i = 0; i < wallLength; i++) {
                exclusions.add(segmentPosition + i * OBJECT_SIZE_MEDIUM);
                exclusions.add(segmentPosition - i * OBJECT_SIZE_MEDIUM);
            }
        }

        exclusions.addAll(Arrays.asList(0, OBJECT_SIZE_MEDIUM, OBJECT_SIZE_MEDIUM * 2));

        int random;
        do {
            random = (rand.nextInt(range) / OBJECT_SIZE_MEDIUM) * OBJECT_SIZE_MEDIUM;
        } while (exclusions.contains(random));

        return random;
    }

    /**
     * Draws the game board with a checkerboard pattern.
     *
     * @param gc GraphicsContext used for drawing
     */
    private void drawGameBoard(GraphicsContext gc) {
        for (int i = 0; i < GAME_BOARD_SIZE_MEDIUM; i++) {
            for (int j = 0; j < GAME_BOARD_SIZE_MEDIUM; j++) {
                if ((i + j) % 2 == 0) {
                    gc.setFill(Color.web(GAMEBOARD_COLOR_LIGHT));
                } else {
                    gc.setFill(Color.web(GAMEBOARD_COLOR_DARK));
                }
                gc.fillRect(i * OBJECT_SIZE_MEDIUM, j * OBJECT_SIZE_MEDIUM, OBJECT_SIZE_MEDIUM, OBJECT_SIZE_MEDIUM);
            }
        }
    }

    /**
     * Draws a food item on the game board.
     *
     * @param gc   GraphicsContext used for drawing
     * @param food The food item to draw
     */
    private void drawFood(GraphicsContext gc, Food food) {
        if (food == null) return;
        Image foodImg = new Image("graphics/food/" + food.getFoodType());
        gc.drawImage(foodImg, food.getLocation().getX(), food.getLocation().getY(), OBJECT_SIZE_MEDIUM, OBJECT_SIZE_MEDIUM);
    }

    /**
     * Draws the snake on the game board.
     *
     * @param gc GraphicsContext used for drawing
     */
    private void drawSnake(GraphicsContext gc) {
        gc.setFill(Color.BLUE);
        Position headPosition = snake.getSegments().get(0);
        Image head = getSnakeHeadImage();

        gc.drawImage(head, headPosition.getX(), headPosition.getY(), OBJECT_SIZE_MEDIUM, OBJECT_SIZE_MEDIUM);

        for (int i = 1; i < snake.getSegments().size(); i++) {
            Position bodySegment = snake.getSegments().get(i);
            gc.drawImage(snakeBody, bodySegment.getX(), bodySegment.getY(), OBJECT_SIZE_MEDIUM, OBJECT_SIZE_MEDIUM);
        }
    }

    /**
     * Retrieves the appropriate snake head image based on the current direction.
     *
     * @return Image of the snake's head
     */
    private Image getSnakeHeadImage() {
        switch (snake.getDirection()) {
            case RIGHT:
                return snakeHeadRight;
            case DOWN:
                return snakeHeadDown;
            case LEFT:
                return snakeHeadLeft;
            default:
                return snakeHeadUp;
        }
    }

    // Füge die folgende Methode hinzu oder passe die vorhandene an

    /**
     * Überprüft, ob der Kopf der Schlange sich auf dem Essen befindet.
     *
     * @param food Das zu überprüfende Essenobjekt.
     * @return true, wenn der Schlange auf dem Essen ist, sonst false.
     */
    private boolean checkIfSnakeHeadIsOnFood(Food food) {
        if (food == null) return false;
        Position snakeHead = snake.getSegments().get(0);
        Position foodPos = food.getLocation();
        boolean isCollision = snakeHead.equals(foodPos);
        if (isCollision) {
            LOG.info("Schlange hat das Essen an Position ({}, {}) gegessen.", foodPos.getX(), foodPos.getY());
        } else {
            LOG.debug("Schlangen-Kopf: ({}, {}), Essen-Position: ({}, {})",
                    snakeHead.getX(), snakeHead.getY(), foodPos.getX(), foodPos.getY());
        }
        return isCollision;
    }

    /**
     * Draws all walls on the game board.
     *
     * @param gc GraphicsContext used for drawing
     */
    private void drawWalls(GraphicsContext gc) {
        drawPerimeterWalls(gc);
        if (innerWall != null) drawInnerWalls(gc);
    }


    /**
     * Draws the perimeter walls.
     *
     * @param gc GraphicsContext used for drawing
     */
    private void drawPerimeterWalls(GraphicsContext gc) {
        for (int i = 0; i < GAME_BOARD_SIZE_MEDIUM; i += OBJECT_SIZE_MEDIUM) {
            gc.drawImage(wallPattern, i, 0, OBJECT_SIZE_MEDIUM, OBJECT_SIZE_MEDIUM); // Upper
            gc.drawImage(wallPattern, i, GAME_BOARD_SIZE_MEDIUM - OBJECT_SIZE_MEDIUM, OBJECT_SIZE_MEDIUM, OBJECT_SIZE_MEDIUM); // Bottom
            gc.drawImage(wallPattern, 0, i, OBJECT_SIZE_MEDIUM, OBJECT_SIZE_MEDIUM); // Left
            gc.drawImage(wallPattern, GAME_BOARD_SIZE_MEDIUM - OBJECT_SIZE_MEDIUM, i, OBJECT_SIZE_MEDIUM, OBJECT_SIZE_MEDIUM); // Right
        }
    }

    /**
     * Draws the inner walls.
     *
     * @param gc GraphicsContext used for drawing
     */
    private void drawInnerWalls(GraphicsContext gc) {
        for (Position wallSegment : innerWall.getSegments()) {
            gc.drawImage(wallPattern, wallSegment.getX(), wallSegment.getY(), OBJECT_SIZE_MEDIUM, OBJECT_SIZE_MEDIUM);
        }
    }

    /**
     * Initializes all key event handlers.
     */
    private void initializeEvents() {
        gameBoardCanvas.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP -> handleDirectionChange(UP, DOWN);
                case DOWN -> handleDirectionChange(DOWN, UP);
                case LEFT -> handleDirectionChange(LEFT, RIGHT);
                case RIGHT -> handleDirectionChange(RIGHT, LEFT);
                case P -> {
                    if (snake.isAlive()) {
                        pauseManager.togglePause(() -> {
                            if (!pauseManager.isGamePaused()) {
                                timeline.play();
                            } else {
                                timeline.pause();
                            }
                            isGamePaused = pauseManager.isGamePaused();
                        });
                    }
                }
                case ESCAPE -> handleEscape();
            }
        });
    }

    /**
     * Handles the change of direction for the snake.
     *
     * @param newDir      The new direction to set
     * @param oppositeDir The opposite direction to prevent reversing
     */
    private void handleDirectionChange(Direction newDir, Direction oppositeDir) {
        if (!isGamePaused && snake.getDirection() != oppositeDir && snake.isPositionUpdated()) {
            snake.setDirection(newDir);
            snake.setPositionUpdated(false);
        }
    }

    /**
     * Handles the escape key press to potentially exit to the start screen.
     */
    private void handleEscape() {
        if (snake.isAlive()) {
            isGamePaused = true;

            Alert alert = new Alert(Alert.AlertType.WARNING, """
                    If you return to the Start-Screen while playing the game,
                    you will lose all points.
                    Do you really want to return to the Start-Screen?""", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                this.stopAnimation();
                try {
                    StateManager.switchToStartView();
                } catch (IOException e) {
                    LOG.error("Error switching to the Start view", e);
                }
            }

            isGamePaused = false;
        }
    }

    /**
     * Prompts the user to input their name for the highscore.
     */
    private void promptUserForInput() {
        TextInputDialog inputPlayerName = new TextInputDialog();
        inputPlayerName.setHeaderText("Please enter your name:");
        inputPlayerName.setContentText("Name: ");

        Optional<String> result = inputPlayerName.showAndWait();
        String name = result.map(s -> s.replace("%", "")).orElse("Anonymous");

        Player player = new Player(name, score);
        HighscoreService.savePlayerHighscore(player);
    }

    /**
     * Aktualisiert und zeichnet die Spieloberfläche neu.
     */
    private void refreshGameBoard() {
        if (isGamePaused) {
            displayPausedState();
            return;
        }

        Platform.runLater(() -> {
            try {
                snake.updateSnakePosition();
                LOG.debug("Schlangen-Position aktualisiert. Neuer Kopf: ({}, {})",
                        snake.getSegments().get(0).getX(), snake.getSegments().get(0).getY());
                snake.checkForCollisions(innerWall);
                if (snake.isAlive()) {
                    handleFoodGeneration();
                    gc.clearRect(0, 0, gameBoardCanvas.getWidth(), gameBoardCanvas.getHeight());
                    drawGameBoard(gc);
                    drawWalls(gc);
                    drawSnake(gc);

                    handleFoodConsumption();
                } else {
                    endCurrentGame();
                }
            } catch (Exception ex) {
                LOG.error("Fehler beim Aktualisieren des Spielbretts", ex);
            }
        });
    }

    /**
     * Displays the paused state on the game board.
     */
    private void displayPausedState() {
        gc.setFill(Color.WHITE);
        gc.fillRect(OBJECT_SIZE_MEDIUM * 0.3, GAME_BOARD_SIZE_MEDIUM - OBJECT_SIZE_MEDIUM * 0.9, OBJECT_SIZE_MEDIUM * 2.7, OBJECT_SIZE_MEDIUM * 0.8);
        gc.setFont(new Font(OBJECT_SIZE_MEDIUM * 0.6));
        gc.setFill(Color.BLACK);
        gc.fillText("Paused!", OBJECT_SIZE_MEDIUM * 0.6, GAME_BOARD_SIZE_MEDIUM - OBJECT_SIZE_MEDIUM * 0.3, GAME_BOARD_SIZE_MEDIUM);
    }

    /**
     * Handles the generation of new food items.
     */
    private void handleFoodGeneration() {
        if (foodManager.getRegularFood() == null) {
            foodManager.generateRegularFood();
        }
        if (foodManager.shouldGenerateSpecialFood()) {
            foodManager.generateSpecialFood();
        }
    }

    /**
     * Handles the consumption of food by the snake.
     */
    private void handleFoodConsumption() {
        Food regular = foodManager.getRegularFood();
        if (checkIfSnakeHeadIsOnFood(regular)) {
            snake.eats(regular);
            score += regular.getScoreValue();
            foodManager.handleFoodConsumption(regular.getScoreValue());
            StateManager.getScoreBoard().drawScoreBoard(this.getScore());
            regularFood = null;
        } else {
            drawFood(gc, regular);
        }

        Food special = foodManager.getSpecialFood();
        if (special != null) {
            if (checkIfSnakeHeadIsOnFood(special)) {
                snake.eats(special);
                score += special.getScoreValue();
                foodManager.handleFoodConsumption(special.getScoreValue());
                StateManager.getScoreBoard().drawScoreBoard(this.getScore());
                foodManager.resetSpecialFood();
            } else {
                special.decreaseSpecialFoodTimeToLive();
                if (special.getSpecialFoodTimeToLive() == 0) {
                    foodManager.resetSpecialFood();
                } else {
                    drawFood(gc, special);
                }
            }
        }
    }
}