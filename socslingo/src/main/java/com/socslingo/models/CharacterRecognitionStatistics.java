package com.socslingo.models;

import java.time.LocalDateTime;

public class CharacterRecognitionStatistics {
    private int statId;
    private int userId;
    private int charactersCorrect;
    private int charactersIncorrect;
    private double charactersAccuracy;
    private LocalDateTime lastUpdated;

    // Constructors
    public CharacterRecognitionStatistics() {
    }

    public CharacterRecognitionStatistics(int userId) {
        this.userId = userId;
        this.charactersCorrect = 0;
        this.charactersIncorrect = 0;
        this.charactersAccuracy = 0.0;
        this.lastUpdated = LocalDateTime.now();
    }

    public CharacterRecognitionStatistics(int statId, int userId, int charactersCorrect, int charactersIncorrect, double charactersAccuracy, LocalDateTime lastUpdated) {
        this.statId = statId;
        this.userId = userId;
        this.charactersCorrect = charactersCorrect;
        this.charactersIncorrect = charactersIncorrect;
        this.charactersAccuracy = charactersAccuracy;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public int getStatId() {
        return statId;
    }

    public void setStatId(int statId) {
        this.statId = statId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCharactersCorrect() {
        return charactersCorrect;
    }

    public void setCharactersCorrect(int charactersCorrect) {
        this.charactersCorrect = charactersCorrect;
        updateAccuracy();
    }

    public int getCharactersIncorrect() {
        return charactersIncorrect;
    }

    public void setCharactersIncorrect(int charactersIncorrect) {
        this.charactersIncorrect = charactersIncorrect;
        updateAccuracy();
    }

    public double getCharactersAccuracy() {
        return charactersAccuracy;
    }

    /**
     * Sets the characters accuracy directly.
     *
     * @param charactersAccuracy The accuracy percentage to set.
     */
    public void setCharactersAccuracy(double charactersAccuracy) {
        this.charactersAccuracy = charactersAccuracy;
    }

    public void updateAccuracy() {
        int total = charactersCorrect + charactersIncorrect;
        if (total > 0) {
            this.charactersAccuracy = ((double) charactersCorrect / total) * 100.0;
        } else {
            this.charactersAccuracy = 0.0;
        }
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}