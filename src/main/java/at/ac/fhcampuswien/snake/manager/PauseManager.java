package at.ac.fhcampuswien.snake.manager;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

/**
 * Manages the pause functionality of the game.
 */
public class PauseManager {

    private final PauseTransition pauseTransition;
    private boolean isGamePaused;

    public PauseManager() {
        this.pauseTransition = new PauseTransition(Duration.seconds(3));
        this.isGamePaused = false;
    }

    /**
     * Initiates the pause.
     *
     * @param onPauseComplete Callback to execute after the pause.
     */
    public void pauseGame(Runnable onPauseComplete) {
        this.isGamePaused = true;
        pauseTransition.setOnFinished(event -> {
            this.isGamePaused = false;
            onPauseComplete.run();
        });
        pauseTransition.play();
    }

    /**
     * Toggles the pause state.
     */
    public void togglePause(Runnable onPauseComplete) {
        if (isGamePaused) {
            // Resume the game
            isGamePaused = false;
            pauseTransition.stop();
            onPauseComplete.run();
        } else {
            // Pause the game
            isGamePaused = true;
            pauseGame(onPauseComplete);
        }
    }

    /**
     * Checks if the game is currently paused.
     *
     * @return true if paused, false otherwise.
     */
    public boolean isGamePaused() {
        return isGamePaused;
    }

    /**
     * Stops any ongoing pause.
     */
    public void stopPause() {
        pauseTransition.stop();
        this.isGamePaused = false;
    }
}