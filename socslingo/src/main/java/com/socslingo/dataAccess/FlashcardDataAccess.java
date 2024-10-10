package com.socslingo.dataAccess;

import com.socslingo.managers.DatabaseManager;
import com.socslingo.models.Flashcard;
import com.socslingo.models.Deck;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlashcardDataAccess {
    private static final Logger logger = LoggerFactory.getLogger(FlashcardDataAccess.class);

    private DatabaseManager databaseManager;

    public FlashcardDataAccess(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        logger.info("FlashcardDataAccess initialized with DatabaseManager");
    }

    // ----------------------
    // Existing Flashcard Methods
    // ----------------------

    /**
     * Inserts a new flashcard into the database.
     *
     * @param userId      ID of the user creating the flashcard.
     * @param frontText   Text on the front side of the flashcard.
     * @param backText    Text on the back side of the flashcard.
     * @param createdDate Date when the flashcard was created.
     */
    public void insertFlashcard(int userId, String frontText, String backText, String createdDate) {
        String sql = "INSERT INTO flashcards_table(user_id, front_text, back_text, created_date) VALUES(?, ?, ?, ?)";

        logger.debug("Inserting flashcard for userId: {}", userId);
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, frontText);
            pstmt.setString(3, backText);
            pstmt.setString(4, createdDate);
            pstmt.executeUpdate();
            logger.info("Flashcard inserted successfully for userId: {}", userId);

        } catch (SQLException e) {
            logger.error("Error inserting flashcard for userId: {}", userId, e);
        }
    }

    /**
     * Updates an existing flashcard in the database.
     *
     * @param id           ID of the flashcard to update.
     * @param newFrontText New text for the front side.
     * @param newBackText  New text for the back side.
     */
    public void updateFlashcard(int id, String newFrontText, String newBackText) {
        String sql = "UPDATE flashcards_table SET front_text = ?, back_text = ? WHERE flashcard_id = ?";

        logger.debug("Updating flashcardId: {}", id);
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newFrontText);
            pstmt.setString(2, newBackText);
            pstmt.setInt(3, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("FlashcardId: {} updated successfully", id);
            } else {
                logger.warn("No flashcard found with flashcardId: {}", id);
            }

        } catch (SQLException e) {
            logger.error("Error updating flashcardId: {}", id, e);
        }
    }

    /**
     * Retrieves all flashcards for a specific user.
     *
     * @param userId ID of the user.
     * @return List of Flashcard objects.
     */
    public List<Flashcard> retrieveAllFlashcards(int userId) {
        List<Flashcard> flashcards = new ArrayList<>();
        String sql = "SELECT flashcard_id, front_text, back_text FROM flashcards_table WHERE user_id = ?";

        logger.debug("Retrieving all flashcards for userId: {}", userId);
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("flashcard_id");
                String front = rs.getString("front_text");
                String back = rs.getString("back_text");
                flashcards.add(new Flashcard(id, front, back));
                logger.debug("Flashcard retrieved: ID={}, Front='{}'", id, front);
            }

            logger.info("Total flashcards retrieved for userId {}: {}", userId, flashcards.size());

        } catch (SQLException e) {
            logger.error("Error retrieving flashcards for userId: {}", userId, e);
        }
        return flashcards;
    }

    // You can add more methods with similar logging as needed
}
