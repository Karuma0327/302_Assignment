package com.socslingo.dataAccess;

import java.sql.*;
import java.util.*;

import com.socslingo.managers.DatabaseManager;
import com.socslingo.models.*;

import org.slf4j.*;

public class DeckDataAccess {

    private static final Logger logger = LoggerFactory.getLogger(DeckDataAccess.class);

    private DatabaseManager database_manager;

    public DeckDataAccess(DatabaseManager database_manager) {
        this.database_manager = database_manager;
        logger.info("DeckDataAccess initialized with DatabaseManager");
    }

    public int createDeck(int user_id, String deck_name, String created_date) {
        String sql = "INSERT INTO flashcard_decks_table(user_id, deck_name, created_date) VALUES(?, ?, ?)";

        logger.debug("Creating deck for user_id: {}, deck_name: '{}', created_date: '{}'", user_id, deck_name, created_date);
        try (Connection connection = database_manager.getConnection();
             PreparedStatement prepared_statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            prepared_statement.setInt(1, user_id);
            prepared_statement.setString(2, deck_name);
            prepared_statement.setString(3, created_date);
            int affected_rows = prepared_statement.executeUpdate();

            if (affected_rows == 0) {
                logger.error("Creating deck failed, no rows affected for deck_name: '{}'", deck_name);
                throw new SQLException("Creating deck failed, no rows affected.");
            }

            try (ResultSet generated_keys = prepared_statement.getGeneratedKeys()) {
                if (generated_keys.next()) {
                    int deck_id = generated_keys.getInt(1);
                    logger.info("Deck created with ID: {}", deck_id);
                    return deck_id;
                } else {
                    logger.error("Creating deck failed, no ID obtained for deck_name: '{}'", deck_name);
                    throw new SQLException("Creating deck failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            logger.error("Error creating deck for user_id: {}, deck_name: '{}'", user_id, deck_name, e);
            return -1;
        }
    }

    public List<Deck> getUserDecks(int user_id) {
        List<Deck> decks = new ArrayList<>();
        String sql = "SELECT deck_id, deck_name, created_date FROM flashcard_decks_table WHERE user_id = ?";

        logger.debug("Retrieving decks for user_id: {}", user_id);
        try (Connection connection = database_manager.getConnection();
             PreparedStatement prepared_statement = connection.prepareStatement(sql)) {

            prepared_statement.setInt(1, user_id);
            ResultSet result_set = prepared_statement.executeQuery();

            while (result_set.next()) {
                int deck_id = result_set.getInt("deck_id");
                String deck_name = result_set.getString("deck_name");
                String created_date = result_set.getString("created_date");
                decks.add(new Deck(deck_id, user_id, deck_name, created_date));
                logger.debug("Deck retrieved: ID={}, Name='{}', CreatedDate='{}'", deck_id, deck_name, created_date);
            }

            logger.info("Total decks retrieved for user_id {}: {}", user_id, decks.size());

        } catch (SQLException e) {
            logger.error("Error retrieving decks for user_id: {}", user_id, e);
        }
        return decks;
    }



    public boolean updateDeck(int deck_id, String new_deck_name) {
        String sql = "UPDATE flashcard_decks_table SET deck_name = ? WHERE deck_id = ?";

        logger.debug("Updating deck_id: {} with new_deck_name: '{}'", deck_id, new_deck_name);
        try (Connection connection = database_manager.getConnection();
             PreparedStatement prepared_statement = connection.prepareStatement(sql)) {

            prepared_statement.setString(1, new_deck_name);
            prepared_statement.setInt(2, deck_id);
            int affected_rows = prepared_statement.executeUpdate();

            if (affected_rows > 0) {
                logger.info("Deck_id: {} updated successfully with new name: '{}'", deck_id, new_deck_name);
                return true;
            } else {
                logger.warn("No deck found with deck_id: {}", deck_id);
                return false;
            }

        } catch (SQLException e) {
            logger.error("Error updating deck_id: {}", deck_id, e);
            return false;
        }
    }

    public boolean deleteDeck(int deck_id) {
        String sql = "DELETE FROM flashcard_decks_table WHERE deck_id = ?";

        logger.debug("Deleting deck_id: {}", deck_id);
        try (Connection connection = database_manager.getConnection();
             PreparedStatement prepared_statement = connection.prepareStatement(sql)) {

            prepared_statement.setInt(1, deck_id);
            int affected_rows = prepared_statement.executeUpdate();

            if (affected_rows > 0) {
                logger.info("Deck_id: {} deleted successfully.", deck_id);
                return true;
            } else {
                logger.warn("No deck found with deck_id: {}", deck_id);
                return false;
            }

        } catch (SQLException e) {
            logger.error("Error deleting deck_id: {}", deck_id, e);
            return false;
        }
    }

    public boolean addFlashcardToDeck(int deck_id, int flashcard_id) {
        String sql = "INSERT INTO deck_flashcards_table(deck_id, flashcard_id) VALUES(?, ?)";

        logger.debug("Associating flashcard_id: {} with deck_id: {}", flashcard_id, deck_id);
        try (Connection connection = database_manager.getConnection();
             PreparedStatement prepared_statement = connection.prepareStatement(sql)) {

            prepared_statement.setInt(1, deck_id);
            prepared_statement.setInt(2, flashcard_id);
            prepared_statement.executeUpdate();
            logger.info("Flashcard_id: {} added to Deck_id: {}", flashcard_id, deck_id);
            return true;

        } catch (SQLException e) {
            logger.error("Error adding flashcard_id: {} to deck_id: {}", flashcard_id, deck_id, e);
            return false;
        }
    }

    public boolean removeFlashcardFromDeck(int deck_id, int flashcard_id) {
        String sql = "DELETE FROM deck_flashcards_table WHERE deck_id = ? AND flashcard_id = ?";

        logger.debug("Removing flashcard_id: {} from deck_id: {}", flashcard_id, deck_id);
        try (Connection connection = database_manager.getConnection();
             PreparedStatement prepared_statement = connection.prepareStatement(sql)) {

            prepared_statement.setInt(1, deck_id);
            prepared_statement.setInt(2, flashcard_id);
            int affected_rows = prepared_statement.executeUpdate();

            if (affected_rows > 0) {
                logger.info("Flashcard_id: {} removed from Deck_id: {}", flashcard_id, deck_id);
                return true;
            } else {
                logger.warn("No association found between deck_id: {} and flashcard_id: {}", deck_id, flashcard_id);
                return false;
            }

        } catch (SQLException e) {
            logger.error("Error removing flashcard_id: {} from deck_id: {}", flashcard_id, deck_id, e);
            return false;
        }
    }

    public List<Flashcard> getFlashcardsForDeck(int deck_id) {
        List<Flashcard> flashcards = new ArrayList<>();
        String sql = "SELECT f.flashcard_id, f.front_text, f.back_text FROM flashcards_table f " +
                    "JOIN deck_flashcards_table df ON f.flashcard_id = df.flashcard_id " +
                    "WHERE df.deck_id = ?";

        logger.debug("Retrieving flashcards for deck_id: {}", deck_id);
        try (Connection connection = database_manager.getConnection();
            PreparedStatement prepared_statement = connection.prepareStatement(sql)) {

            prepared_statement.setInt(1, deck_id);
            ResultSet result_set = prepared_statement.executeQuery();

            while (result_set.next()) {
                int flashcard_id = result_set.getInt("flashcard_id");
                String front_text = result_set.getString("front_text");
                String back_text = result_set.getString("back_text");
                flashcards.add(new Flashcard(flashcard_id, front_text, back_text));
                logger.debug("Flashcard retrieved: ID={}, Front='{}', Back='{}'", flashcard_id, front_text, back_text);
            }

            logger.info("Total flashcards retrieved for deck_id {}: {}", deck_id, flashcards.size());

        } catch (SQLException e) {
            logger.error("Error retrieving flashcards for deck_id: {}", deck_id, e);
        }

        return flashcards;
    }

    public List<Flashcard> getUnassignedFlashcardsForUser(int user_id) {
        List<Flashcard> flashcards = new ArrayList<>();
        String sql = "SELECT f.flashcard_id, f.front_text, f.back_text FROM flashcards_table f " +
                     "LEFT JOIN deck_flashcards_table df ON f.flashcard_id = df.flashcard_id " +
                     "WHERE f.user_id = ? AND df.deck_id IS NULL";

        logger.debug("Retrieving flashcards not in any deck for user_id: {}", user_id);
        try (Connection connection = database_manager.getConnection();
             PreparedStatement prepared_statement = connection.prepareStatement(sql)) {

            prepared_statement.setInt(1, user_id);
            ResultSet result_set = prepared_statement.executeQuery();

            while (result_set.next()) {
                int flashcard_id = result_set.getInt("flashcard_id");
                String front_text = result_set.getString("front_text");
                String back_text = result_set.getString("back_text");
                flashcards.add(new Flashcard(flashcard_id, front_text, back_text));
                logger.debug("Flashcard not in any deck retrieved: ID={}, Front='{}', Back='{}'", flashcard_id, front_text, back_text);
            }

            logger.info("Total flashcards not in any deck retrieved for user_id {}: {}", user_id, flashcards.size());

        } catch (SQLException e) {
            logger.error("Error retrieving flashcards not in any deck for user_id: {}", user_id, e);
        }

        return flashcards;
    }

    public List<Flashcard> getFlashcardsNotInDeck(int user_id, int deck_id) {
        List<Flashcard> flashcards = new ArrayList<>();
        String sql = "SELECT f.flashcard_id, f.front_text, f.back_text FROM flashcards_table f " +
                     "WHERE f.user_id = ? AND f.flashcard_id NOT IN ( " +
                     "SELECT flashcard_id FROM deck_flashcards_table WHERE deck_id = ?)";
    
        logger.debug("Retrieving flashcards not in deck_id: {} for user_id: {}", deck_id, user_id);
        try (Connection connection = database_manager.getConnection();
             PreparedStatement prepared_statement = connection.prepareStatement(sql)) {

            prepared_statement.setInt(1, user_id);
            prepared_statement.setInt(2, deck_id);
            ResultSet result_set = prepared_statement.executeQuery();

            while (result_set.next()) {
                int flashcard_id = result_set.getInt("flashcard_id");
                String front_text = result_set.getString("front_text");
                String back_text = result_set.getString("back_text");
                flashcards.add(new Flashcard(flashcard_id, front_text, back_text));
                logger.debug("Flashcard not in deck retrieved: ID={}, Front='{}', Back='{}'", flashcard_id, front_text, back_text);
            }

            logger.info("Total flashcards not in deck_id {} retrieved for user_id {}: {}", deck_id, user_id, flashcards.size());

        } catch (SQLException e) {
            logger.error("Error retrieving flashcards not in deck_id: {} for user_id: {}", deck_id, user_id, e);
        }
        return flashcards;
    }

    public boolean deleteAllFlashcardsInDeck(int deck_id) {
        String sql = "DELETE FROM deck_flashcards_table WHERE deck_id = ?";

        logger.debug("Deleting all flashcards from deck_id: {}", deck_id);
        try (Connection connection = database_manager.getConnection();
             PreparedStatement prepared_statement = connection.prepareStatement(sql)) {

            prepared_statement.setInt(1, deck_id);
            int affected_rows = prepared_statement.executeUpdate();

            if (affected_rows > 0) {
                logger.info("All flashcards removed from deck_id: {}", deck_id);
                return true;
            } else {
                logger.warn("No flashcards found for deck_id: {}", deck_id);
                return false;
            }

        } catch (SQLException e) {
            logger.error("Error deleting flashcards from deck_id: {}", deck_id, e);
            return false;
        }
    }
}
