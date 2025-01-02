package at.ac.fhcampuswien.snake.service;

import at.ac.fhcampuswien.snake.util.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Utility class to manage high scores by reading from and writing to a text file.
 */
public class HighscoreService {

    private static final Logger LOG = LoggerFactory.getLogger(HighscoreService.class);
    private static final int MAX_HIGHSCORES = 5;
    private static final String HIGHSCORES_FILE_PATH = "src/main/resources/highscores.txt";
    private static final String HIGHSCORE_SEPARATOR = ":"; // Assuming ':' as separator

    /**
     * Retrieves the high scores from the file.
     *
     * @return List of top players sorted by score in descending order.
     */
    public static List<Player> getSavedPlayerList() {
        try {
            ensureHighscoresFileExists();
            return Files.lines(Paths.get(HIGHSCORES_FILE_PATH))
                    .map(HighscoreService::parsePlayer)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .sorted(Comparator.comparingInt(Player::getScore).reversed())
                    .limit(MAX_HIGHSCORES)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            LOG.error("Error retrieving high scores", e);
            return List.of();
        }
    }

    /**
     * Saves a player's high score, maintaining only the top five scores.
     *
     * @param player The player to save.
     */
    public static void savePlayerHighscore(Player player) {
        try {
            ensureHighscoresFileExists();
            List<Player> players = getSavedPlayerList();
            players = players.stream()
                    .filter(p -> !p.getName().equals(player.getName()))
                    .collect(Collectors.toList());
            players.add(player);
            players = players.stream()
                    .sorted(Comparator.comparingInt(Player::getScore).reversed())
                    .limit(MAX_HIGHSCORES)
                    .collect(Collectors.toList());
            writePlayersToFile(players);
        } catch (IOException e) {
            LOG.error("Error saving high score for player: " + player.getName(), e);
        }
    }

    /**
     * Ensures that the high scores file exists; creates it if it does not.
     *
     * @throws IOException if the file cannot be created.
     */
    private static void ensureHighscoresFileExists() throws IOException {
        File file = new File(HIGHSCORES_FILE_PATH);
        if (!file.exists()) {
            boolean created = file.createNewFile();
            if (!created) {
                throw new IOException("Failed to create highscore file at " + HIGHSCORES_FILE_PATH);
            }
        }
    }

    /**
     * Parses a line from the high scores file into a Player object.
     *
     * @param line The line to parse.
     * @return Optional containing the Player if parsing is successful.
     */
    private static Optional<Player> parsePlayer(String line) {
        String[] parts = line.split(HIGHSCORE_SEPARATOR);
        if (parts.length == 2) {
            try {
                String name = parts[0].trim();
                int score = Integer.parseInt(parts[1].trim());
                return Optional.of(new Player(name, score));
            } catch (NumberFormatException e) {
                LOG.warn("Invalid score format in line: {}", line, e);
            }
        } else {
            LOG.warn("Invalid line format: {}", line);
        }
        return Optional.empty();
    }

    /**
     * Writes the list of players to the high scores file.
     *
     * @param players The list of players to write.
     * @throws IOException if an I/O error occurs.
     */
    private static void writePlayersToFile(List<Player> players) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(HIGHSCORES_FILE_PATH))) {
            for (Player player : players) {
                writer.write(player.getName() + HIGHSCORE_SEPARATOR + player.getScore());
                writer.newLine();
            }
        } catch (IOException e) {
            LOG.error("Error writing high scores to file", e);
            throw e;
        }
    }
}