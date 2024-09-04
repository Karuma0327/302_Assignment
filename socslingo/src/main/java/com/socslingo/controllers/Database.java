package com.socslingo.controllers;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Database {

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
            Scanner scanner = new Scanner(System.in);
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
                + " cards_completed integer,\n"
                + " accuracy_rate real,\n"
                + " experience_gained integer\n"
                + ");";

        String kahoot_game_table = "CREATE TABLE IF NOT EXISTS kahoot_game_table (\n"
                + " kahoot_game_id integer PRIMARY KEY,\n"
                + " user_id integer,\n"
                + " kahoot_game_completed integer,\n"
                + " accuracy_rate real,\n"
                + " experience_gained integer\n"
                + ");";

        String user_created_flashcards_table = "CREATE TABLE IF NOT EXISTS user_created_flashcards_table (\n"
                + " flashcard_id integer PRIMARY KEY,\n"
                + " user_id integer,\n"
                + " front_text text,\n"
                + " back_text text,\n"
                + " created_date text\n"
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
                stmt.execute(user_created_flashcards_table);
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

    public static void insertData(int flashcard_id, int user_id, String front_text, String back_text, String created_date) {
        String dbPath = "src/main/database/socslingo_database.db";
        String url = "jdbc:sqlite:" + dbPath;

        String sql = "INSERT INTO user_created_flashcards_table(flashcard_id, user_id, front_text, back_text, created_date) VALUES(?, ?, ?, ?, ?)";

        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            try (Connection conn = DriverManager.getConnection(url);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, flashcard_id);
                pstmt.setInt(2, user_id);
                pstmt.setString(3, front_text);
                pstmt.setString(4, back_text);
                pstmt.setString(5, created_date);
                pstmt.executeUpdate();
                System.out.println("Data inserted successfully.");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void updateData(int id, String newValue, String newCardFront, String newCardBack) {
        String dbPath = "src/main/database/socslingo_database.db";
        String url = "jdbc:sqlite:" + dbPath;

        String sql = "UPDATE sample_table SET value = ?, card_front = ?, card_back = ? WHERE id = ?";

        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newValue);
                pstmt.setString(2, newCardFront);
                pstmt.setString(3, newCardBack);
                pstmt.setInt(4, id);
                pstmt.executeUpdate();
                System.out.println("Data updated successfully.");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void retrieveData(int id) {
        String dbPath = "src/main/database/socslingo_database.db";
        String url = "jdbc:sqlite:" + dbPath;

        String sql = "SELECT id, value, card_front, card_back FROM sample_table WHERE id = ?";

        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id"));
                    System.out.println("Value: " + rs.getString("value"));
                    System.out.println("Card Front: " + rs.getString("card_front"));
                    System.out.println("Card Back: " + rs.getString("card_back"));
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
        public static boolean insertUser(String username, String email, String password) {
        String dbPath = "src/main/database/socslingo_database.db";
        String url = "jdbc:sqlite:" + dbPath;

        String sql = "INSERT INTO user_table(username, email, password) VALUES(?, ?, ?)";

        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            try (Connection conn = DriverManager.getConnection(url);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, email);
                pstmt.setString(3, password);
                pstmt.executeUpdate();
                return true;
            }
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public static boolean validateUser(String username, String password) {
        String dbPath = "src/main/database/socslingo_database.db";
        String url = "jdbc:sqlite:" + dbPath;

        String sql = "SELECT * FROM user_table WHERE username = ? AND password = ?";

        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Hash the input password
            String hashedPassword = hashPassword(password);

            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, hashedPassword);
                ResultSet rs = pstmt.executeQuery();
                return rs.next(); // Returns true if a matching record is found
            }
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        createDatabase();

    }
}