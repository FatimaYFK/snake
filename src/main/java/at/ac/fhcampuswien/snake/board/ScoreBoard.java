package at.ac.fhcampuswien.snake.board;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import static at.ac.fhcampuswien.snake.util.Constants.*;

/**
 * Represents the scoreboard and countdown timer in the Snake game.
 */
public class ScoreBoard {
    private final GraphicsContext gc;

    /**
     * Initializes the ScoreBoard with the provided canvas.
     *
     * @param scoreBoardCanvas Canvas to draw the scoreboard and timer.
     */
    public ScoreBoard(Canvas scoreBoardCanvas) {
        this.gc = scoreBoardCanvas.getGraphicsContext2D();
    }

    /**
     * Draws the current score on the scoreboard.
     *
     * @param score The current score to display.
     */
    public void drawScoreBoard(int score) {
        gc.setFill(Color.web("4a148c"));
        gc.fillRect(0, 0, SCOREBOARD_WIDTH, SCOREBOARD_HEIGHT);

        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Courier", OBJECT_SIZE_MEDIUM));

        gc.fillText("Score: " + score, SCOREBOARD_WIDTH - 7, SCOREBOARD_HEIGHT / 2);
    }

    /**
     * Draws a countdown timer on the scoreboard.
     * Replaces the Thread-based implementation with a Timeline for better integration with JavaFX.
     */
    public void drawCountdownTimer() {
        final int duration = 3;

        Timeline countdownTimeline = new Timeline();
        countdownTimeline.setCycleCount(1);

        for (int i = duration; i >= 0; i--) {
            final int currentSecond = i;
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(duration - i), event -> {
                gc.setFill(Color.web("4a148c"));
                gc.fillRect(0, 0, SCOREBOARD_WIDTH / 2, SCOREBOARD_HEIGHT);

                if (currentSecond > 0) {
                    gc.setTextAlign(TextAlignment.LEFT);
                    gc.setTextBaseline(VPos.CENTER);
                    gc.setFill(Color.WHITE);
                    gc.setFont(Font.font("Courier", OBJECT_SIZE_MEDIUM));

                    String timerText = "Starting in: " + currentSecond;
                    gc.fillText(timerText, 7, SCOREBOARD_HEIGHT / 2);
                } else {
                    // Optionally, you can clear the timer or perform another action when countdown finishes
                }
            });
            countdownTimeline.getKeyFrames().add(keyFrame);
        }

        countdownTimeline.play();
    }
}