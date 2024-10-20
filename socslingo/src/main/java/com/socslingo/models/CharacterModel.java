package com.socslingo.models;

public class CharacterModel {
    private String characterType;
    private String character;
    private String romaji;

    public CharacterModel(String characterType, String character, String romaji) {
        this.characterType = characterType;
        this.character = character;
        this.romaji = romaji;
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
    
}