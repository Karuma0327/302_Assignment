package com.socslingo.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharacterRecognition {
    private static final Logger logger = LoggerFactory.getLogger(CharacterRecognition.class);

    private final int activityId;
    private final String characterType; // "Hiragana", "Katakana", or "Kanji"
    private final String character;     // The character itself
    private final String romaji;

    public CharacterRecognition(int activityId, String characterType, String character, String romaji) {
        this.activityId = activityId;
        this.characterType = characterType;
        this.character = character;
        this.romaji = romaji;
        logger.info("CharacterRecognition created: {}", this);
    }

    public int getActivityId() {
        return activityId;
    }

    public String getCharacterType() {
        return characterType;
    }

    public String getCharacter() {
        return character;
    }

    public String getRomaji() {
        return romaji;
    }

    @Override
    public String toString() {
        return "CharacterRecognition{" +
                "activityId=" + activityId +
                ", characterType='" + characterType + '\'' +
                ", character='" + character + '\'' +
                ", romaji='" + romaji + '\'' +
                '}';
    }
}
