package com.socslingo.dao;

import com.socslingo.managers.DatabaseManager;
import com.socslingo.models.Flashcard;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) class for managing Flashcard entities in the database.
 * <p>
 * This class provides methods to insert, update, and retrieve flashcards associated with a user.
 * It interacts with the {@code DatabaseManager} to establish database connections and execute SQL queries.
 * </p>
 * 
 * @author TEAM-SOCSLINGO 
 * @version 1.0
 * @since 2024-09-18
 */
public class FlashcardDAO {
    private DatabaseManager databaseManager;

    /**
     * Constructs a new {@code FlashcardDAO} with the specified {@code DatabaseManager}.
     * 
     * @param databaseManager the manager responsible for handling database connections
     */
    public FlashcardDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Inserts a new flashcard into the database.
     * 
     * @param userId      the ID of the user who owns the flashcard
     * @param frontText   the text displayed on the front side of the flashcard
     * @param backText    the text displayed on the back side of the flashcard
     * @param createdDate the date when the flashcard was created
     */
    public void insertFlashcard(int userId, String frontText, String backText, String createdDate) {
        String sql = "INSERT INTO flashcards_table(user_id, front_text, back_text, created_date) VALUES(?, ?, ?, ?)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, frontText);
            pstmt.setString(3, backText);
            pstmt.setString(4, createdDate);
            pstmt.executeUpdate();
            System.out.println("Flashcard inserted successfully.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Updates an existing flashcard in the database.
     * 
     * @param id           the ID of the flashcard to update
     * @param newFrontText the new text for the front side of the flashcard
     * @param newBackText  the new text for the back side of the flashcard
     */
    public void updateFlashcard(int id, String newFrontText, String newBackText) {
        String sql = "UPDATE flashcards_table SET front_text = ?, back_text = ? WHERE flashcard_id = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newFrontText);
            pstmt.setString(2, newBackText);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
            System.out.println("Flashcard updated successfully.");

        } catch (SQLException e) {
            System.out.println("Error updating flashcard: " + e.getMessage());
        }
    }

    /**
     * Retrieves all flashcards associated with a specific user from the database.
     * 
     * @param userId the ID of the user whose flashcards are to be retrieved
     * @return a {@code List} of {@code Flashcard} objects belonging to the user
     */
    public List<Flashcard> retrieveAllFlashcards(int userId) {
        List<Flashcard> flashcards = new ArrayList<>();
        String sql = "SELECT flashcard_id, front_text, back_text FROM flashcards_table WHERE user_id = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("flashcard_id");
                String front = rs.getString("front_text");
                String back = rs.getString("back_text");
                flashcards.add(new Flashcard(id, front, back));
                System.out.println("Flashcard retrieved: " + front);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving flashcards: " + e.getMessage());
        }
        return flashcards;
    }
}
