package com.socslingo.dao;

import com.socslingo.managers.DatabaseManager;
import com.socslingo.models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object (DAO) class for managing User entities in the database.
 * <p>
 * This class provides methods to insert new users, check for existing usernames,
 * and retrieve users based on their username and password. It interacts with the
 * {@code DatabaseManager} to establish database connections and execute SQL queries.
 * </p>
 * 
 * @author TEAM-SOCSLINGO 
 * @version 1.0
 * @since 2024-09-18
 */
public class UserDAO {
    private DatabaseManager databaseManager;

    /**
     * Constructs a new {@code UserDAO} with the specified {@code DatabaseManager}.
     * 
     * @param databaseManager the manager responsible for handling database connections
     */
    public UserDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Inserts a new user into the database.
     * 
     * @param username       the username of the new user
     * @param email          the email address of the new user
     * @param hashedPassword the hashed password of the new user
     * @return {@code true} if the user was inserted successfully; {@code false} otherwise
     */
    public boolean insertUser(String username, String email, String hashedPassword) {
        String sql = "INSERT INTO user_table(username, email, password) VALUES(?, ?, ?)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, hashedPassword);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * Checks if a username is already taken in the database.
     * 
     * @param username the username to check for availability
     * @return {@code true} if the username is already taken; {@code false} otherwise
     */
    public boolean isUsernameTaken(String username) {
        String sql = "SELECT user_id FROM user_table WHERE username = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            return rs.next(); // Returns true if username exists

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves a user from the database based on username and hashed password.
     * 
     * @param username       the username of the user
     * @param hashedPassword the hashed password of the user
     * @return a {@code User} object if the credentials match an existing user; {@code null} otherwise
     */
    public User getUserByUsernameAndPassword(String username, String hashedPassword) {
        String sql = "SELECT user_id, username, password FROM user_table WHERE username = ? AND password = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String userPassword = rs.getString("password");
                return new User(userId, username, userPassword);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
