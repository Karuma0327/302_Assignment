package com.socslingo.controllers.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.socslingo.models.Flashcard;

public class FlashcardDatabaseController {

    public static void insertData(int user_id, String front_text, String back_text, String created_date) {
        String dbPath = "src/main/database/socslingo_database.db";
        String url = "jdbc:sqlite:" + dbPath;

        String sql = "INSERT INTO user_created_flashcards_table(user_id, front_text, back_text, created_date) VALUES(?, ?, ?, ?)";

        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, user_id);
                pstmt.setString(2, front_text);
                pstmt.setString(3, back_text);
                pstmt.setString(4, created_date);
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

    public static List<Flashcard> retrieveAllFlashcards(int userId) {
        List<Flashcard> flashcards = new ArrayList<>();
        String dbPath = "src/main/database/socslingo_database.db";
        String url = "jdbc:sqlite:" + dbPath;
        String query = "SELECT flashcard_id, front_text, back_text FROM user_created_flashcards_table WHERE user_id = ?";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("flashcard_id");
                String front = resultSet.getString("front_text");
                String back = resultSet.getString("back_text");
                flashcards.add(new Flashcard(id, front, back));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return flashcards;
    }
}