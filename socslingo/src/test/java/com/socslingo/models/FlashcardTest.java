package com.socslingo.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FlashcardTest {

    @Test
    public void testFlashcardConstructor() {
        Flashcard flashcard = new Flashcard(1, "What is Java?", "A programming language.");
        assertEquals(1, flashcard.getId(), "Flashcard ID should be initialized to 1");
        assertEquals("What is Java?", flashcard.getFront(), "Front should be initialized correctly");
        assertEquals("A programming language.", flashcard.getBack(), "Back should be initialized correctly");
    }

    @Test
    public void testSettersAndGetters() {
        Flashcard flashcard = new Flashcard(0, null, null);

        flashcard.setId(2);
        assertEquals(2, flashcard.getId(), "Flashcard ID should be set to 2");

        flashcard.setFront("What is Python?");
        assertEquals("What is Python?", flashcard.getFront(), "Front should be updated to 'What is Python?'");

        flashcard.setBack("A versatile programming language.");
        assertEquals("A versatile programming language.", flashcard.getBack(), "Back should be updated correctly");
    }

    @Test
    public void testToString() {
        Flashcard flashcard = new Flashcard(3, "Capital of France?", "Paris");
        String expected = "Capital of France? - Paris";
        assertEquals(expected, flashcard.toString(), "toString() method should return the correct string representation");
    }

    @Test
    public void testEdgeCases() {
        // Testing with empty strings
        Flashcard flashcardEmpty = new Flashcard(4, "", "");
        assertEquals("", flashcardEmpty.getFront(), "Front should be an empty string");
        assertEquals("", flashcardEmpty.getBack(), "Back should be an empty string");
        assertEquals(" - ", flashcardEmpty.toString(), "toString() should handle empty strings correctly");

        // Testing with null values
        Flashcard flashcardNull = new Flashcard(5, null, null);
        assertNull(flashcardNull.getFront(), "Front should be null");
        assertNull(flashcardNull.getBack(), "Back should be null");
        assertEquals("null - null", flashcardNull.toString(), "toString() should handle null values correctly");
    }

    /**
     * New Test Cases Below
     */

    @Test
    public void testSetFrontToNull() {
        Flashcard flashcard = new Flashcard(6, "Initial Front", "Initial Back");
        flashcard.setFront(null);
        assertNull(flashcard.getFront(), "Front should be set to null");
    }

    @Test
    public void testSetBackToNull() {
        Flashcard flashcard = new Flashcard(7, "Initial Front", "Initial Back");
        flashcard.setBack(null);
        assertNull(flashcard.getBack(), "Back should be set to null");
    }

    @Test
    public void testUpdateMultipleFields() {
        Flashcard flashcard = new Flashcard(8, "Old Front", "Old Back");
        flashcard.setId(9);
        flashcard.setFront("New Front");
        flashcard.setBack("New Back");

        assertEquals(9, flashcard.getId(), "Flashcard ID should be updated to 9");
        assertEquals("New Front", flashcard.getFront(), "Front should be updated to 'New Front'");
        assertEquals("New Back", flashcard.getBack(), "Back should be updated to 'New Back'");
    }

    @Test
    public void testSetFrontToEmptyString() {
        Flashcard flashcard = new Flashcard(10, "Some Front", "Some Back");
        flashcard.setFront("");
        assertEquals("", flashcard.getFront(), "Front should be set to an empty string");
    }

    @Test
    public void testSetBackToEmptyString() {
        Flashcard flashcard = new Flashcard(11, "Some Front", "Some Back");
        flashcard.setBack("");
        assertEquals("", flashcard.getBack(), "Back should be set to an empty string");
    }

    @Test
    public void testToStringWithNullFields() {
        Flashcard flashcard = new Flashcard(12, null, null);
        String expected = "null - null";
        assertEquals(expected, flashcard.toString(), "toString() should handle null fields correctly");
    }

    @Test
    public void testToStringWithEmptyFields() {
        Flashcard flashcard = new Flashcard(13, "", "");
        String expected = " - ";
        assertEquals(expected, flashcard.toString(), "toString() should handle empty string fields correctly");
    }

    @Test
    public void testToStringWithMixedFields() {
        Flashcard flashcard = new Flashcard(14, "Question?", "");
        String expected = "Question? - ";
        assertEquals(expected, flashcard.toString(), "toString() should handle mixed fields correctly");

        flashcard.setFront("");
        flashcard.setBack("Answer.");
        expected = " - Answer.";
        assertEquals(expected, flashcard.toString(), "toString() should handle mixed fields correctly");
    }
}
