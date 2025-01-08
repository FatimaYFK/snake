module at.ac.fhcampuswien.snake {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires javafx.media;
    requires transitive javafx.graphics;

    opens at.ac.fhcampuswien.snake to javafx.fxml;

    exports at.ac.fhcampuswien.snake;
    exports at.ac.fhcampuswien.snake.controller;
    exports at.ac.fhcampuswien.snake.util;
    exports at.ac.fhcampuswien.snake.board;
    exports at.ac.fhcampuswien.snake.manager;
    exports at.ac.fhcampuswien.snake.ingameobjects;

    opens at.ac.fhcampuswien.snake.controller to javafx.fxml;
    opens at.ac.fhcampuswien.snake.util to javafx.fxml;
    opens at.ac.fhcampuswien.snake.board to javafx.fxml;
    opens at.ac.fhcampuswien.snake.manager to javafx.fxml;
}