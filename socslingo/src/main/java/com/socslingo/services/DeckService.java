package com.socslingo.services;

import com.socslingo.dataAccess.DeckDataAccess;
import com.socslingo.models.Deck;
import com.socslingo.models.Flashcard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DeckService {
    private static final Logger logger = LoggerFactory.getLogger(DeckService.class);
    private final DeckDataAccess deck_data_access;

    public DeckService(DeckDataAccess deck_data_access) {
        this.deck_data_access = deck_data_access;
    }

    public Deck getDeckByName(int userId, String deckName) {
        try {
            List<Deck> userDecks = deck_data_access.getUserDecks(userId);
            for (Deck deck : userDecks) {
                if (deck.getDeckName().equals(deckName)) {
                    return deck;
                }
            }
            return null;
        } catch (Exception e) {
            logger.error("Exception occurred while retrieving deck '{}' for userId: {}", deckName, userId, e);
            throw new RuntimeException("Failed to retrieve deck by name.", e);
        }
    }
    public Deck createDeck(int user_id, String deck_name) {
        if (deck_name == null || deck_name.trim().isEmpty()) {
            logger.warn("Attempted to create a deck with an empty name.");
            throw new IllegalArgumentException("Deck name cannot be empty.");
        }

        String created_date = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        try {
            int deck_id = deck_data_access.createDeck(user_id, deck_name, created_date);
            if (deck_id != -1) {
                logger.info("Deck '{}' created successfully with ID: {}", deck_name, deck_id);
                return new Deck(deck_id, user_id, deck_name, created_date);
            } else {
                logger.error("Failed to create deck '{}'", deck_name);
                return null;
            }
        } catch (Exception e) {
            logger.error("Exception occurred while creating deck '{}'", deck_name, e);
            throw new RuntimeException("Failed to create deck.", e);
        }
    }

    public boolean deleteDeck(int deck_id) {
        try {
            boolean success = deck_data_access.deleteDeck(deck_id);
            if (success) {
                logger.info("DeckId: {} deleted successfully.", deck_id);
            } else {
                logger.error("Failed to delete DeckId: {}", deck_id);
            }
            return success;
        } catch (Exception e) {
            logger.error("Exception occurred while deleting DeckId: {}", deck_id, e);
            throw new RuntimeException("Failed to delete deck.", e);
        }
    }

    public boolean updateDeckName(int deck_id, String new_deck_name) {
        if (new_deck_name == null || new_deck_name.trim().isEmpty()) {
            logger.warn("Attempted to update deck with an empty name.");
            throw new IllegalArgumentException("Deck name cannot be empty.");
        }

        try {
            boolean success = deck_data_access.updateDeck(deck_id, new_deck_name);
            if (success) {
                logger.info("DeckId: {} updated successfully to '{}'.", deck_id, new_deck_name);
            } else {
                logger.error("Failed to update DeckId: {}", deck_id);
            }
            return success;
        } catch (Exception e) {
            logger.error("Exception occurred while updating DeckId: {}", deck_id, e);
            throw new RuntimeException("Failed to update deck.", e);
        }
    }

    public List<Deck> getUserDecks(int user_id) {
        try {
            List<Deck> decks = deck_data_access.getUserDecks(user_id);
            logger.info("Retrieved {} decks for userId: {}", decks.size(), user_id);
            return decks;
        } catch (Exception e) {
            logger.error("Exception occurred while retrieving decks for userId: {}", user_id, e);
            throw new RuntimeException("Failed to retrieve decks.", e);
        }
    }

    public boolean addFlashcardToDeck(int deck_id, int flashcard_id) {
        try {
            boolean success = deck_data_access.addFlashcardToDeck(deck_id, flashcard_id);
            if (success) {
                logger.info("FlashcardId: {} added to DeckId: {} successfully.", flashcard_id, deck_id);
            } else {
                logger.error("Failed to add FlashcardId: {} to DeckId: {}", flashcard_id, deck_id);
            }
            return success;
        } catch (Exception e) {
            logger.error("Exception occurred while adding FlashcardId: {} to DeckId: {}", flashcard_id, deck_id, e);
            throw new RuntimeException("Failed to add flashcard to deck.", e);
        }
    }

    public boolean removeFlashcardFromDeck(int deck_id, int flashcard_id) {
        try {
            boolean success = deck_data_access.removeFlashcardFromDeck(deck_id, flashcard_id);
            if (success) {
                logger.info("FlashcardId: {} removed from DeckId: {} successfully.", flashcard_id, deck_id);
            } else {
                logger.error("Failed to remove FlashcardId: {} from DeckId: {}", flashcard_id, deck_id);
            }
            return success;
        } catch (Exception e) {
            logger.error("Exception occurred while removing FlashcardId: {} from DeckId: {}", flashcard_id, deck_id, e);
            throw new RuntimeException("Failed to remove flashcard from deck.", e);
        }
    }

    public List<Flashcard> getAvailableFlashcards(int user_id) {
        try {
            List<Flashcard> available_flashcards = deck_data_access.getUnassignedFlashcardsForUser(user_id);
            logger.info("Retrieved {} available flashcards for userId: {}", available_flashcards.size(), user_id);
            return available_flashcards;
        } catch (Exception e) {
            logger.error("Exception occurred while retrieving available flashcards for userId: {}", user_id, e);
            throw new RuntimeException("Failed to retrieve available flashcards.", e);
        }
    }

    public List<Flashcard> getFlashcardsInDeck(int deck_id) {
        try {
            List<Flashcard> deck_flashcards = deck_data_access.getFlashcardsForDeck(deck_id);
            logger.info("Retrieved {} flashcards for DeckId: {}", deck_flashcards.size(), deck_id);
            return deck_flashcards;
        } catch (Exception e) {
            logger.error("Exception occurred while retrieving flashcards for DeckId: {}", deck_id, e);
            throw new RuntimeException("Failed to retrieve flashcards in deck.", e);
        }
    }

    public List<Flashcard> getFlashcardsNotInDeck(int user_id, int deck_id) {
        try {
            List<Flashcard> available_flashcards = deck_data_access.getFlashcardsNotInDeck(user_id, deck_id);
            logger.info("Retrieved {} flashcards not in DeckId: {} for userId: {}", available_flashcards.size(), deck_id, user_id);
            return available_flashcards;
        } catch (Exception e) {
            logger.error("Exception occurred while retrieving flashcards not in DeckId: {} for userId: {}", deck_id, user_id, e); 
            throw new RuntimeException("Failed to retrieve flashcards not in deck.", e);
        }
    }
}
