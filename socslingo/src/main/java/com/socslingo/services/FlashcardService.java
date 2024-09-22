package com.socslingo.services;

import com.socslingo.dao.FlashcardDAO;
import com.socslingo.models.Flashcard;
import java.util.List;

/**
 * Service class responsible for managing flashcard-related operations.
 * <p>
 * This class provides methods to create, update, and retrieve flashcards.
 * It acts as an intermediary between the controllers and the data access layer (DAO),
 * encapsulating the business logic associated with flashcards.
 * </p>
 * 
 * @author TEAM-SOCSLINGO 
 * @version 1.0
 * @since 2024-09-18
 */
public class FlashcardService {
    private FlashcardDAO flashcardDAO;

    /**
     * Constructs a new {@code FlashcardService} with the specified {@code FlashcardDAO}.
     * 
     * @param flashcardDAO the DAO responsible for flashcard data operations
     */
    public FlashcardService(FlashcardDAO flashcardDAO) {
        this.flashcardDAO = flashcardDAO;
    }

    /**
     * Creates a new flashcard for the specified user.
     * <p>
     * This method handles the business logic for creating a flashcard, including any necessary validations
     * before delegating the insertion to the DAO.
     * </p>
     * 
     * @param userId      the ID of the user creating the flashcard
     * @param frontText   the text to be displayed on the front side of the flashcard
     * @param backText    the text to be displayed on the back side of the flashcard
     * @param createdDate the date when the flashcard was created
     */
    public void createFlashcard(int userId, String frontText, String backText, String createdDate) {
        // Business logic can be added here, e.g., validation
        flashcardDAO.insertFlashcard(userId, frontText, backText, createdDate);
    }

    /**
     * Updates an existing flashcard with new front and back text.
     * <p>
     * This method handles the business logic for updating a flashcard, including any necessary validations
     * before delegating the update to the DAO.
     * </p>
     * 
     * @param flashcardId  the ID of the flashcard to be updated
     * @param newFrontText the new text for the front side of the flashcard
     * @param newBackText  the new text for the back side of the flashcard
     */
    public void updateFlashcard(int flashcardId, String newFrontText, String newBackText) {
        // Business logic for updating a flashcard
        flashcardDAO.updateFlashcard(flashcardId, newFrontText, newBackText);
    }

    /**
     * Retrieves all flashcards associated with a specific user.
     * 
     * @param userId the ID of the user whose flashcards are to be retrieved
     * @return a {@code List} of {@code Flashcard} objects belonging to the user
     */
    public List<Flashcard> getUserFlashcards(int userId) {
        return flashcardDAO.retrieveAllFlashcards(userId);
    }
}
