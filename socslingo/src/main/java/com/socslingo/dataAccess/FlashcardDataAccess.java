package com.socslingo.dataAccess;

import com.socslingo.managers.DatabaseManager;
import com.socslingo.models.Flashcard;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlashcardDataAccess {
    private static final Logger logger = LoggerFactory.getLogger(FlashcardDataAccess.class);

    private DatabaseManager database_manager;

    public FlashcardDataAccess(DatabaseManager database_manager) {
        this.database_manager = database_manager;
        logger.info("FlashcardDataAccess initialized with DatabaseManager");
    }

    public void insertFlashcard(int user_id, String front_text, String back_text, String created_date) {
        String sql = "INSERT INTO flashcards_table(user_id, front_text, back_text, created_date) VALUES(?, ?, ?, ?)";

        logger.debug("Inserting flashcard for user_id: {}", user_id);
        try (Connection conn = database_manager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user_id);
            pstmt.setString(2, front_text);
            pstmt.setString(3, back_text);
            pstmt.setString(4, created_date);
            pstmt.executeUpdate();
            logger.info("Flashcard inserted successfully for user_id: {}", user_id);

        } catch (SQLException e) {
            logger.error("Error inserting flashcard for user_id: {}", user_id, e);
        }
    }

    public void updateFlashcard(int id, String new_front_text, String new_back_text) {
        String sql = "UPDATE flashcards_table SET front_text = ?, back_text = ? WHERE flashcard_id = ?";

        logger.debug("Updating flashcard_id: {}", id);
        try (Connection conn = database_manager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, new_front_text);
            pstmt.setString(2, new_back_text);
            pstmt.setInt(3, id);
            int rows_affected = pstmt.executeUpdate();
            if (rows_affected > 0) {
                logger.info("Flashcard_id: {} updated successfully", id);
            } else {
                logger.warn("No flashcard found with flashcard_id: {}", id);
            }

        } catch (SQLException e) {
            logger.error("Error updating flashcard_id: {}", id, e);
        }
    }

    public List<Flashcard> retrieveAllFlashcards(int user_id) {
        List<Flashcard> flashcards = new ArrayList<>();
        String sql = "SELECT flashcard_id, front_text, back_text FROM flashcards_table WHERE user_id = ?";

        logger.debug("Retrieving all flashcards for user_id: {}", user_id);
        try (Connection conn = database_manager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user_id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("flashcard_id");
                String front = rs.getString("front_text");
                String back = rs.getString("back_text");
                flashcards.add(new Flashcard(id, front, back));
                logger.debug("Flashcard retrieved: ID={}, Front='{}'", id, front);
            }

            logger.info("Total flashcards retrieved for user_id {}: {}", user_id, flashcards.size());

        } catch (SQLException e) {
            logger.error("Error retrieving flashcards for user_id: {}", user_id, e);
        }
        return flashcards;
    }

    public Flashcard retrieveFlashcardById(int flashcard_id) {
        String sql = "SELECT flashcard_id, front_text, back_text FROM flashcards_table WHERE flashcard_id = ?";
        try (Connection conn = database_manager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, flashcard_id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String front_text = rs.getString("front_text");
                String back_text = rs.getString("back_text");
                Flashcard flashcard = new Flashcard(flashcard_id, front_text, back_text);
                logger.debug("Retrieved Flashcard: {}", flashcard);
                return flashcard;
            }
        } catch (SQLException e) {
            logger.error("Error retrieving flashcard with id: {}", flashcard_id, e);
        }
        logger.warn("Flashcard with id {} not found.", flashcard_id);
        return null;
    }
}
