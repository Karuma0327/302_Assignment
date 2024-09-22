package com.socslingo.controllers.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Controller class responsible for managing the creation and setup of the application's database.
 * <p>
 * This class handles the initialization of the SQLite database, including the creation of necessary
 * tables such as user_table, pet_table, flashcards_table, kahoot_game_table, user_created_flashcards_table,
 * and user_created_kahootgame_table. It also manages scenarios where the database already exists,
 * allowing users to overwrite it if desired.
 * </p>
 * 
 * @author TEAM-SOCSLINGO 
 * @version 1.0
 * @since 2024-09-18
 */
public class DatabaseController {

    /**
     * Default constructor for DatabaseController.
     */
    public DatabaseController() {
        // Default constructor
    }


    /**
     * Creates the application's SQLite database and its associated tables.
     * <p>
     * This method performs the following actions:
     * <ul>
     *     <li>Checks if the database directory exists and creates it if necessary.</li>
     *     <li>Determines if the database file already exists. If it does, prompts the user to overwrite it.</li>
     *     <li>Establishes a connection to the SQLite database.</li>
     *     <li>Executes SQL statements to create the required tables if they do not already exist.</li>
     * </ul>
     */
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

        String user_table = "CREATE TABLE IF NOT EXISTS user_table (\n"
                + " user_id integer PRIMARY KEY,\n"
                + " username text NOT NULL,\n"
                + " email text NOT NULL,\n"
                + " password text NOT NULL,\n"
                + " language_level text,\n"
                + " tasks_completed integer,\n"
                + " pet_id integer\n"
                + ");";

        String pet_table = "CREATE TABLE IF NOT EXISTS pet_table (\n"
                + " pet_id integer PRIMARY KEY,\n"
                + " pet_name text NOT NULL,\n"
                + " hunger_level integer,\n"
                + " happiness_level integer,\n"
                + " health_level integer,\n"
                + " pet_tier integer,\n"
                + " pet_experience integer\n"
                + ");";

        String flashcards_table = "CREATE TABLE IF NOT EXISTS flashcards_table (\n"
                + " flashcard_id integer PRIMARY KEY,\n"
                + " user_id integer,\n"
                + " front_text text NOT NULL,\n"
                + " back_text text NOT NULL,\n"
                + " flashcard_list_id integer,\n" // Optional
                + " created_date text NOT NULL,\n"
                + " FOREIGN KEY (user_id) REFERENCES user_table(user_id),\n"
                + " FOREIGN KEY (flashcard_list_id) REFERENCES flashcard_list_table(flashcard_list_id)\n" // Optional
                + ");";

        String kahoot_game_table = "CREATE TABLE IF NOT EXISTS kahoot_game_table (\n"
                + " kahoot_game_id integer PRIMARY KEY,\n"
                + " user_id integer,\n"
                + " kahoot_game_completed integer,\n"
                + " accuracy_rate real,\n"
                + " experience_gained integer\n"
                + ");";

        String user_created_kahoot_game_table = "CREATE TABLE IF NOT EXISTS user_created_kahootgame_table (\n"
                + " kahoot_game_id integer PRIMARY KEY,\n"
                + " user_id integer,\n"
                + " kahoot_question text,\n"
                + " kahoot_answer_choice_one text,\n"
                + " kahoot_answer_choice_two text,\n"
                + " kahoot_answer_choice_three text,\n"
                + " kahoot_answer_choice_four text,\n"
                + " kahoot_correct_choice text,\n"
                + " kahoot_experience integer,\n"
                + " created_date text\n"
                + ");";

        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            try (Connection conn = DriverManager.getConnection(url);
                 Statement stmt = conn.createStatement()) {
                stmt.execute(user_table);
                stmt.execute(pet_table);
                stmt.execute(flashcards_table);
                stmt.execute(kahoot_game_table);
                stmt.execute(user_created_kahoot_game_table);
                System.out.println("Database and tables created successfully.");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * The main method to execute the database creation process.
     * 
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        createDatabase();
    }
}
