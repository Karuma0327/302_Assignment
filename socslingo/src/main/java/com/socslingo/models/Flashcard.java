package com.socslingo.models;

/**
 * Represents a flashcard used in the Socslingo application.
 * <p>
 * Each flashcard contains a unique identifier, a front side (question or prompt),
 * and a back side (answer or explanation). Flashcards are used for studying and
 * memorization purposes.
 * </p>
 * 
 * @author TEAM-SOCSLINGO 
 * @version 1.0
 * @since 2024-09-18
 */
public class Flashcard {
    private int id;
    private String front;
    private String back;

    /**
     * Constructs a new {@code Flashcard} with the specified ID, front text, and back text.
     * 
     * @param id    the unique identifier for the flashcard
     * @param front the text displayed on the front side of the flashcard
     * @param back  the text displayed on the back side of the flashcard
     */
    public Flashcard(int id, String front, String back) {
        this.id = id;
        this.front = front;
        this.back = back;
    }

    /**
     * Retrieves the unique identifier of the flashcard.
     * 
     * @return the flashcard's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the flashcard.
     * 
     * @param id the new ID for the flashcard
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retrieves the text displayed on the front side of the flashcard.
     * 
     * @return the front text of the flashcard
     */
    public String getFront() {
        return front;
    }

    /**
     * Sets the text displayed on the front side of the flashcard.
     * 
     * @param front the new front text for the flashcard
     */
    public void setFront(String front) {
        this.front = front;
    }

    /**
     * Retrieves the text displayed on the back side of the flashcard.
     * 
     * @return the back text of the flashcard
     */
    public String getBack() {
        return back;
    }

    /**
     * Sets the text displayed on the back side of the flashcard.
     * 
     * @param back the new back text for the flashcard
     */
    public void setBack(String back) {
        this.back = back;
    }

    /**
     * Returns a string representation of the flashcard, combining the front and back text.
     * 
     * @return a string in the format "front - back"
     */
    @Override
    public String toString() {
        return front + " - " + back;
    }
}
