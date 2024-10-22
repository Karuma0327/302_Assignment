package com.socslingo.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CharacterRecognitionTest {

    @Test
    void testCharacterRecognitionCreationHiragana() {
        CharacterRecognition cr = new CharacterRecognition(101, "Hiragana", "あ", "a");
        assertEquals(101, cr.getActivityId());
        assertEquals("Hiragana", cr.getCharacterType());
        assertEquals("あ", cr.getCharacter());
        assertEquals("a", cr.getRomaji());
    }

    @Test
    void testCharacterRecognitionCreationKatakana() {
        CharacterRecognition cr = new CharacterRecognition(102, "Katakana", "ケ", "ke");
        assertEquals(102, cr.getActivityId());
        assertEquals("Katakana", cr.getCharacterType());
        assertEquals("ケ", cr.getCharacter());
        assertEquals("ke", cr.getRomaji());
    }

    @Test
    void testCharacterRecognitionCreationKanji() {
        CharacterRecognition cr = new CharacterRecognition(103, "Kanji", "木", "ki");
        assertEquals(103, cr.getActivityId());
        assertEquals("Kanji", cr.getCharacterType());
        assertEquals("木", cr.getCharacter());
        assertEquals("ki", cr.getRomaji());
    }

    @Test
    void testGetActivityId() {
        CharacterRecognition cr = new CharacterRecognition(104, "Hiragana", "う", "u");
        assertEquals(104, cr.getActivityId());
    }

    @Test
    void testGetCharacterType() {
        CharacterRecognition cr = new CharacterRecognition(105, "Katakana", "ク", "ku");
        assertEquals("Katakana", cr.getCharacterType());
    }

    @Test
    void testGetCharacter() {
        CharacterRecognition cr = new CharacterRecognition(106, "Kanji", "日", "ni");
        assertEquals("日", cr.getCharacter());
    }

    @Test
    void testGetRomaji() {
        CharacterRecognition cr = new CharacterRecognition(107, "Hiragana", "え", "e");
        assertEquals("e", cr.getRomaji());
    }

    @Test
    void testToStringHiragana() {
        CharacterRecognition cr = new CharacterRecognition(108, "Hiragana", "お", "o");
        String expected = "CharacterRecognition{activityId=108, characterType='Hiragana', character='お', romaji='o'}";
        assertEquals(expected, cr.toString());
    }

    @Test
    void testToStringKatakana() {
        CharacterRecognition cr = new CharacterRecognition(109, "Katakana", "ケ", "ke");
        String expected = "CharacterRecognition{activityId=109, characterType='Katakana', character='ケ', romaji='ke'}";
        assertEquals(expected, cr.toString());
    }

    @Test
    void testToStringKanji() {
        CharacterRecognition cr = new CharacterRecognition(110, "Kanji", "森", "mori");
        String expected = "CharacterRecognition{activityId=110, characterType='Kanji', character='森', romaji='mori'}";
        assertEquals(expected, cr.toString());
    }

    @Test
    void testMultipleCharacterRecognition() {
        CharacterRecognition cr1 = new CharacterRecognition(111, "Hiragana", "か", "ka");
        CharacterRecognition cr2 = new CharacterRecognition(112, "Katakana", "カー", "kaa");
        CharacterRecognition cr3 = new CharacterRecognition(113, "Kanji", "火", "hi");

        assertEquals(111, cr1.getActivityId());
        assertEquals("Hiragana", cr1.getCharacterType());
        assertEquals("か", cr1.getCharacter());
        assertEquals("ka", cr1.getRomaji());

        assertEquals(112, cr2.getActivityId());
        assertEquals("Katakana", cr2.getCharacterType());
        assertEquals("カー", cr2.getCharacter());
        assertEquals("kaa", cr2.getRomaji());

        assertEquals(113, cr3.getActivityId());
        assertEquals("Kanji", cr3.getCharacterType());
        assertEquals("火", cr3.getCharacter());
        assertEquals("hi", cr3.getRomaji());
    }

    @Test
    void testImmutableFields() {
        CharacterRecognition cr = new CharacterRecognition(114, "Hiragana", "き", "ki");
        // Since fields are final, they should remain unchanged
        assertEquals("Hiragana", cr.getCharacterType());
        assertEquals("き", cr.getCharacter());
        assertEquals("ki", cr.getRomaji());
    }

    @Test
    void testSpecialCharacters() {
        CharacterRecognition cr = new CharacterRecognition(115, "Katakana", "シ", "shi");
        assertEquals("シ", cr.getCharacter());
        assertEquals("shi", cr.getRomaji());
    }

    @Test
    void testUnicodeCharacters() {
        CharacterRecognition cr = new CharacterRecognition(116, "Kanji", "愛", "ai");
        assertEquals("愛", cr.getCharacter());
        assertEquals("ai", cr.getRomaji());
    }

    @Test
    void testNegativeActivityId() {
        CharacterRecognition cr = new CharacterRecognition(-117, "Hiragana", "つ", "tsu");
        assertEquals(-117, cr.getActivityId());
    }

    @Test
    void testZeroActivityId() {
        CharacterRecognition cr = new CharacterRecognition(0, "Katakana", "ツ", "tsu");
        assertEquals(0, cr.getActivityId());
    }

    @Test
    void testEmptyRomaji() {
        CharacterRecognition cr = new CharacterRecognition(118, "Kanji", "日", "");
        assertEquals("", cr.getRomaji());
    }
}