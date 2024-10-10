package com.socslingo.dataAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

import com.socslingo.managers.DatabaseManager;
import com.socslingo.models.Deck;
import com.socslingo.models.Flashcard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeckDataAccess {

    private static final Logger logger = LoggerFactory.getLogger(DeckDataAccess.class);

    private DatabaseManager databaseManager;

    public DeckDataAccess(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        logger.info("DeckDataAccess initialized with DatabaseManager");
    }

    // ----------------------
    // New Deck Methods
    // ----------------------

    /**
     * Creates a new flashcard deck for a user.
     *
     * @param userId      ID of the user creating the deck.
     * @param deckName    Name of the deck.
     * @param createdDate Date when the deck was created.
     * @return The deck ID of the newly created deck, or -1 if creation failed.
     */
    public int createDeck(int userId, String deckName, String createdDate) {
        String sql = "INSERT INTO flashcard_decks_table(user_id, deck_name, created_date) VALUES(?, ?, ?)";

        logger.debug("Creating deck for userId: {}, deckName: '{}', createdDate: '{}'", userId, deckName, createdDate);
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, deckName);
            pstmt.setString(3, createdDate);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                logger.error("Creating deck failed, no rows affected for deckName: '{}'", deckName);
                throw new SQLException("Creating deck failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int deckId = generatedKeys.getInt(1);
                    logger.info("Deck created with ID: {}", deckId);
                    return deckId;
                } else {
                    logger.error("Creating deck failed, no ID obtained for deckName: '{}'", deckName);
                    throw new SQLException("Creating deck failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            logger.error("Error creating deck for userId: {}, deckName: '{}'", userId, deckName, e);
            return -1;
        }
    }

    /**
     * Retrieves all decks for a specific user.
     *
     * @param userId ID of the user.
     * @return List of Deck objects.
     */
    public List<Deck> getUserDecks(int userId) {
        List<Deck> decks = new ArrayList<>();
        String sql = "SELECT deck_id, deck_name, created_date FROM flashcard_decks_table WHERE user_id = ?";

        logger.debug("Retrieving decks for userId: {}", userId);
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int deckId = rs.getInt("deck_id");
                String deckName = rs.getString("deck_name");
                String createdDate = rs.getString("created_date");
                decks.add(new Deck(deckId, userId, deckName, createdDate));
                logger.debug("Deck retrieved: ID={}, Name='{}', CreatedDate='{}'", deckId, deckName, createdDate);
            }

            logger.info("Total decks retrieved for userId {}: {}", userId, decks.size());

        } catch (SQLException e) {
            logger.error("Error retrieving decks for userId: {}", userId, e);
        }
        return decks;
    }

    /**
     * Updates the name of an existing deck.
     *
     * @param deckId      ID of the deck to update.
     * @param newDeckName New name for the deck.
     * @return true if update was successful, false otherwise.
     */
    public boolean updateDeck(int deckId, String newDeckName) {
        String sql = "UPDATE flashcard_decks_table SET deck_name = ? WHERE deck_id = ?";

        logger.debug("Updating deckId: {} with newDeckName: '{}'", deckId, newDeckName);
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newDeckName);
            pstmt.setInt(2, deckId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("DeckId: {} updated successfully with new name: '{}'", deckId, newDeckName);
                return true;
            } else {
                logger.warn("No deck found with deckId: {}", deckId);
                return false;
            }

        } catch (SQLException e) {
            logger.error("Error updating deckId: {}", deckId, e);
            return false;
        }
    }

    /**
     * Deletes a flashcard deck and its associations.
     *
     * @param deckId ID of the deck to delete.
     * @return true if deletion was successful, false otherwise.
     */
    public boolean deleteDeck(int deckId) {
        String sql = "DELETE FROM flashcard_decks_table WHERE deck_id = ?";

        logger.debug("Deleting deckId: {}", deckId);
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, deckId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("DeckId: {} deleted successfully.", deckId);
                return true;
            } else {
                logger.warn("No deck found with deckId: {}", deckId);
                return false;
            }

        } catch (SQLException e) {
            logger.error("Error deleting deckId: {}", deckId, e);
            return false;
        }
    }

    /**
     * Associates a flashcard with a deck.
     *
     * @param deckId      ID of the deck.
     * @param flashcardId ID of the flashcard.
     * @return true if association was successful, false otherwise.
     */
    public boolean addFlashcardToDeck(int deckId, int flashcardId) {
        String sql = "INSERT INTO deck_flashcards_table(deck_id, flashcard_id) VALUES(?, ?)";

        logger.debug("Associating flashcardId: {} with deckId: {}", flashcardId, deckId);
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, deckId);
            pstmt.setInt(2, flashcardId);
            pstmt.executeUpdate();
            logger.info("FlashcardId: {} added to DeckId: {}", flashcardId, deckId);
            return true;

        } catch (SQLException e) {
            logger.error("Error adding flashcardId: {} to deckId: {}", flashcardId, deckId, e);
            return false;
        }
    }

    /**
     * Removes a flashcard from a deck.
     *
     * @param deckId      ID of the deck.
     * @param flashcardId ID of the flashcard.
     * @return true if removal was successful, false otherwise.
     */
    public boolean removeFlashcardFromDeck(int deckId, int flashcardId) {
        String sql = "DELETE FROM deck_flashcards_table WHERE deck_id = ? AND flashcard_id = ?";

        logger.debug("Removing flashcardId: {} from deckId: {}", flashcardId, deckId);
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, deckId);
            pstmt.setInt(2, flashcardId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("FlashcardId: {} removed from DeckId: {}", flashcardId, deckId);
                return true;
            } else {
                logger.warn("No association found between deckId: {} and flashcardId: {}", deckId, flashcardId);
                return false;
            }

        } catch (SQLException e) {
            logger.error("Error removing flashcardId: {} from deckId: {}", flashcardId, deckId, e);
            return false;
        }
    }

    /**
     * Retrieves all flashcards in a specific deck.
     *
     * @param deckId ID of the deck.
     * @return List of Flashcard objects in the deck.
     */
    public List<Flashcard> getFlashcardsInDeck(int deckId) {
        List<Flashcard> flashcards = new ArrayList<>();
        String sql = "SELECT f.flashcard_id, f.front_text, f.back_text FROM flashcards_table f " +
                     "JOIN deck_flashcards_table df ON f.flashcard_id = df.flashcard_id " +
                     "WHERE df.deck_id = ?";

        logger.debug("Retrieving flashcards for deckId: {}", deckId);
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, deckId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int flashcardId = rs.getInt("flashcard_id");
                String frontText = rs.getString("front_text");
                String backText = rs.getString("back_text");
                flashcards.add(new Flashcard(flashcardId, frontText, backText));
                logger.debug("Flashcard retrieved: ID={}, Front='{}', Back='{}'", flashcardId, frontText, backText);
            }

            logger.info("Total flashcards retrieved for deckId {}: {}", deckId, flashcards.size());

        } catch (SQLException e) {
            logger.error("Error retrieving flashcards for deckId: {}", deckId, e);
        }

        return flashcards;
    }

    /**
     * Retrieves all flashcards not associated with any deck for a user.
     *
     * @param userId ID of the user.
     * @return List of Flashcard objects not in any deck.
     */
    public List<Flashcard> getFlashcardsNotInAnyDeck1(int userId) {
        List<Flashcard> flashcards = new ArrayList<>();
        String sql = "SELECT f.flashcard_id, f.front_text, f.back_text FROM flashcards_table f " +
                     "LEFT JOIN deck_flashcards_table df ON f.flashcard_id = df.flashcard_id " +
                     "WHERE f.user_id = ? AND df.deck_id IS NULL";

        logger.debug("Retrieving flashcards not in any deck for userId: {}", userId);
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int flashcardId = rs.getInt("flashcard_id");
                String frontText = rs.getString("front_text");
                String backText = rs.getString("back_text");
                flashcards.add(new Flashcard(flashcardId, frontText, backText));
                logger.debug("Flashcard not in any deck retrieved: ID={}, Front='{}', Back='{}'", flashcardId, frontText, backText);
            }

            logger.info("Total flashcards not in any deck retrieved for userId {}: {}", userId, flashcards.size());

        } catch (SQLException e) {
            logger.error("Error retrieving flashcards not in any deck for userId: {}", userId, e);
        }

        return flashcards;
    }

