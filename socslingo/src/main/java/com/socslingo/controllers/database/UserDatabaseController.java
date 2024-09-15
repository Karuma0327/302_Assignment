package com.socslingo.controllers.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDatabaseController {

    public static boolean insertUser(String username, String email, String hashedPassword) {
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
                pstmt.setString(3, hashedPassword);
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

    public static boolean validateUser(String username, String hashedPassword) {
        String dbPath = "src/main/database/socslingo_database.db";
        String url = "jdbc:sqlite:" + dbPath;

        String sql = "SELECT * FROM user_table WHERE username = ? AND password = ?";

        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            try (Connection conn = DriverManager.getConnection(url);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, hashedPassword);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    return true;
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }


}