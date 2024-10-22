package com.socslingo.dataAccess;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

import com.socslingo.managers.DatabaseManager;
import com.socslingo.models.User;

import org.slf4j.*;

public class UserDataAccess {
    private static final Logger logger = LoggerFactory.getLogger(UserDataAccess.class);

    private DatabaseManager database_manager;

    public UserDataAccess(DatabaseManager database_manager) {
        this.database_manager = database_manager;
        logger.info("UserDataAccess initialized with DatabaseManager");
    }

    public boolean insertUser(String username, String email, String hashed_password, String actual_name) {
        String sql = "INSERT INTO user_table(username, email, password, created_date, actual_name) VALUES(?, ?, ?, ?, ?)";

        logger.debug("Attempting to insert user: username={}, email={}, actual_name={}", username, email, actual_name);
        try (Connection connection = database_manager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, hashed_password);
            preparedStatement.setString(4, LocalDateTime.now().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + LocalDateTime.now().getYear());
            preparedStatement.setString(5, actual_name);
            int rows_affected = preparedStatement.executeUpdate();
            if (rows_affected > 0) {
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

    public boolean isUsernameTaken(String username) {
        String sql = "SELECT user_id FROM user_table WHERE username = ?";

        logger.debug("Checking if username '{}' is taken.", username);
        try (Connection connection = database_manager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();

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

    public User getUserByUsernameAndPassword(String username, String hashed_password) {
        String sql = "SELECT user_id, username, password, created_date, profile_banner_path, email, actual_name FROM user_table WHERE username = ? AND password = ?";
    
        logger.debug("Retrieving user with username='{}' and provided password.", username);
        try (Connection connection = database_manager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
    
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashed_password);
            ResultSet rs = preparedStatement.executeQuery();
    
            if (rs.next()) {
                int user_id = rs.getInt("user_id");
                String user_password = rs.getString("password");
                String created_date_str = rs.getString("created_date");
                String profile_banner_path = rs.getString("profile_banner_path");
                String email = rs.getString("email"); // Retrieve email
                String actual_name = rs.getString("actual_name"); // Retrieve actual name
    
                User user = new User(user_id, username, user_password, created_date_str);
                user.setProfileBannerPath(profile_banner_path);
                user.setEmail(email); // Set email
                user.setActualName(actual_name); // Set actual name
    
                logger.info("User '{}' retrieved successfully with user_id={}.", username, user_id);
                return user;
            } else {
                logger.warn("No user found with username='{}' and provided password.", username);
            }
    
        } catch (SQLException e) {
            logger.error("Error retrieving user '{}': {}", username, e.getMessage(), e);
        }
        return null;
    }
    

    public boolean updateUserProfileBanner(int user_id, String profile_banner_path) {
        String sql = "UPDATE user_table SET profile_banner_path = ? WHERE user_id = ?";

        logger.debug("Updating profile banner for user_id={}", user_id);
        try (Connection connection = database_manager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, profile_banner_path);
            preparedStatement.setInt(2, user_id);

            int rows_affected = preparedStatement.executeUpdate();
            if (rows_affected > 0) {
                logger.info("Profile banner updated successfully for user_id={}.", user_id);
                return true;
            } else {
                logger.warn("No rows affected while updating profile banner for user_id={}.", user_id);
            }

        } catch (SQLException e) {
            logger.error("Error updating profile banner for user_id '{}': {}", user_id, e.getMessage(), e);
        }
        return false;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE user_table SET actual_name = ?, email = ?, password = ?, profile_banner_path = ? WHERE user_id = ?";

        logger.debug("Attempting to update user: user_id={}", user.getId());
        try (Connection connection = database_manager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, user.getActualName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getProfileBannerPath());
            preparedStatement.setInt(5, user.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("User with ID '{}' updated successfully.", user.getId());
                return true;
            } else {
                logger.warn("No rows affected while updating user with ID '{}'.", user.getId());
            }

        } catch (SQLException e) {
            logger.error("Error updating user with ID '{}': {}", user.getId(), e.getMessage(), e);
        }
        return false;
    }
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM user_table WHERE user_id = ?";

        logger.debug("Attempting to delete user: user_id={}", userId);
        try (Connection connection = database_manager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("User with ID '{}' deleted successfully.", userId);
                return true;
            } else {
                logger.warn("No rows affected while deleting user with ID '{}'.", userId);
            }

        } catch (SQLException e) {
            logger.error("Error deleting user with ID '{}': {}", userId, e.getMessage(), e);
        }
        return false;
    }
}