public List<Flashcard> getFlashcardsNotInDeck(int userId, int deckId) {
    List<Flashcard> flashcards = new ArrayList<>();
    String sql = "SELECT f.flashcard_id, f.front_text, f.back_text FROM flashcards_table f " +
                 "WHERE f.user_id = ? AND f.flashcard_id NOT IN ( " +
                 "SELECT flashcard_id FROM deck_flashcards_table WHERE deck_id = ?)";
    
    logger.debug("Retrieving flashcards not in deckId: {} for userId: {}", deckId, userId);
    try (Connection conn = databaseManager.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, userId);
        pstmt.setInt(2, deckId);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            int flashcardId = rs.getInt("flashcard_id");
            String frontText = rs.getString("front_text");
            String backText = rs.getString("back_text");
            flashcards.add(new Flashcard(flashcardId, frontText, backText));
            logger.debug("Flashcard not in deck retrieved: ID={}, Front='{}', Back='{}'", flashcardId, frontText, backText);
        }

        logger.info("Total flashcards not in deckId {} retrieved for userId {}: {}", deckId, userId, flashcards.size());

    } catch (SQLException e) {
        logger.error("Error retrieving flashcards not in deckId: {} for userId: {}", deckId, userId, e);
    }
    return flashcards;
}
    // ----------------------
    // Additional Helper Methods (Optional)
    // ----------------------

    /**
     * Deletes all flashcards associated with a specific deck.
     *
     * @param deckId ID of the deck.
     * @return true if deletion was successful, false otherwise.
     */
    public boolean deleteAllFlashcardsInDeck(int deckId) {
        String sql = "DELETE FROM deck_flashcards_table WHERE deck_id = ?";

        logger.debug("Deleting all flashcards from deckId: {}", deckId);
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, deckId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("All flashcards removed from deckId: {}", deckId);
                return true;
            } else {
                logger.warn("No flashcards found for deckId: {}", deckId);
                return false;
            }

        } catch (SQLException e) {
            logger.error("Error deleting flashcards from deckId: {}", deckId, e);
            return false;
        }
    }


}
