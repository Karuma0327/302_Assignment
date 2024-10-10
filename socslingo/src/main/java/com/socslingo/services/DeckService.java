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
    private final DeckDataAccess deckDataAccess;

    public DeckService(DeckDataAccess deckDataAccess) {
        this.deckDataAccess = deckDataAccess;
    }

    public Deck createDeck(int userId, String deckName) {
        if (deckName == null || deckName.trim().isEmpty()) {
            logger.warn("Attempted to create a deck with an empty name.");
            throw new IllegalArgumentException("Deck name cannot be empty.");
        }

        String createdDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        try {
            int deckId = deckDataAccess.createDeck(userId, deckName, createdDate);
            if (deckId != -1) {
                logger.info("Deck '{}' created successfully with ID: {}", deckName, deckId);
                return new Deck(deckId, userId, deckName, createdDate);
            } else {
                logger.error("Failed to create deck '{}'", deckName);
                return null;
            }
        } catch (Exception e) {
            logger.error("Exception occurred while creating deck '{}'", deckName, e);
            throw new RuntimeException("Failed to create deck.", e);
        }
    }

    public boolean deleteDeck(int deckId) {
        try {
            boolean success = deckDataAccess.deleteDeck(deckId);
            if (success) {
                logger.info("DeckId: {} deleted successfully.", deckId);
            } else {
                logger.error("Failed to delete DeckId: {}", deckId);
            }
            return success;
        } catch (Exception e) {
            logger.error("Exception occurred while deleting DeckId: {}", deckId, e);
            throw new RuntimeException("Failed to delete deck.", e);
        }
    }

    public boolean updateDeckName(int deckId, String newDeckName) {
        if (newDeckName == null || newDeckName.trim().isEmpty()) {
            logger.warn("Attempted to update deck with an empty name.");
            throw new IllegalArgumentException("Deck name cannot be empty.");
        }

        try {
            boolean success = deckDataAccess.updateDeck(deckId, newDeckName);
            if (success) {
                logger.info("DeckId: {} updated successfully to '{}'.", deckId, newDeckName);
            } else {
                logger.error("Failed to update DeckId: {}", deckId);
            }
            return success;
        } catch (Exception e) {
            logger.error("Exception occurred while updating DeckId: {}", deckId, e);
            throw new RuntimeException("Failed to update deck.", e);
        }
    }

    public List<Deck> getUserDecks(int userId) {
        try {
            List<Deck> decks = deckDataAccess.getUserDecks(userId);
            logger.info("Retrieved {} decks for userId: {}", decks.size(), userId);
            return decks;
        } catch (Exception e) {
            logger.error("Exception occurred while retrieving decks for userId: {}", userId, e);
            throw new RuntimeException("Failed to retrieve decks.", e);
        }
    }

    public boolean addFlashcardToDeck(int deckId, int flashcardId) {
        try {
            boolean success = deckDataAccess.addFlashcardToDeck(deckId, flashcardId);
            if (success) {
                logger.info("FlashcardId: {} added to DeckId: {} successfully.", flashcardId, deckId);
            } else {
                logger.error("Failed to add FlashcardId: {} to DeckId: {}", flashcardId, deckId);
            }
            return success;
        } catch (Exception e) {
            logger.error("Exception occurred while adding FlashcardId: {} to DeckId: {}", flashcardId, deckId, e);
            throw new RuntimeException("Failed to add flashcard to deck.", e);
        }
    }

    public boolean removeFlashcardFromDeck(int deckId, int flashcardId) {
        try {
            boolean success = deckDataAccess.removeFlashcardFromDeck(deckId, flashcardId);
            if (success) {
                logger.info("FlashcardId: {} removed from DeckId: {} successfully.", flashcardId, deckId);
            } else {
                logger.error("Failed to remove FlashcardId: {} from DeckId: {}", flashcardId, deckId);
            }
            return success;
        } catch (Exception e) {
            logger.error("Exception occurred while removing FlashcardId: {} from DeckId: {}", flashcardId, deckId, e);
            throw new RuntimeException("Failed to remove flashcard from deck.", e);
        }
    }

    public List<Flashcard> getAvailableFlashcards(int userId) {
        try {
            List<Flashcard> availableFlashcards = deckDataAccess.getFlashcardsNotInAnyDeck1(userId);
            logger.info("Retrieved {} available flashcards for userId: {}", availableFlashcards.size(), userId);
            return availableFlashcards;
        } catch (Exception e) {
            logger.error("Exception occurred while retrieving available flashcards for userId: {}", userId, e);
            throw new RuntimeException("Failed to retrieve available flashcards.", e);
        }
    }

    public List<Flashcard> getFlashcardsInDeck(int deckId) {
        try {
            List<Flashcard> deckFlashcards = deckDataAccess.getFlashcardsInDeck(deckId);
            logger.info("Retrieved {} flashcards for DeckId: {}", deckFlashcards.size(), deckId);
            return deckFlashcards;
        } catch (Exception e) {
            logger.error("Exception occurred while retrieving flashcards for DeckId: {}", deckId, e);
            throw new RuntimeException("Failed to retrieve flashcards in deck.", e);
        }
    }

    public List<Flashcard> getFlashcardsNotInDeck(int userId, int deckId) {
        try {
            List<Flashcard> availableFlashcards = deckDataAccess.getFlashcardsNotInDeck(userId, deckId);
            logger.info("Retrieved {} flashcards not in DeckId: {} for userId: {}", availableFlashcards.size(), deckId, userId);
            return availableFlashcards;
        } catch (Exception e) {
            logger.error("Exception occurred while retrieving flashcards not in DeckId: {} for userId: {}", deckId, userId, e);
            throw new RuntimeException("Failed to retrieve flashcards not in deck.", e);
        }
    }

    
    
}
