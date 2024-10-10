package com.socslingo.services;

import com.socslingo.dataAccess.FlashcardDataAccess;
import com.socslingo.models.Flashcard;
import java.util.List;

public class FlashcardService {
    private FlashcardDataAccess flashcardDataAccess;

    public FlashcardService(FlashcardDataAccess flashcardDataAccess) {
        this.flashcardDataAccess = flashcardDataAccess;
    }

    public void createFlashcard(int userId, String frontText, String backText, String createdDate) {
        flashcardDataAccess.insertFlashcard(userId, frontText, backText, createdDate);
    }

    public void updateFlashcard(int flashcardId, String newFrontText, String newBackText) {
        flashcardDataAccess.updateFlashcard(flashcardId, newFrontText, newBackText);
    }

    public List<Flashcard> getUserFlashcards(int userId) {
        return flashcardDataAccess.retrieveAllFlashcards(userId);
    }
    
    public Flashcard getFlashcardById(int flashcardId) {
        return flashcardDataAccess.retrieveFlashcardById(flashcardId);
    }

}
