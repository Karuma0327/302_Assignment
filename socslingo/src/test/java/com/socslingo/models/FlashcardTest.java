package com.socslingo.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FlashcardTest {

    @Test
    void testFlashcardCreation() {
        Flashcard flashcard = new Flashcard(1, "こんにちは", "Hello");
        assertEquals(1, flashcard.getId());
        assertEquals("こんにちは", flashcard.getFront());
        assertEquals("Hello", flashcard.getBack());
    }

    @Test
    void testSetId() {
        Flashcard flashcard = new Flashcard(1, "ありがとう", "Thank you");
        flashcard.setId(2);
        assertEquals(2, flashcard.getId());
    }

    @Test
    void testSetFront() {
        Flashcard flashcard = new Flashcard(3, "さようなら", "Goodbye");
        flashcard.setFront("こんばんは");
        assertEquals("こんばんは", flashcard.getFront());
    }

    @Test
    void testSetBack() {
        Flashcard flashcard = new Flashcard(4, "はい", "Yes");
        flashcard.setBack("Affirmative");
        assertEquals("Affirmative", flashcard.getBack());
    }

    @Test
    void testGetId() {
        Flashcard flashcard = new Flashcard(5, "いいえ", "No");
        assertEquals(5, flashcard.getId());
    }

    @Test
    void testGetFront() {
        Flashcard flashcard = new Flashcard(6, "おはよう", "Good morning");
        assertEquals("おはよう", flashcard.getFront());
    }

    @Test
    void testGetBack() {
        Flashcard flashcard = new Flashcard(7, "こんばんは", "Good evening");
        assertEquals("Good evening", flashcard.getBack());
    }

    @Test
    void testToString() {
        Flashcard flashcard = new Flashcard(8, "ありがとう", "Thank you");
        String expected = "Flashcard{id=8, front='ありがとう', back='Thank you'}";
        assertEquals(expected, flashcard.toString());
    }

    @Test
    void testMultipleFlashcards() {
        Flashcard flashcard1 = new Flashcard(9, "おはよう", "Good morning");
        Flashcard flashcard2 = new Flashcard(10, "こんばんは", "Good evening");
        Flashcard flashcard3 = new Flashcard(11, "さようなら", "Goodbye");

        assertEquals(9, flashcard1.getId());
        assertEquals("おはよう", flashcard1.getFront());
        assertEquals("Good morning", flashcard1.getBack());

        assertEquals(10, flashcard2.getId());
        assertEquals("こんばんは", flashcard2.getFront());
        assertEquals("Good evening", flashcard2.getBack());

        assertEquals(11, flashcard3.getId());
        assertEquals("さようなら", flashcard3.getFront());
        assertEquals("Goodbye", flashcard3.getBack());
    }

    @Test
    void testEmptyBack() {
        Flashcard flashcard = new Flashcard(12, "何", "");
        assertEquals("", flashcard.getBack());
    }

    @Test
    void testEmptyFront() {
        Flashcard flashcard = new Flashcard(13, "", "Empty Front");
        assertEquals("", flashcard.getFront());
    }

    @Test
    void testNegativeId() {
        Flashcard flashcard = new Flashcard(-1, "マイナス", "Minus");
        assertEquals(-1, flashcard.getId());
    }

    @Test
    void testZeroId() {
        Flashcard flashcard = new Flashcard(0, "ゼロ", "Zero");
        assertEquals(0, flashcard.getId());
    }

    @Test
    void testLongText() {
        String longText = "これはとても長いフロントテキストです。";
        String longBack = "This is a very long back text.";
        Flashcard flashcard = new Flashcard(14, longText, longBack);
        assertEquals(longText, flashcard.getFront());
        assertEquals(longBack, flashcard.getBack());
    }

    @Test
    void testSpecialCharacters() {
        Flashcard flashcard = new Flashcard(15, "こんにちは！", "Hello!");
        assertEquals("こんにちは！", flashcard.getFront());
        assertEquals("Hello!", flashcard.getBack());
    }

    @Test
    void testUnicodeCharacters() {
        Flashcard flashcard = new Flashcard(16, "😊", "Smiley");
        assertEquals("😊", flashcard.getFront());
        assertEquals("Smiley", flashcard.getBack());
    }

    @Test
    void testImmutableId() {
        Flashcard flashcard = new Flashcard(17, "テスト", "Test");
        flashcard.setId(18);
        assertEquals(18, flashcard.getId());
    }
}