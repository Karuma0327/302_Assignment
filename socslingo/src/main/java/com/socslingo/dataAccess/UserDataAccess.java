package com.socslingo.dataAccess;

import com.socslingo.managers.DatabaseManager;
import com.socslingo.models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDataAccess {
    private static final Logger logger = LoggerFactory.getLogger(UserDataAccess.class);

    private DatabaseManager databaseManager;

    public UserDataAccess(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        logger.info("UserDataAccess initialized with DatabaseManager");
    }

    /**
     * Inserts a new user into the database.
     *
     * @param username       Username of the new user.
     * @param email          Email of the new user.
     * @param hashedPassword Hashed password of the new user.
     * @return true if insertion is successful, false otherwise.
     */
    public boolean insertUser(String username, String email, String hashedPassword) {
        String sql = "INSERT INTO user_table(username, email, password) VALUES(?, ?, ?)";

        logger.debug("Attempting to insert user: username={}, email={}", username, email);
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, hashedPassword);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("User '{}' inserted successfully.", username);
                return true;
            } else {
                logger.warn("No rows affected while inserting user '{}'.", username);
            }

        } catch (SQLException e) {
            logger.error("Error inserting user '{}': {}", username, e.getMessage(), e);
        }
        return false;
    }

    /**
     * Checks if a username is already taken.
     *
     * @param username Username to check.
     * @return true if username exists, false otherwise.
     */
    public boolean isUsernameTaken(String username) {
        String sql = "SELECT user_id FROM user_table WHERE username = ?";

        logger.debug("Checking if username '{}' is taken.", username);
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            boolean exists = rs.next();
            if (exists) {
                logger.info("Username '{}' is already taken.", username);
            } else {
                logger.info("Username '{}' is available.", username);
            }
            return exists;

        } catch (SQLException e) {
            logger.error("Error checking username '{}': {}", username, e.getMessage(), e);
        }
        return false;
    }

    /**
     * Retrieves a user by username and hashed password.
     *
     * @param username       Username of the user.
     * @param hashedPassword Hashed password of the user.
     * @return User object if credentials are valid, null otherwise.
     */
    public User getUserByUsernameAndPassword(String username, String hashedPassword) {
        String sql = "SELECT user_id, username, password FROM user_table WHERE username = ? AND password = ?";

        logger.debug("Retrieving user with username='{}' and provided password.", username);
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String userPassword = rs.getString("password");
                logger.info("User '{}' retrieved successfully with user_id={}.", username, userId);
                return new User(userId, username, userPassword);
            } else {
                logger.warn("No user found with username='{}' and provided password.", username);
            }

        } catch (SQLException e) {
            logger.error("Error retrieving user '{}': {}", username, e.getMessage(), e);
        }
        return null;
    }

    // You can add more methods with similar logging as needed
}
