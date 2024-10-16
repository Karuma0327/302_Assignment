package com.socslingo.services;

import com.socslingo.dataAccess.FlashcardDataAccess;
import com.socslingo.models.Flashcard;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlashcardService {
    private FlashcardDataAccess flashcard_data_access;
    private static final Logger logger = LoggerFactory.getLogger(FlashcardService.class);

    public FlashcardService(FlashcardDataAccess flashcard_data_access) {
        this.flashcard_data_access = flashcard_data_access;
    }

    public void createFlashcard(int user_id, String front_text, String back_text, String created_date) {
        flashcard_data_access.insertFlashcard(user_id, front_text, back_text, created_date);
    }

    public void updateFlashcard(int flashcard_id, String new_front_text, String new_back_text) {
        flashcard_data_access.updateFlashcard(flashcard_id, new_front_text, new_back_text);
    }

    public List<Flashcard> getUserFlashcards(int user_id) {
        return flashcard_data_access.retrieveAllFlashcards(user_id);
    }

    public Flashcard getFlashcardById(int flashcard_id) {
        return flashcard_data_access.retrieveFlashcardById(flashcard_id);
    }

    public List<Flashcard> getAllFlashcardsForUser(int userId) {
        logger.debug("Fetching all flashcards for user_id: {}", userId);
        return flashcard_data_access.getUserFlashcards(userId);
    }


    
}
