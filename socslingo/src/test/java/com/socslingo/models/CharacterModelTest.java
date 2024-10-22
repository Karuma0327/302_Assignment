package com.socslingo.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CharacterModelTest {

    @Test
    void testCharacterModelCreationHiragana() {
        CharacterModel character = new CharacterModel("Hiragana", "あ", "a");
        assertEquals("Hiragana", character.getCharacterType());
        assertEquals("あ", character.getCharacter());
        assertEquals("a", character.getRomaji());
    }

    @Test
    void testCharacterModelCreationKatakana() {
        CharacterModel character = new CharacterModel("Katakana", "カ", "ka");
        assertEquals("Katakana", character.getCharacterType());
        assertEquals("カ", character.getCharacter());
        assertEquals("ka", character.getRomaji());
    }

    @Test
    void testCharacterModelCreationKanji() {
        CharacterModel character = new CharacterModel("Kanji", "日", "ni");
        assertEquals("Kanji", character.getCharacterType());
        assertEquals("日", character.getCharacter());
        assertEquals("ni", character.getRomaji());
    }

    @Test
    void testGetCharacterType() {
        CharacterModel character = new CharacterModel("Hiragana", "い", "i");
        assertEquals("Hiragana", character.getCharacterType());
    }

    @Test
    void testGetCharacter() {
        CharacterModel character = new CharacterModel("Katakana", "キ", "ki");
        assertEquals("キ", character.getCharacter());
    }

    @Test
    void testGetRomaji() {
        CharacterModel character = new CharacterModel("Kanji", "木", "ki");
        assertEquals("ki", character.getRomaji());
    }

    @Test
    void testMultipleCharacterModels() {
        CharacterModel hiragana = new CharacterModel("Hiragana", "う", "u");
        CharacterModel katakana = new CharacterModel("Katakana", "ク", "ku");
        CharacterModel kanji = new CharacterModel("Kanji", "木", "ki");

        assertEquals("Hiragana", hiragana.getCharacterType());
        assertEquals("う", hiragana.getCharacter());
        assertEquals("u", hiragana.getRomaji());

        assertEquals("Katakana", katakana.getCharacterType());
        assertEquals("ク", katakana.getCharacter());
        assertEquals("ku", katakana.getRomaji());

        assertEquals("Kanji", kanji.getCharacterType());
        assertEquals("木", kanji.getCharacter());
        assertEquals("ki", kanji.getRomaji());
    }

    @Test
    void testCharacterTypeNotNull() {
        CharacterModel character = new CharacterModel("Hiragana", "え", "e");
        assertNotNull(character.getCharacterType());
    }

    @Test
    void testCharacterNotNull() {
        CharacterModel character = new CharacterModel("Katakana", "ケ", "ke");
        assertNotNull(character.getCharacter());
    }

    @Test
    void testRomajiNotNull() {
        CharacterModel character = new CharacterModel("Kanji", "家", "ie");
        assertNotNull(character.getRomaji());
    }



    @Test
    void testEmptyRomaji() {
        CharacterModel character = new CharacterModel("Hiragana", "ん", "");
        assertEquals("", character.getRomaji());
    }

    @Test
    void testSpecialCharacters() {
        CharacterModel character = new CharacterModel("Kanji", "龍", "ryū");
        assertEquals("ryū", character.getRomaji());
    }

    @Test
    void testUnicodeCharacters() {
        CharacterModel character = new CharacterModel("Kanji", "愛", "ai");
        assertEquals("愛", character.getCharacter());
    }

    @Test
    void testMultipleInstances() {
        CharacterModel char1 = new CharacterModel("Hiragana", "か", "ka");
        CharacterModel char2 = new CharacterModel("Katakana", "キ", "ki");
        CharacterModel char3 = new CharacterModel("Kanji", "木", "ki");

        assertNotSame(char1, char2);
        assertNotSame(char1, char3);
        assertNotSame(char2, char3);
    }

    @Test
    void testImmutableFields() {
        CharacterModel character = new CharacterModel("Hiragana", "く", "ku");
        // Since there are no setters, fields should remain unchanged
        assertEquals("Hiragana", character.getCharacterType());
        assertEquals("く", character.getCharacter());
        assertEquals("ku", character.getRomaji());
    }
}