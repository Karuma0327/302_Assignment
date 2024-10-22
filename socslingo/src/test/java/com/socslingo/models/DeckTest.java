package com.socslingo.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    @Test
    void testDeckCreation() {
        Deck deck = new Deck(1, 10, "Japanese Basics", "2023-10-05");
        assertEquals(1, deck.getDeckId());
        assertEquals(10, deck.getUserId());
        assertEquals("Japanese Basics", deck.getDeckName());
        assertEquals("2023-10-05", deck.getCreatedDate());
    }

    @Test
    void testSetDeckId() {
        Deck deck = new Deck(2, 20, "Advanced Kanji", "2023-11-10");
        deck.setDeckId(3);
        assertEquals(3, deck.getDeckId());
    }

    @Test
    void testSetUserId() {
        Deck deck = new Deck(4, 30, "Intermediate Hiragana", "2023-12-15");
        deck.setUserId(40);
        assertEquals(40, deck.getUserId());
    }

    @Test
    void testSetDeckName() {
        Deck deck = new Deck(5, 50, "Basic Vocabulary", "2023-09-25");
        deck.setDeckName("Advanced Vocabulary");
        assertEquals("Advanced Vocabulary", deck.getDeckName());
    }

    @Test
    void testSetCreatedDate() {
        Deck deck = new Deck(6, 60, "Grammar Essentials", "2023-08-20");
        deck.setCreatedDate("2023-10-20");
        assertEquals("2023-10-20", deck.getCreatedDate());
    }

    @Test
    void testGetDeckId() {
        Deck deck = new Deck(7, 70, "Listening Practice", "2023-07-15");
        assertEquals(7, deck.getDeckId());
    }

    @Test
    void testGetUserId() {
        Deck deck = new Deck(8, 80, "Speaking Drills", "2023-06-10");
        assertEquals(80, deck.getUserId());
    }

    @Test
    void testGetDeckName() {
        Deck deck = new Deck(9, 90, "Reading Comprehension", "2023-05-05");
        assertEquals("Reading Comprehension", deck.getDeckName());
    }

    @Test
    void testGetCreatedDate() {
        Deck deck = new Deck(10, 100, "Writing Skills", "2023-04-01");
        assertEquals("2023-04-01", deck.getCreatedDate());
    }

    @Test
    void testToString() {
        Deck deck = new Deck(11, 110, "Pronunciation", "2023-03-25");
        String expected = "Pronunciation";
        assertEquals(expected, deck.toString());
    }

    @Test
    void testMultipleDecks() {
        Deck deck1 = new Deck(12, 120, "Kanji Mastery", "2023-02-20");
        Deck deck2 = new Deck(13, 130, "Hiragana Practice", "2023-01-15");
        Deck deck3 = new Deck(14, 140, "Katakana Practice", "2022-12-10");

        assertEquals(12, deck1.getDeckId());
        assertEquals("Kanji Mastery", deck1.getDeckName());

        assertEquals(13, deck2.getDeckId());
        assertEquals("Hiragana Practice", deck2.getDeckName());

        assertEquals(14, deck3.getDeckId());
        assertEquals("Katakana Practice", deck3.getDeckName());
    }

    @Test
    void testEmptyDeckName() {
        Deck deck = new Deck(15, 150, "", "2022-11-05");
        assertEquals("", deck.getDeckName());
    }

    @Test
    void testNullDeckName() {
        Deck deck = new Deck(16, 160, null, "2022-10-01");
        assertNull(deck.getDeckName());
    }

    @Test
    void testNegativeDeckId() {
        Deck deck = new Deck(-1, 170, "Negative ID Deck", "2022-09-25");
        assertEquals(-1, deck.getDeckId());
    }

    @Test
    void testZeroUserId() {
        Deck deck = new Deck(17, 0, "Zero User ID Deck", "2022-08-20");
        assertEquals(0, deck.getUserId());
    }

    @Test
    void testImmutableDeckId() {
        Deck deck = new Deck(18, 180, "Immutable Test", "2022-07-15");
        deck.setDeckId(19);
        assertEquals(19, deck.getDeckId());
    }

    @Test
    void testImmutableUserId() {
        Deck deck = new Deck(20, 190, "Immutable User ID", "2022-06-10");
        deck.setUserId(200);
        assertEquals(200, deck.getUserId());
    }
}