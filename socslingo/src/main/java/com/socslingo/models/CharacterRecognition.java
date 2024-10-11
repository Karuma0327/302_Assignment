package com.socslingo.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharacterRecognition {
    private static final Logger logger = LoggerFactory.getLogger(CharacterRecognition.class);

    private final String katakana;
    private final String hiragana;
    private final String romaji;

    public CharacterRecognition(String katakana, String hiragana, String romaji) {
        this.katakana = katakana;
        this.hiragana = hiragana;
        this.romaji = romaji;
        logger.info("LanguageCharacter created: {}", this);
    }

    public String getKatakana() {
        return katakana;
    }

    public String getHiragana() {
        return hiragana;
    }

    public String getRomaji() {
        return romaji;
    }

    @Override
    public String toString() {
        return "LanguageCharacter{" +
                "katakana='" + katakana + '\'' +
                ", hiragana='" + hiragana + '\'' +
                ", romaji='" + romaji + '\'' +
                '}';
    }
}
