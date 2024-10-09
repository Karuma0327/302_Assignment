package com.socslingo.models;

public class Deck {
    private int deckId;
    private int userId;
    private String deckName;
    private String createdDate;

    public Deck(int deckId, int userId, String deckName, String createdDate) {
        this.deckId = deckId;
        this.userId = userId;
        this.deckName = deckName;
        this.createdDate = createdDate;
    }

    // Getters and Setters

    public int getDeckId() {
        return deckId;
    }

    public void setDeckId(int deckId) {
        this.deckId = deckId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return deckName; // Display deck name in ListView
    }
}
