package at.ac.fhcampuswien.snake;

import java.io.IOException;

import at.ac.fhcampuswien.snake.util.StateManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class SnakeApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        StateManager.initializeStage(stage);
        StateManager.switchToStartView();
    }

    public static void main(String[] args) {
        launch();
    }
}