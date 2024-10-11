package com.socslingo.models;

public class Deck {
    private int deck_id;
    private int user_id;
    private String deck_name;
    private String created_date;

    public Deck(int deck_id, int user_id, String deck_name, String created_date) {
        this.deck_id = deck_id;
        this.user_id = user_id;
        this.deck_name = deck_name;
        this.created_date = created_date;
    }

    // Getters and Setters

    public int getDeckId() {
        return deck_id;
    }

    public void setDeckId(int deck_id) {
        this.deck_id = deck_id;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public String getDeckName() {
        return deck_name;
    }

    public void setDeckName(String deck_name) {
        this.deck_name = deck_name;
    }

    public String getCreatedDate() {
        return created_date;
    }

    public void setCreatedDate(String created_date) {
        this.created_date = created_date;
    }

    @Override
    public String toString() {
        return deck_name; // Display deck name in ListView
    }
}
