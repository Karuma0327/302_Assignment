package com.socslingo.dataAccess;

import com.socslingo.models.CharacterRecognitionStatistics;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.sql.*;

public class CharacterRecognitionStatisticsDAO {
    private static final Logger logger = LoggerFactory.getLogger(CharacterRecognitionStatisticsDAO.class);
    private static final String DATABASE_PATH = "src/main/database/socslingo_database.db";
    private static final String DATABASE_URL = "jdbc:sqlite:" + DATABASE_PATH;

    // Singleton instance
    private static CharacterRecognitionStatisticsDAO instance = null;

    private CharacterRecognitionStatisticsDAO() {
        // Private constructor to enforce singleton pattern
    }

    public static CharacterRecognitionStatisticsDAO getInstance() {
        if (instance == null) {
            instance = new CharacterRecognitionStatisticsDAO();
        }
        return instance;
    }

    /**
     * Retrieves the statistics for a given user ID.
     *
     * @param userId The user ID.
     * @return The CharacterRecognitionStatistics object or null if not found.
     */
    public CharacterRecognitionStatistics getStatisticsByUserId(int userId) {
        String query = "SELECT * FROM character_recognition_statistics WHERE user_id = ?";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                CharacterRecognitionStatistics stats = new CharacterRecognitionStatistics();
                stats.setStatId(rs.getInt("stat_id"));
                stats.setUserId(rs.getInt("user_id"));
                stats.setCharactersCorrect(rs.getInt("characters_correct"));
                stats.setCharactersIncorrect(rs.getInt("characters_incorrect"));
                stats.setCharactersAccuracy(rs.getDouble("characters_accuracy"));
                // Updated parsing with formatter
                stats.setLastUpdated(LocalDateTime.parse(rs.getString("last_updated"), formatter));
                return stats;
            }

        } catch (SQLException e) {
            logger.error("Error retrieving statistics for user ID {}: {}", userId, e.getMessage());
        } catch (DateTimeParseException e) {
            logger.error("Error parsing last_updated for user ID {}: {}", userId, e.getMessage());
        }

        return null;
    }

    /**
     * Creates a new statistics record for a user.
     *
     * @param userId The user ID.
     * @return True if created successfully, false otherwise.
     */
    public boolean createStatistics(int userId) {
        String insert = "INSERT INTO character_recognition_statistics (user_id, characters_correct, characters_incorrect, characters_accuracy, last_updated) VALUES (?, 0, 0, 0.00, CURRENT_TIMESTAMP)";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = connection.prepareStatement(insert)) {

            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Statistics created for user ID {}", userId);
                return true;
            }

        } catch (SQLException e) {
            logger.error("Error creating statistics for user ID {}: {}", userId, e.getMessage());
        }

        return false;
    }

    /**
     * Updates the statistics record.
     *
     * @param stats The CharacterRecognitionStatistics object with updated values.
     * @return True if updated successfully, false otherwise.
     */
    public boolean updateStatistics(CharacterRecognitionStatistics stats) {
        String update = "UPDATE character_recognition_statistics SET characters_correct = ?, characters_incorrect = ?, characters_accuracy = ?, last_updated = CURRENT_TIMESTAMP WHERE user_id = ?";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = connection.prepareStatement(update)) {

            pstmt.setInt(1, stats.getCharactersCorrect());
            pstmt.setInt(2, stats.getCharactersIncorrect());
            pstmt.setDouble(3, stats.getCharactersAccuracy());
            pstmt.setInt(4, stats.getUserId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Statistics updated for user ID {}", stats.getUserId());
                return true;
            }

        } catch (SQLException e) {
            logger.error("Error updating statistics for user ID {}: {}", stats.getUserId(), e.getMessage());
        }

        return false;
    }

    /**
     * Increments the correct answer count for a user.
     *
     * @param userId The user ID.
     * @return True if incremented successfully, false otherwise.
     */
    public boolean incrementCorrect(int userId) {
        String update = "UPDATE character_recognition_statistics SET characters_correct = characters_correct + 1, characters_accuracy = ((characters_correct + 1) * 1.0) / (characters_correct + characters_incorrect + 1) * 100.0, last_updated = CURRENT_TIMESTAMP WHERE user_id = ?";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = connection.prepareStatement(update)) {

            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Incremented correct count for user ID {}", userId);
                return true;
            }

        } catch (SQLException e) {
            logger.error("Error incrementing correct count for user ID {}: {}", userId, e.getMessage());
        }

        return false;
    }

    /**
     * Increments the incorrect answer count for a user.
     *
     * @param userId The user ID.
     * @return True if incremented successfully, false otherwise.
     */
    public boolean incrementIncorrect(int userId) {
        String update = "UPDATE character_recognition_statistics SET characters_incorrect = characters_incorrect + 1, characters_accuracy = (characters_correct * 1.0) / (characters_correct + characters_incorrect + 1) * 100.0, last_updated = CURRENT_TIMESTAMP WHERE user_id = ?";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = connection.prepareStatement(update)) {

            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Incremented incorrect count for user ID {}", userId);
                return true;
            }

        } catch (SQLException e) {
            logger.error("Error incrementing incorrect count for user ID {}: {}", userId, e.getMessage());
        }

        return false;
    }
}