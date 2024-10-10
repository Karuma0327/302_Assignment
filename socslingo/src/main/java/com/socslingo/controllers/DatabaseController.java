package com.socslingo.controllers;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class DatabaseController {

    public static void createDatabase() {
        String folderPath = "src/main/database";
        String dbPath = folderPath + "/socslingo_database.db";

        // Create the database folder if it doesn't exist
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Check if the database file exists
        File dbFile = new File(dbPath);
        if (dbFile.exists()) {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.print("Database already exists. Do you want to overwrite it? (Y/N): ");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("y")) {
                    if (dbFile.delete()) {
                        System.out.println("Existing database deleted.");
                    } else {
                        System.out.println("Failed to delete the existing database.");
                        return;
                    }
                } else {
                    System.out.println("Operation cancelled.");
                    return;
                }
            }
        }

        String url = "jdbc:sqlite:" + dbPath;

        // Existing table creation statements
        String user_table = "CREATE TABLE IF NOT EXISTS user_table (\n"
                + " user_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " username TEXT NOT NULL UNIQUE,\n"
                + " email TEXT NOT NULL UNIQUE,\n"
                + " password TEXT NOT NULL,\n"
                + " language_level TEXT,\n"
                + " tasks_completed INTEGER DEFAULT 0,\n"
                + " pet_id INTEGER,\n"
                + " FOREIGN KEY (pet_id) REFERENCES pet_table(pet_id)\n"
                + ");";

        String pet_table = "CREATE TABLE IF NOT EXISTS pet_table (\n"
                + " pet_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " pet_name TEXT NOT NULL,\n"
                + " hunger_level INTEGER DEFAULT 0,\n"
                + " happiness_level INTEGER DEFAULT 0,\n"
                + " health_level INTEGER DEFAULT 0,\n"
                + " pet_tier INTEGER DEFAULT 1,\n"
                + " pet_experience INTEGER DEFAULT 0\n"
                + ");";

        String flashcards_table = "CREATE TABLE IF NOT EXISTS flashcards_table (\n"
                + " flashcard_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " user_id INTEGER,\n"
                + " front_text TEXT NOT NULL,\n"
                + " back_text TEXT NOT NULL,\n"
                + " created_date TEXT NOT NULL,\n"
                + " FOREIGN KEY (user_id) REFERENCES user_table(user_id)\n"
                + ");";

        String kahoot_game_table = "CREATE TABLE IF NOT EXISTS kahoot_game_table (\n"
                + " kahoot_game_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " user_id INTEGER,\n"
                + " kahoot_game_completed INTEGER DEFAULT 0,\n"
                + " accuracy_rate REAL,\n"
                + " experience_gained INTEGER DEFAULT 0,\n"
                + " FOREIGN KEY (user_id) REFERENCES user_table(user_id)\n"
                + ");";

        String user_created_flashcards_table = "CREATE TABLE IF NOT EXISTS user_created_flashcards_table (\n"
                + " flashcard_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " user_id INTEGER,\n"
                + " front_text TEXT,\n"
                + " back_text TEXT,\n"
                + " created_date TEXT,\n"
                + " FOREIGN KEY (user_id) REFERENCES user_table(user_id)\n"
                + ");";

        String user_created_kahoot_game_table = "CREATE TABLE IF NOT EXISTS user_created_kahootgame_table (\n"
                + " kahoot_game_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " user_id INTEGER,\n"
                + " kahoot_question TEXT,\n"
                + " kahoot_answer_choice_one TEXT,\n"
                + " kahoot_answer_choice_two TEXT,\n"
                + " kahoot_answer_choice_three TEXT,\n"
                + " kahoot_answer_choice_four TEXT,\n"
                + " kahoot_correct_choice TEXT,\n"
                + " kahoot_experience INTEGER DEFAULT 0,\n"
                + " created_date TEXT,\n"
                + " FOREIGN KEY (user_id) REFERENCES user_table(user_id)\n"
                + ");";

        // New table for flashcard decks
        String flashcard_decks_table = "CREATE TABLE IF NOT EXISTS flashcard_decks_table (\n"
                + " deck_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " user_id INTEGER NOT NULL,\n"
                + " deck_name TEXT NOT NULL,\n"
                + " created_date TEXT NOT NULL,\n"
                + " FOREIGN KEY (user_id) REFERENCES user_table(user_id)\n"
                + ");";

        // New association table between decks and flashcards
        String deck_flashcards_table = "CREATE TABLE IF NOT EXISTS deck_flashcards_table (\n"
                + " deck_id INTEGER NOT NULL,\n"
                + " flashcard_id INTEGER NOT NULL,\n"
                + " PRIMARY KEY (deck_id, flashcard_id),\n"
                + " FOREIGN KEY (deck_id) REFERENCES flashcard_decks_table(deck_id) ON DELETE CASCADE,\n"
                + " FOREIGN KEY (flashcard_id) REFERENCES flashcards_table(flashcard_id) ON DELETE CASCADE\n"
                + ");";

        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            try (Connection conn = DriverManager.getConnection(url);
                 Statement stmt = conn.createStatement()) {
                // Execute existing table creation
                stmt.execute(user_table);
                stmt.execute(pet_table);
                stmt.execute(flashcards_table);
                stmt.execute(kahoot_game_table);
                stmt.execute(user_created_flashcards_table);
                stmt.execute(user_created_kahoot_game_table);

                // Execute new table creation
                stmt.execute(flashcard_decks_table);
                stmt.execute(deck_flashcards_table);

                System.out.println("Database and tables created successfully.");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        createDatabase();
    }
}
